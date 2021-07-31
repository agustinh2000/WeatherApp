package com.weatherapp.weatherapp.api.model.weather

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class DailyWeatherResponse (
    @SerializedName("dt")
    val dateTime: Number,
    @SerializedName("temp")
    val temperatureInfo: TemperatureResponse,
    @SerializedName("weather")
    val weatherDescription: WeatherDescriptionResponse
): Parcelable
