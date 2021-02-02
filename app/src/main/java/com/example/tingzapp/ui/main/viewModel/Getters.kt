package com.example.tingzapp.ui.main.viewModel

import com.example.tingzapp.model.MovieModel

fun MainViewModel.getMovie():MovieModel{
    val update = getCurrentViewStateOrNew()
    update.itemFields.movie?.let {
        return it
    }
    return MovieModel()
}