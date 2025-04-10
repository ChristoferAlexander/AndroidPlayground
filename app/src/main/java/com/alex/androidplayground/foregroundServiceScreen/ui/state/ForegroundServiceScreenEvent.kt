package com.alex.androidplayground.foregroundServiceScreen.ui.state

import com.alex.androidplayground.core.ui.state.Event

sealed interface ForegroundServiceScreenEvent : Event {
    data object StartServiceScreen : ForegroundServiceScreenEvent
    data object StopServiceScreen : ForegroundServiceScreenEvent
}
