package com.weatherapp.weatherapp.api.model.weather

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class CurrentWeatherResponse(
    @SerializedName("dt")
    val dateTime: Long,
    @SerializedName("sunrise")
    val sunrise: Long,
    @SerializedName("sunset")
    val sunset: Long,
    @SerializedName("temp")
    val temperature: Double,
    @SerializedName("pressure")
    val pressure: Number,
    @SerializedName("humidity")
    val humidity: Number,
    @SerializedName("wind_speed")
    val windSpeed: Double,
    @SerializedName("weather")
    val weatherDescription: Array<WeatherDescriptionResponse>
):Parcelable