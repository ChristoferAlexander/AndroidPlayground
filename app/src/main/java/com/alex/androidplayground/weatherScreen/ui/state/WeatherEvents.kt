package com.alex.androidplayground.weatherScreen.ui.state

import com.alex.androidplayground.core.ui.state.Event

sealed interface WeatherEvents : Event {
    data class DisplayErrorToast(val error: String) : WeatherEvents
}
