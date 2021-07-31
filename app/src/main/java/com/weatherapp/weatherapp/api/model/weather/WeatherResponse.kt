package com.weatherapp.weatherapp.api.model.weather

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class WeatherResponse(
    @SerializedName("timezone")
    val timeZone: String,

): Parcelable