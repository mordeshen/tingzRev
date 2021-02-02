package com.example.tingzapp.network.response

import com.example.tingzapp.model.MovieModel
import com.google.gson.annotations.Expose

class ListResponse(
    @Expose
    var movies: List<ItemResponse>
) {
    fun movieItemsToList(): List<MovieModel> {
        val movieList: ArrayList<MovieModel> = ArrayList()
        for (movieItem in movies) {
            movieList.add(movieItem.toMovie())
        }
        return movieList
    }
}