package com.alex.androidplayground.weatherScreen.data.repository

import com.alex.androidplayground.core.utils.api.toResult
import com.alex.androidplayground.core.utils.coroutines.DispatcherProvider
import com.alex.androidplayground.core.utils.result.Result
import com.alex.androidplayground.core.utils.result.error.DataError
import com.alex.androidplayground.weatherScreen.data.source.remote.api.WeatherApi
import com.alex.androidplayground.weatherScreen.data.source.remote.model.WeatherResponse
import com.alex.androidplayground.weatherScreen.domain.repository.WeatherRepository
import kotlinx.coroutines.withContext

class WeatherRepositoryImp (val api: WeatherApi, private val dispatcherProvider: DispatcherProvider) : WeatherRepository {
    override suspend fun getCurrentWeather(lat: Double, long: Double): Result<WeatherResponse, DataError.Network> = withContext(dispatcherProvider.io) {
        return@withContext api.getCurrentWeather(lat, long, true).toResult()
    }

    override suspend fun getWeeklyForecast(lat: Double, long: Double): Result<WeatherResponse, DataError.Network> = withContext(dispatcherProvider.io) {
        return@withContext api.getWeeklyForecast(lat, long).toResult()
    }
}

