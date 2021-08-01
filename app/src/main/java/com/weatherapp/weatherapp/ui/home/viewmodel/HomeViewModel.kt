package com.weatherapp.weatherapp.ui.home.viewmodel

import androidx.lifecycle.*
import com.weatherapp.weatherapp.api.Constants.Companion.API_KEY
import com.weatherapp.weatherapp.api.Constants.Companion.DEFAULT_UNIT
import com.weatherapp.weatherapp.repository.interfaces.IWeatherRepository
import com.weatherapp.weatherapp.ui.home.model.Parameter
import kotlinx.coroutines.Dispatchers

class HomeViewModel(private val weatherRepo: IWeatherRepository): ViewModel(){

    private val weatherData = MutableLiveData<Parameter>()

    fun setParameters(params: Parameter){
        weatherData.value = params
    }

    val fetchWeather = weatherData.switchMap { weatherData ->
        liveData(context = viewModelScope.coroutineContext + Dispatchers.IO) {
            emit(weatherRepo.getWeather(weatherData.latitude.toString(), weatherData.longitude.toString(), DEFAULT_UNIT, weatherData.excludeResponse, API_KEY))
        }
    }
}