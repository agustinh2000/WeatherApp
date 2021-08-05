package com.weatherapp.weatherapp.ui.weatherMap.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import com.weatherapp.weatherapp.R
import com.weatherapp.weatherapp.api.model.weather.HourlyWeatherResponse
import com.weatherapp.weatherapp.ui.helpers.BaseViewHolder
import com.weatherapp.weatherapp.ui.home.adapter.GenericAdapter
import java.text.SimpleDateFormat
import java.util.*

class WeatherMapAdapter(
    private val hourlyWeatherList: Array<HourlyWeatherResponse>,
) :
    GenericAdapter<HourlyWeatherResponse>(hourlyWeatherList) {

    inner class WeatherMapHolder(val view: View) : BaseViewHolder<HourlyWeatherResponse>(view) {
        var imageViewWeatherIcon: ImageView = view.findViewById(R.id.ivIcon)
        var textViewHour: TextView = view.findViewById(R.id.tvHour)
        var textViewCurrentTemperature: TextView = view.findViewById(R.id.tvCurrentTemperature)


        override fun render(weather: HourlyWeatherResponse) {
            val icon = weather.weatherDescription[0].icon
            val url = "https://openweathermap.org/img/w/$icon.png"
            val currentTemperature = weather.temperature.toInt()
            val date = weather.dateTime
            Picasso.get().load(url).into(imageViewWeatherIcon)
            textViewCurrentTemperature.text = "$currentTemperature °C"
            textViewHour.text =
                SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(date * 1000))
        }
    }

    override fun setViewHolder(parent: ViewGroup): BaseViewHolder<HourlyWeatherResponse> {
        val layoutInflater = LayoutInflater.from(parent.context)
        return WeatherMapHolder(layoutInflater.inflate(R.layout.item_daily_weather, parent, false))
    }
}