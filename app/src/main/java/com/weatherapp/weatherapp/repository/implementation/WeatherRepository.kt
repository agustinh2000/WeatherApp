package com.weatherapp.weatherapp.repository.implementation

import android.util.Log
import com.weatherapp.weatherapp.api.RetrofitInstance
import com.weatherapp.weatherapp.api.model.weather.WeatherResponse
import com.weatherapp.weatherapp.repository.interfaces.IWeatherRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response

class WeatherRepository : IWeatherRepository {
    override suspend fun getWeather(lat: String, lon:String, units: String, appId: String): Response<WeatherResponse> {
        return try {
            RetrofitInstance.weatherEndpoints.getWeather(lat, lon, units, appId)
        } catch (ex: Exception) {
            Log.e("Exception on getWeather", ex.message!!)
            Response.error(500, "SERVER_ERROR".toResponseBody("text/plain".toMediaTypeOrNull()))
        }
    }
}