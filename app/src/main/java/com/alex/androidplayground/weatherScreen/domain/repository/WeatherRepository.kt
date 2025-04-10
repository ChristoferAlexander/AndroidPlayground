package com.alex.androidplayground.weatherScreen.domain.repository

import com.alex.androidplayground.core.model.result.error.DataError
import com.alex.androidplayground.core.model.result.Result
import com.alex.androidplayground.weatherScreen.data.source.remote.model.WeatherResponse

interface WeatherRepository {
    suspend fun getCurrentWeather(lat: Float, long: Float): Result<WeatherResponse, DataError.Network>
    suspend fun getWeeklyForecast(lat: Float, long: Float): Result<WeatherResponse, DataError.Network>
}
