package com.weatherapp.weatherapp.ui.home.model

data class Parameter (
    var longitude: Float = -58.383716f,
    var latitude: Float = -34.603686f,
    var excludeResponse: String = "hourly,minutely"
)