package com.alex.androidplayground.weatherScreen.data.repository

import com.alex.androidplayground.core.utils.api.safeApiCall
import com.alex.androidplayground.core.utils.coroutines.DispatcherProvider
import com.alex.androidplayground.core.model.result.Result
import com.alex.androidplayground.core.model.result.error.DataError
import com.alex.androidplayground.weatherScreen.data.source.remote.WeatherApi
import com.alex.androidplayground.weatherScreen.data.source.remote.model.WeatherResponse
import com.alex.androidplayground.weatherScreen.domain.repository.WeatherRepository
import kotlinx.coroutines.withContext

class WeatherRepositoryImp(
    val api: WeatherApi,
    private val dispatcherProvider: DispatcherProvider
) : WeatherRepository {
    override suspend fun getCurrentWeather(lat: Float, long: Float): Result<WeatherResponse, DataError.Network> = withContext(dispatcherProvider.io) {
        return@withContext safeApiCall { api.getCurrentWeather(lat, long, true) }
    }

    override suspend fun getWeeklyForecast(lat: Float, long: Float): Result<WeatherResponse, DataError.Network> = withContext(dispatcherProvider.io) {
        return@withContext safeApiCall { api.getWeeklyForecast(lat, long) }
    }
}
