package com.alex.androidplayground.weatherScreen.ui.state

import android.os.Parcelable
import com.alex.androidplayground.core.ui.state.State
import com.alex.androidplayground.weatherScreen.domain.model.CurrentWeather
import com.alex.androidplayground.weatherScreen.domain.model.DailyWeather
import kotlinx.parcelize.Parcelize

@Parcelize
data class WeatherScreenState(
    val lat: String = "0",
    val long: String = "0",
    val currentWeather: CurrentWeather? = null,
    val weeklyForecast: List<DailyWeather> = emptyList(),
    val isLoading: Boolean = false,
    val autoLocation: Boolean = false,
    val showPermissionsRationale: Boolean = false,
    val isLatValid: Boolean = true,
    val isLongValid: Boolean = true
) : Parcelable, State
