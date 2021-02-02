package com.example.tingzapp.ui.main.viewModel

import com.example.tingzapp.model.MovieModel

fun MainViewModel.setMovieList(movieList: List<MovieModel>) {
    val update = getCurrentViewStateOrNew()
    update.listFields.movies = movieList
    setViewState(update)
}

fun MainViewModel.setMovieItem(movie: MovieModel) {
    val update = getCurrentViewStateOrNew()
    update.itemFields.movie = movie
    setViewState(update)
}
