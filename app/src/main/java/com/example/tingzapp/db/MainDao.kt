package com.example.tingzapp.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.tingzapp.model.MovieModel

@Dao
interface MainDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAndReplace(movieModel: MovieModel): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAndIgnore(movieModel: MovieModel): Long

    @Query("SELECT * FROM movies ORDER BY releaseYear ")
    fun getMovies(): LiveData<List<MovieModel>>

    @Query("UPDATE  movies SET title =:title, image =:image, rating =:rating, releaseYear =:releaseYear, genre =:genre  WHERE pk = :pk ")
    fun updateMovie(
        pk: Int,
        title: String,
        image: String,
        rating: Double,
        releaseYear: Int,
        genre: List<String>
    )

    @Query("SELECT * FROM movies WHERE title = :title")
    fun searchByTitle(title: String): LiveData<MovieModel>
}
