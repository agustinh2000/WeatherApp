package com.weatherapp.weatherapp.api

import com.weatherapp.weatherapp.api.Constants.Companion.BASE_URL
import com.weatherapp.weatherapp.api.endpoints.WeatherWebService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private val retrofit by lazy{
        Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val weatherEndpoints: WeatherWebService by lazy {
        retrofit.create(WeatherWebService::class.java)
    }

}