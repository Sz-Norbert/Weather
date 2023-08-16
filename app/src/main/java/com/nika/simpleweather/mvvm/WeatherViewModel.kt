package com.nika.simpleweather.mvvm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nika.simpleweather.constants.Util.Companion.API_KEY
import com.nika.simpleweather.constants.Util.Companion.UNIT
import com.nika.simpleweather.repository.current.CurrentWeatherResponse
import com.nika.simpleweather.repository.current.ForecastResponse
import kotlinx.coroutines.launch
import retrofit2.Response

class WeatherViewModel (val weatherRepository: WeatherRepository) : ViewModel() {

    val currentWeather:MutableLiveData<Resource<CurrentWeatherResponse>> = MutableLiveData()
    val forecastWeather:MutableLiveData<Resource<ForecastResponse>> = MutableLiveData()
    var currentWeatherResponse: CurrentWeatherResponse?= null
    var forecastResponse:ForecastResponse?=null
    val searchedWeather:MutableLiveData<Resource<ForecastResponse>> = MutableLiveData()


    fun getCurrentWeather(lat:Double,lon:Double)=viewModelScope.launch {
        val response=weatherRepository.getCurrentWeathert(lat,lon, API_KEY,UNIT)
        currentWeather.postValue(handleCurrentWeatherResponse(response))
    }

    private fun handleCurrentWeatherResponse(response: Response<CurrentWeatherResponse>): Resource<CurrentWeatherResponse>? {

        if (response.isSuccessful){

           response.body().let {resultResponse->
               currentWeatherResponse=resultResponse
               return (currentWeatherResponse ?: resultResponse)?.let { Resource.Success(it) }!!

           }

        }
        return Resource.Error(response.message())
    }

    fun getForecast(city:String)=viewModelScope.launch {

        val response=weatherRepository.getForecast(city)
        forecastWeather.postValue(handleForecastResponse(response))
    }

    fun getForSearch(city: String, date:String)=viewModelScope.launch {
        val response=weatherRepository.getForSearch(city, date)
        searchedWeather.postValue(handleForSearchResponse(response))
    }

    private fun handleForecastResponse(response: Response<ForecastResponse>): Resource<ForecastResponse>? {
        if ( response.isSuccessful){
            response.body().let {
                forecastResponse=it
                return (forecastResponse ?: it)?.let { Resource.Success(it) }

            }
        }
        return Resource.Error(response.message())
    }


    private fun handleForSearchResponse(response: Response<ForecastResponse>): Resource<ForecastResponse>? {
        if ( response.isSuccessful){
            response.body().let {
                forecastResponse=it
                return (forecastResponse ?: it)?.let { Resource.Success(it) }

            }
        }
        return Resource.Error(response.message())
    }



}