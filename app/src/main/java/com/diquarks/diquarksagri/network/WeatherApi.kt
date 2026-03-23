package com.diquarks.diquarksagri.network

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET("v1/forecast.json")
    fun getWeather(
        @Query("key") apiKey: String,
        @Query("q") location: String,
        @Query("days") days: Int = 7
    ): Call<WeatherResponse>

}