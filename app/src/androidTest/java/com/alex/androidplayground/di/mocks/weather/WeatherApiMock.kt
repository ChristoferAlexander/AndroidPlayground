package com.alex.androidplayground.di.mocks.weather

import com.alex.androidplayground.weather.api.WeatherApi
import com.alex.androidplayground.weather.model.CurrentWeather
import com.alex.androidplayground.weather.model.WeatherResponse
import retrofit2.Response

class WeatherApiMock : WeatherApi {
    override suspend fun getCurrentWeather(
        latitude: Double,
        longitude: Double,
        currentWeather: Boolean
    ): Response<WeatherResponse> {
        val fakeWeather = WeatherResponse(
            current_weather = CurrentWeather(
                temperature = 20.0,
                windspeed = 5.0,
                time = "2024-02-24T14:00:00Z"
            )
        )
        return Response.success(fakeWeather)
    }
}