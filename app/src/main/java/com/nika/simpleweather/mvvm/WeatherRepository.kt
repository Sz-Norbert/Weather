package com.nika.simpleweather.mvvm

import com.nika.simpleweather.retrofit.RetrofitInstance

class WeatherRepository() {

    suspend fun getCurrentWeathert(lat:Double,lon:Double,api_key:String, unit:String)=
        RetrofitInstance.currentApi.getCurrentWeather(lat,lon,api_key,unit)

    suspend fun getForecast(city:String)=
        RetrofitInstance.forecastApi.getForecast(city)

    suspend fun getForSearch(city: String,date:String)=
        RetrofitInstance.forecastApi.getForSearch(city  , date = date)
}