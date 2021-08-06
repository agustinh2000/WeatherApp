package com.weatherapp.weatherapp.ui.weatherMap.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.weatherapp.weatherapp.repository.interfaces.IWeatherRepository

class WeatherMapViewModelFactory(private val weatherRepo: IWeatherRepository) : ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return WeatherMapViewModel(weatherRepo) as T
    }
}