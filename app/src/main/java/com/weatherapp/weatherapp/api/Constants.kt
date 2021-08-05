package com.weatherapp.weatherapp.api

class Constants {
    companion object {
        const val BASE_URL = "https://api.openweathermap.org/data/2.5/"
        const val API_KEY = "f2514a4cdd5cc64ec0cc132d01ad0af6"
        const val DEFAULT_UNIT = "metric"
        const val TEMPERATURE_UNIT = "°C"
        const val DATE_PATTERN = "EEE, d MMM yyyy"
        const val DATE_PATTERN_FOR_WEEKLY_WEATHER = "EEEE, d"
        const val ZONE_DELIMITER = "/"
        const val HOUR_PATTERN = "hh:mm a"
        const val URL_TO_GET_ICON = "https://openweathermap.org/img/w/"
        const val WEATHER_ICON_EXTENSION = ".png"
        const val HUMIDITY_UNIT = "%"
        const val WIND_SPEED_UNIT = "m/s"
        const val PRESSURE_UNIT = "hPa"
    }
}
