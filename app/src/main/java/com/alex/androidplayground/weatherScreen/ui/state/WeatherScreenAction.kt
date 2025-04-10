package com.alex.androidplayground.weatherScreen.ui.state

import com.alex.androidplayground.core.ui.state.Action

sealed interface WeatherScreenAction : Action {
    data object RetryFetchData : WeatherScreenAction
    data object FetchWeatherScreen : WeatherScreenAction
    data class UpdateLatLong(val lat: String, val long: String) : WeatherScreenAction
    data object ToggleAutoLocation : WeatherScreenAction
    data object ShowPermissionsRationale : WeatherScreenAction
    data object HidePermissionsRationale : WeatherScreenAction
}