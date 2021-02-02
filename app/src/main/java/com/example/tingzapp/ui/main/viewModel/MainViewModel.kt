package com.example.tingzapp.ui.main.viewModel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import com.bumptech.glide.RequestManager
import com.e.msappsreview.ui.main.state.MainStateEvent
import com.example.tingzapp.repository.MainRepository
import com.example.tingzapp.ui.BaseViewModel
import com.example.tingzapp.ui.DataState
import com.example.tingzapp.ui.main.state.MainViewState
import com.example.tingzapp.utils.AbsentLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.InternalCoroutinesApi
import javax.inject.Inject
@HiltViewModel
class MainViewModel
@Inject
constructor(
    private val mainRepository: MainRepository,
    private val requestManager: RequestManager
) : BaseViewModel<MainStateEvent, MainViewState>() {
    @OptIn(InternalCoroutinesApi::class)
    override fun handleStateEvent(stateEvent: MainStateEvent): LiveData<DataState<MainViewState>> {
        return when (stateEvent) {
            is MainStateEvent.GetMainMoviesEvent -> {
                mainRepository.getMovies()
            }
            is MainStateEvent.None -> {
                AbsentLiveData.create()
            }
        }
    }

    override fun initNewViewState(): MainViewState {
        return MainViewState()
    }

    fun cancelActiveJobs() {
        mainRepository.cancelActiveJobs()
        handlePendingData()
    }

    private fun handlePendingData() {
        setStateEvent(MainStateEvent.None())
    }

    override fun onCleared() {
        super.onCleared()
        cancelActiveJobs()
    }

}