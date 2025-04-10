package com.alex.androidplayground.weatherScreen.ui.state

import com.alex.androidplayground.core.ui.state.Event

sealed interface WeatherScreenEvent : Event {
    data class DisplayErrorToast(val error: String) : WeatherScreenEvent
    data object LaunchAppSettings: WeatherScreenEvent
}
