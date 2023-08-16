package com.nika.simpleweather.retrofit

import com.nika.simpleweather.constants.Util.Companion.X_RAPIDAPI_KEY
import com.nika.simpleweather.constants.Util.Companion.X_RapidAPI_HOST
import com.nika.simpleweather.repository.current.CurrentWeatherResponse
import com.nika.simpleweather.repository.current.ForecastResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface WeatherApi {

    @GET("weather")
    suspend fun getCurrentWeather(
        @Query("lat")
        lat:Double,
        @Query("lon")
        lon:Double,
        @Query("appid")
        api_key:String,
        @Query("units")
        unit:String
    ) :Response<CurrentWeatherResponse>

    @GET("forecast.json")
    suspend fun getForecast(
        @Query("q")
        city:String,
        @Header("X-RapidAPI-Key") apiKey: String= X_RAPIDAPI_KEY,
        @Header("X-RapidAPI-Host") apiHost: String=X_RapidAPI_HOST ,
        @Query("days")
        days:Int=1
    ) : Response<ForecastResponse>


    @GET("forecast.json")
    suspend fun getForSearch(
        @Query("q")
        city:String,
        @Query("days")
        days:Int=1,
        @Query("dt")
        date:String,
        @Header("X-RapidAPI-Key") apiKey: String= X_RAPIDAPI_KEY,
        @Header("X-RapidAPI-Host") apiHost: String=X_RapidAPI_HOST

    ) : Response<ForecastResponse>
}