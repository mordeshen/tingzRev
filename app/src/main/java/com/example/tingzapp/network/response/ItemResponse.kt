package com.example.tingzapp.network.response

import com.example.tingzapp.model.MovieModel
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ItemResponse(
    @SerializedName("title")
    @Expose
    var title: String? = null,

    @SerializedName("image")
    @Expose
    var image: String? = null,

    @SerializedName("rating")
    @Expose
    var rating: Double? = null,

    @SerializedName("releaseYear")
    @Expose
    var releaseYear: Int? = null,

    @SerializedName("genre")
    @Expose
    var genre: List<String>? = listOf()
) {
    fun toMovie(): MovieModel {
        return MovieModel(
            title = title,
            image = image,
            rating = rating,
            releaseYear = releaseYear,
            genre = genre
        )
    }
}