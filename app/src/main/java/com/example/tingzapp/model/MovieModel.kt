package com.example.tingzapp.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName


@Entity(tableName = "movies")
data class MovieModel(

    @PrimaryKey
    @ColumnInfo
    var pk: Int? = null,

    @SerializedName("title")
    @ColumnInfo
    var title: String? = null,

    @SerializedName("image")
    @ColumnInfo
    var image: String? = null,

    @SerializedName("rating")
    @ColumnInfo
    var rating: Double? = null,

    @SerializedName("releaseYear")
    @ColumnInfo
    var releaseYear: Int? = null,

    @SerializedName("genre")
    @ColumnInfo
    var genre: List<String>? = listOf()
)
