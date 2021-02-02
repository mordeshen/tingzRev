package com.e.msappsreview.ui.main.state

sealed class MainStateEvent {
    class GetMainMoviesEvent : MainStateEvent()
    class None : MainStateEvent()
}
