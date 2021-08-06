package com.weatherapp.weatherapp.ui.weatherMap.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import com.weatherapp.weatherapp.R
import com.weatherapp.weatherapp.api.Constants.Companion.HOUR_PATTERN
import com.weatherapp.weatherapp.api.Constants.Companion.TEMPERATURE_UNIT
import com.weatherapp.weatherapp.api.Constants.Companion.URL_TO_GET_ICON
import com.weatherapp.weatherapp.api.Constants.Companion.WEATHER_ICON_EXTENSION
import com.weatherapp.weatherapp.api.model.weather.HourlyWeatherResponse
import com.weatherapp.weatherapp.ui.helpers.BaseViewHolder
import com.weatherapp.weatherapp.ui.home.adapter.GenericAdapter
import java.text.SimpleDateFormat
import java.util.*

class WeatherMapAdapter(
    hourlyWeatherList: Array<HourlyWeatherResponse>,
) :
    GenericAdapter<HourlyWeatherResponse>(hourlyWeatherList) {

    inner class WeatherMapHolder(view: View) : BaseViewHolder<HourlyWeatherResponse>(view) {
        var imageViewWeatherIcon: ImageView = view.findViewById(R.id.ivIcon)
        var textViewHour: TextView = view.findViewById(R.id.tvHour)
        var textViewCurrentTemperature: TextView = view.findViewById(R.id.tvCurrentTemperature)

        override fun render(weather: HourlyWeatherResponse) {
            val weatherIconCode = weather.weatherDescription[0].icon
            val urlToGetImage = "$URL_TO_GET_ICON$weatherIconCode$WEATHER_ICON_EXTENSION"
            val currentTemperature = weather.temperature.toInt()
            val date = weather.dateTime
            Picasso.get().load(urlToGetImage).into(imageViewWeatherIcon)
            textViewCurrentTemperature.text = "$currentTemperature $TEMPERATURE_UNIT"
            textViewHour.text =
                SimpleDateFormat(HOUR_PATTERN, Locale.ENGLISH).format(Date(date * 1000))
        }
    }

    override fun setViewHolder(parent: ViewGroup): BaseViewHolder<HourlyWeatherResponse> {
        val layoutInflater = LayoutInflater.from(parent.context)
        return WeatherMapHolder(layoutInflater.inflate(R.layout.item_daily_weather, parent, false))
    }
}