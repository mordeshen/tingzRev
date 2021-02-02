package com.example.tingzapp.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.tingzapp.model.MovieModel
import com.example.tingzapp.utils.Converters

@Database(entities = [MovieModel::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getMainDao(): MainDao

    companion object {
        const val DATABASE_NAME = "app_db"
    }
}