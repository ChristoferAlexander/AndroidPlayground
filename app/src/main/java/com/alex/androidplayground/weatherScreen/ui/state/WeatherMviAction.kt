package com.alex.androidplayground.weatherScreen.ui.state

sealed interface WeatherMviAction {
    data object RetryFetchData : WeatherMviAction
    data object FetchWeather : WeatherMviAction
    data object InvalidLatLong: WeatherMviAction
    data class UpdateLatLong(val lat: String, val long: String): WeatherMviAction
}
