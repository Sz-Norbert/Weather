package com.nika.simpleweather.mvvm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class WeatherViewModelProviderFactory(
    val weatherRepository:WeatherRepository
): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        return WeatherViewModel(weatherRepository) as T
    }

}