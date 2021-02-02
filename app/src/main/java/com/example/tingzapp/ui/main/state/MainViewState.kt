package com.example.tingzapp.ui.main.state

import com.example.tingzapp.model.MovieModel
class MainViewState(

    var listFields: ListFields = ListFields(),
    var itemFields: ItemFields = ItemFields()

) {
    data class ListFields(
        var movies: List<MovieModel>? = null
    )

    data class ItemFields(
        var movie: MovieModel? = null
    )
}