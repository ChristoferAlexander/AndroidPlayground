package com.alex.androidplayground.weatherScreen.domain.repository

import com.alex.androidplayground.core.utils.result.error.DataError
import com.alex.androidplayground.core.utils.result.Result
import com.alex.androidplayground.weatherScreen.data.source.remote.model.WeatherResponse

interface WeatherRepository {
    suspend fun getCurrentWeather(lat: Double, long: Double): Result<WeatherResponse, DataError.Network>
    suspend fun getWeeklyForecast(lat: Double, long: Double): Result<WeatherResponse, DataError.Network>
}
