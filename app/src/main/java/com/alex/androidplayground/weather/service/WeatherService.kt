package com.alex.androidplayground.weather.service

import com.alex.androidplayground.weather.api.WeatherApi
import com.alex.androidplayground.weather.model.WeatherResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

interface WeatherService {
    suspend fun getCurrentWeather(lat: Double, long: Double): Response<WeatherResponse>
}


class WeatherServiceImp(val api: WeatherApi, private val defaultDispatcher: CoroutineDispatcher = Dispatchers.IO) : WeatherService {
    override suspend fun getCurrentWeather(lat: Double, long: Double): Response<WeatherResponse> = withContext(defaultDispatcher) {
        return@withContext api.getCurrentWeather(lat, long, true)
    }
}

