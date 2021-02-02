package com.example.tingzapp.network

import androidx.lifecycle.LiveData
import com.example.tingzapp.network.response.ListResponse
import com.example.tingzapp.utils.GenericApiResponse
import retrofit2.http.GET

interface ApiService{
    @GET("movies.json")
    fun getMovies(): LiveData<GenericApiResponse<ListResponse>>
}