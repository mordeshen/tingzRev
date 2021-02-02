package com.example.tingzapp.ui

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.tingzapp.ui.main.BaseMainFragment
import com.example.tingzapp.utils.SessionManager
import dagger.hilt.DefineComponent
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

abstract class BaseActivity : AppCompatActivity(), DataStateChangeListener {
    private val TAG: String = "AppDebug"


    override fun onDataStateChange(dataState: DataState<*>?) {
        dataState?.let {
            GlobalScope.launch(Main) {
                displayProgressBar(it.loading.isLoading)
            }

            it.error?.let { errorEvent ->
                handleStateError(errorEvent)
            }

            it.data?.let {
                it.response?.let { responseEvent ->
                    handleStateResponse(responseEvent)
                }
            }
        }
    }

    private fun handleStateResponse(event: Event<Response>) {
        event.getContentIfNotHandled()?.let {
            when (it.responseType) {
                is ResponseType.Toast -> {
                    it.message?.let { message ->
                        displayToast(message)
                    }
                }
                is ResponseType.Dialog -> {
                    it.message?.let { message ->
                        displaySuccessDialog(message)
                    }
                }
                is ResponseType.None -> {
                    Log.e(TAG, "handleStateResponse: ${it.message}")
                }
            }
        }
    }

    private fun handleStateError(event: Event<StateError>) {
        event.getContentIfNotHandled()?.let {
            when (it.response.responseType) {
                is ResponseType.Toast -> {
                    it.response.message?.let { message ->
                        displayToast(message)
                    }
                }
                is ResponseType.Dialog -> {
                    it.response.message?.let { message ->
                        displayErrorDialog(message)
                    }
                }
                is ResponseType.None -> {
                    Log.e(TAG, "handleStateError: ${it.response.message}")
                }
            }
        }
    }

    abstract fun displayProgressBar(boolean: Boolean)
}