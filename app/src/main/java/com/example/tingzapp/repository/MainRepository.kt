package com.example.tingzapp.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.switchMap
import com.example.tingzapp.network.ApiService
import com.example.tingzapp.network.response.ListResponse
import com.example.tingzapp.model.MovieModel
import com.example.tingzapp.db.MainDao
import com.example.tingzapp.utils.SessionManager
import com.example.tingzapp.ui.DataState
import com.example.tingzapp.ui.main.state.MainViewState
import com.example.tingzapp.utils.GenericApiResponse
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import javax.inject.Inject

class MainRepository
@Inject
constructor(
    val apiMainService: ApiService,
    val mainDao: MainDao,
    val sessionManager: SessionManager
) : JobManager("AccountRepository") {
    private val TAG = "AccountRepository"

    private var repositoryJob: Job? = null

    @InternalCoroutinesApi
    fun getMovies(): LiveData<DataState<MainViewState>> {
        return object : NetworkBoundResource<ListResponse, List<MovieModel>, MainViewState>(
            sessionManager.isConnectedToInternet(),
            true,
            true,
            true
        ) {
            override fun createCall(): LiveData<GenericApiResponse<ListResponse>> {
                return apiMainService.getMovies()
            }

            override fun setJob(job: Job) {
                addJob("AccountRepository", job)
            }

            override suspend fun createCacheRequestAndReturn() {
                withContext(Main) {
                    result.addSource(loadFromCache()) { viewState ->
                        onCompleteJob(
                            DataState.data(
                                data = viewState,
                                response = null
                            )
                        )

                    }
                }
            }

            override fun loadFromCache(): LiveData<MainViewState> {
                return mainDao.getMovies()
                    .switchMap {
                        object : LiveData<MainViewState>() {
                            override fun onActive() {
                                super.onActive()
                                value = MainViewState(
                                    MainViewState.ListFields(
                                        movies = it
                                    )
                                )
                            }
                        }

                    }

            }


            override suspend fun handleApiSuccessResponse(response: GenericApiResponse.ApiSuccessResponse<ListResponse>) {
                updateLocalDb(response.body.movieItemsToList())
                createCacheRequestAndReturn()
            }

            override suspend fun updateLocalDb(cacheObject: List<MovieModel>?) {
                if (cacheObject != null) {
                    withContext(Dispatchers.IO) {
                        for (movie in cacheObject) {
                            try {
                                launch {
                                    Log.d(TAG, "updateLocalDb: inserting movie: $movie")
                                    mainDao.insertAndReplace(movie)
                                }

                            } catch (e: Exception) {
                                Log.e(
                                    TAG, "updateLocalDb: error updating cache" +
                                            "on movie post with title: ${movie.title}"
                                )
                            }
                        }
                    }
                }
            }

        }.asLiveData()
    }

}
