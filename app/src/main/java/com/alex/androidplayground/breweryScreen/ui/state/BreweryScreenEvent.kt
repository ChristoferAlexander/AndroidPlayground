package com.alex.androidplayground.breweryScreen.ui.state

import com.alex.androidplayground.core.ui.state.Event

sealed interface BreweryScreenEvent : Event {
    data class OnBrewerySelected(val breweryId: String) : BreweryScreenEvent
    data object ScrollToTop: BreweryScreenEvent
    data object OnConnectionRestored: BreweryScreenEvent
}