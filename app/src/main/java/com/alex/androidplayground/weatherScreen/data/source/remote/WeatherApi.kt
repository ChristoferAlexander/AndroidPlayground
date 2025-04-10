package com.alex.androidplayground.weatherScreen.data.source.remote

import com.alex.androidplayground.weatherScreen.data.source.remote.model.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("v1/forecast")
    suspend fun getCurrentWeather(
        @Query("latitude") latitude: Float,
        @Query("longitude") longitude: Float,
        @Query("current_weather") currentWeather: Boolean = true
    ): Response<WeatherResponse>

    @GET("v1/forecast")
    suspend fun getWeeklyForecast(
        @Query("latitude") latitude: Float,
        @Query("longitude") longitude: Float,
        @Query("daily") daily: String = "temperature_2m_max,temperature_2m_min,precipitation_sum,windspeed_10m_max",
        @Query("timezone") timezone: String = "auto"
    ): Response<WeatherResponse>
}