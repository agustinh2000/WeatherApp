package com.weatherapp.weatherapp.api.model.weather

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class CurrentWeatherResponse(
    @SerializedName("dt")
    val dateTime: Number,
    @SerializedName("sunrise")
    val sunrise: Number,
    @SerializedName("sunset")
    val sunset: Number,
):Parcelable