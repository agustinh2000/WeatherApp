package com.weatherapp.weatherapp.repository.interfaces

import com.weatherapp.weatherapp.api.model.weather.WeatherResponse
import retrofit2.Response

interface IWeatherRepository {
    suspend fun getWeather(lat: String, lon:String, units:String, exclude: String, appId: String): Response<WeatherResponse>
}