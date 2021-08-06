package com.weatherapp.weatherapp.ui.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import com.weatherapp.weatherapp.R
import com.weatherapp.weatherapp.api.Constants.Companion.DATE_PATTERN_FOR_WEEKLY_WEATHER
import com.weatherapp.weatherapp.api.Constants.Companion.TEMPERATURE_UNIT
import com.weatherapp.weatherapp.api.Constants.Companion.URL_TO_GET_ICON
import com.weatherapp.weatherapp.api.Constants.Companion.WEATHER_ICON_EXTENSION
import com.weatherapp.weatherapp.api.model.weather.DailyWeatherResponse
import com.weatherapp.weatherapp.ui.helpers.BaseViewHolder
import java.text.SimpleDateFormat
import java.util.*

class WeatherAdapter(
    dailyWeatherList: Array<DailyWeatherResponse>,
) :
    GenericAdapter<DailyWeatherResponse>(dailyWeatherList) {

    inner class WeatherHolder(view: View) : BaseViewHolder<DailyWeatherResponse>(view) {
        var imageViewWeatherIcon: ImageView = view.findViewById(R.id.ivWeather)
        var textViewDay: TextView = view.findViewById(R.id.tvDay)
        var textViewMinTemperature: TextView = view.findViewById(R.id.tvMinTemperature)
        var textViewMaxTemperature: TextView = view.findViewById(R.id.tvMaxTemperature)


        override fun render(weather: DailyWeatherResponse) {
            val weatherIconCode = weather.weatherDescription[0].icon
            val url = "$URL_TO_GET_ICON$weatherIconCode$WEATHER_ICON_EXTENSION"
            val minTemperature = weather.temperatureInfo.minTemperature.toInt().toString()
            val maxTemperature = weather.temperatureInfo.maxTemperature.toInt().toString()
            val date = weather.dateTime
            Picasso.get().load(url).into(imageViewWeatherIcon)
            textViewMinTemperature.text = "$minTemperature $TEMPERATURE_UNIT"
            textViewMaxTemperature.text = "$maxTemperature $TEMPERATURE_UNIT"
            textViewDay.text =
                SimpleDateFormat(DATE_PATTERN_FOR_WEEKLY_WEATHER, Locale.ENGLISH).format(Date(date * 1000))
        }
    }

    override fun setViewHolder(parent: ViewGroup): BaseViewHolder<DailyWeatherResponse> {
        val layoutInflater = LayoutInflater.from(parent.context)
        return WeatherHolder(layoutInflater.inflate(R.layout.item_weather, parent, false))
    }
}