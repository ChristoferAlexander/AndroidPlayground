package com.alex.androidplayground.weatherScreen.data.source.remote.model

import com.alex.androidplayground.weatherScreen.domain.model.CurrentWeather
import com.alex.androidplayground.weatherScreen.domain.model.WeaklyForecast

data class WeatherResponse(
    val current_weather: CurrentWeather? = null,
    val daily: WeaklyForecast? = null
)