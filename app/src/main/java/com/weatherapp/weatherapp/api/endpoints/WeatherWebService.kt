package com.weatherapp.weatherapp.api.endpoints

import com.weatherapp.weatherapp.api.model.weather.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherWebService {
    @GET("onecall?")
    suspend fun getWeather(@Query ("lat") lat: String, @Query ("lon") lon: String,
                                 @Query("units") units:String, @Query ("exclude") exclude: String,
                           @Query("appid") appId: String): Response <WeatherResponse>
}