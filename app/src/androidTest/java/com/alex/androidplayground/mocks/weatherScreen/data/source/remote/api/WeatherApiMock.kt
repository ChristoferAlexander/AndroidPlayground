package com.alex.androidplayground.mocks.weatherScreen.data.source.remote.api

import com.alex.androidplayground.weatherScreen.data.source.remote.api.WeatherApi
import com.alex.androidplayground.weatherScreen.domain.model.CurrentWeather
import com.alex.androidplayground.weatherScreen.data.source.remote.model.WeatherResponse
import com.alex.androidplayground.weatherScreen.domain.model.WeaklyForecast
import retrofit2.Response

class WeatherApiMock : WeatherApi {
    override suspend fun getCurrentWeather(
        latitude: Double, longitude: Double, currentWeather: Boolean
    ): Response<WeatherResponse> {
        val fakeWeather = WeatherResponse(
            current_weather = CurrentWeather(
                temperature = 20.0, windspeed = 5.0, time = "2024-02-24T14:00:00Z"
            ), daily = null
        )
        return Response.success(fakeWeather)
    }

    override suspend fun getWeeklyForecast(
        latitude: Double, longitude: Double, daily: String, timezone: String
    ): Response<WeatherResponse> {
        val fakeWeeklyForecast = WeatherResponse(
            current_weather = null,
            daily = WeaklyForecast(
                time = listOf("2024-02-25", "2024-02-26", "2024-02-27", "2024-02-28", "2024-03-01"),
                temperatureMax = listOf(22.0, 23.5, 21.0, 24.0, 26.0),
                temperatureMin = listOf(15.0, 16.0, 14.5, 15.5, 17.0),
                precipitation = listOf(1.2, 0.5, 0.0, 1.0, 0.2),
                windSpeedMax = listOf(15.0, 12.0, 14.0, 16.0, 18.0)
            )
        )
        return Response.success(fakeWeeklyForecast)
    }
}