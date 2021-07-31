package com.weatherapp.weatherapp.api.endpoints

import com.weatherapp.weatherapp.api.model.weather.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherWebService {
    @GET()
    suspend fun getWeather(@Query ("lat") lat: Float, @Query ("lon") lon: Float
                                 , @Query ("exclude") exclude: String,
                           @Query("appid") appId: String): Response <WeatherResponse>
}