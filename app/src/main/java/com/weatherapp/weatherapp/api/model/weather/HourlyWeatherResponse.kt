package com.weatherapp.weatherapp.api.model.weather

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class HourlyWeatherResponse (
    @SerializedName("dt")
    val dateTime: Long,
    @SerializedName("temp")
    val temperature: Double,
    @SerializedName("weather")
    val weatherDescription: Array<WeatherDescriptionResponse>
): Parcelable
