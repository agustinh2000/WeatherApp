package com.weatherapp.weatherapp.api.model.weather

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class TemperatureResponse(
   @SerializedName("min")
   val minTemperature: Double,
   @SerializedName("max")
   val maxTemperature: Double,
):Parcelable