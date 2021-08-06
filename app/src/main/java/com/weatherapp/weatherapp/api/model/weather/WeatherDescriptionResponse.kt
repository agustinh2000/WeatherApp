package com.weatherapp.weatherapp.api.model.weather

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class WeatherDescriptionResponse(
    @SerializedName("main")
    val mainDescription: String,
    @SerializedName("description")
    val detailedDescription: String,
    @SerializedName("icon")
    val icon: String
): Parcelable