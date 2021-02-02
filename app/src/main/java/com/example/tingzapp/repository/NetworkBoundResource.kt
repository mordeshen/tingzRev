package com.example.tingzapp.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.example.tingzapp.ui.DataState
import com.example.tingzapp.ui.Response
import com.example.tingzapp.ui.ResponseType
import com.example.tingzapp.utils.Constants.NETWORK_TIMEOUT
import com.example.tingzapp.utils.Constants.TESTING_NETWORK_DELAY
import com.example.tingzapp.utils.ErrorHandling
import com.example.tingzapp.utils.ErrorHandling.Companion.ERROR_CHECK_NETWORK_CONNECTION
import com.example.tingzapp.utils.ErrorHandling.Companion.ERROR_UNKNOWN
import com.example.tingzapp.utils.ErrorHandling.Companion.UNABLE_TODO_OPERATION_WO_INTERNET
import com.example.tingzapp.utils.ErrorHandling.Companion.UNABLE_TO_RESOLVE_HOST
import com.example.tingzapp.utils.GenericApiResponse
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main

@InternalCoroutinesApi
abstract class NetworkBoundResource<ResponseObject, CacheObject, ViewStateType>
    (
    isNetworkAvailable: Boolean,// is there a network connection
    isNetworkRequest: Boolean, //is this a network request
    shouldCancelIfNotInternet: Boolean, //should this job be cancelled
    shouldLoadFromCache: Boolean//should the cached data be loaded
) {
    private val TAG = "NetworkBoundResource"

    protected val result = MediatorLiveData<DataState<ViewStateType>>()
    protected lateinit var job: CompletableJob
    protected lateinit var coroutineScope: CoroutineScope

    init {
        setJob(initNewJob())
        setValue(DataState.loading(isLoading = true, cachedData = null))

        if (shouldLoadFromCache) {
            val dbSource = loadFromCache()
            result.addSource(dbSource) {
                result.removeSource(dbSource)
                setValue(DataState.loading(isLoading = true, cachedData = it))
            }
        }

        if (isNetworkRequest) {
            if (isNetworkAvailable) {
                doNetworkRequest()
            } else {
                if (shouldCancelIfNotInternet) {
                    onErrorReturn(
                        UNABLE_TODO_OPERATION_WO_INTERNET,
                        shouldUseDialog = true,
                        shouldUseToast = false
                    )
                } else {
                    doCacheRequest()
                }
            }
        } else {
            doCacheRequest()
        }
    }

    private fun doCacheRequest() {
        coroutineScope.launch {
            delay(TESTING_NETWORK_DELAY)
            createCacheRequestAndReturn()
        }
    }

    private fun doNetworkRequest() {
        coroutineScope.launch {
            delay(TESTING_NETWORK_DELAY)
            withContext(Main) {
                val apiResponse = createCall()
                result.addSource(apiResponse) { response ->
                    result.removeSource(apiResponse)

                    coroutineScope.launch {
                        handleNetworkCall(response)
                    }


                }
            }

        }
        GlobalScope.launch(IO) {
            delay(NETWORK_TIMEOUT)
            if (!job.complete()) {
                Log.e(TAG, "NetworkBoundResource: JOB NETWORK TIMEOUT.")
                job.cancel(CancellationException(UNABLE_TO_RESOLVE_HOST))
            }
        }
    }

    suspend fun handleNetworkCall(response: GenericApiResponse<ResponseObject>) {
        when (response) {
            is GenericApiResponse.ApiSuccessResponse -> {
                handleApiSuccessResponse(response)
            }
            is GenericApiResponse.ApiErrorResponse -> {
                Log.e(TAG, "handleNetworkCall:  ${response.errorMessage}")
                onErrorReturn(response.errorMessage, true, false)
            }
            is GenericApiResponse.ApiEmptyResponse -> {
                Log.e(TAG, "handleNetworkCall: Request return NOTHING (HTTP 204)")
                onErrorReturn(ERROR_UNKNOWN, true, false)
            }
        }

    }

    fun onCompleteJob(dataState: DataState<ViewStateType>) {
        GlobalScope.launch(Main) {
            job.complete()
            setValue(dataState)
        }
    }

    private fun setValue(dataState: DataState<ViewStateType>) {
        result.value = dataState
    }

    fun onErrorReturn(errorMessage: String?, shouldUseDialog: Boolean, shouldUseToast: Boolean) {
        var msg = errorMessage
        var useDialog = shouldUseDialog
        var responseType: ResponseType = ResponseType.None()

        if (msg == null) {
            msg = ERROR_UNKNOWN
        } else if (ErrorHandling.isNetworkError(msg)) {
            msg = ERROR_CHECK_NETWORK_CONNECTION
            useDialog = false
        }
        if (shouldUseToast) {
            responseType = ResponseType.Toast()
        }
        if (useDialog) {
            responseType = ResponseType.Dialog()
        }
        onCompleteJob(
            DataState.error(
                response = Response(
                    message = msg,
                    responseType = responseType
                )
            )
        )
    }

    @InternalCoroutinesApi
    private fun initNewJob(): Job {
        Log.d(TAG, "initNewJob: called")
        job = Job()
        job.invokeOnCompletion(
            onCancelling = true,
            invokeImmediately = true,
            handler = object : CompletionHandler {
                override fun invoke(cause: Throwable?) {
                    if (job.isCancelled) {
                        Log.e(TAG, "invoke: NetworkBoundResource: job has been cancelled")
                        cause?.let {
                            onErrorReturn(it.message, false, true)
                        } ?: onErrorReturn(ERROR_UNKNOWN, false, true)
                    } else if (job.isCompleted) {
                        Log.e(TAG, "invoke: NetworkBoundResource: job has been completed")
                        //Do nothing. should be handled already.
                    }
                }

            })
        coroutineScope = CoroutineScope(IO + job)
        return job
    }

    fun asLiveData() = result as LiveData<DataState<ViewStateType>>
    abstract suspend fun handleApiSuccessResponse(response: GenericApiResponse.ApiSuccessResponse<ResponseObject>)
    abstract fun createCall(): LiveData<GenericApiResponse<ResponseObject>>
    abstract fun setJob(job: Job)
    abstract fun loadFromCache(): LiveData<ViewStateType>
    abstract suspend fun updateLocalDb(cacheObject: CacheObject?)
    abstract suspend fun createCacheRequestAndReturn()
}