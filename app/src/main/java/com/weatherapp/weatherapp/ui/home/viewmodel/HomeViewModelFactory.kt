package com.weatherapp.weatherapp.ui.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.weatherapp.weatherapp.repository.interfaces.IWeatherRepository

class HomeViewModelFactory(private val weatherRepo: IWeatherRepository) : ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return HomeViewModel(weatherRepo) as T
    }
}