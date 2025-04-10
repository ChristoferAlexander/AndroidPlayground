package com.alex.androidplayground.breweryScreen.ui.state

import com.alex.androidplayground.core.ui.state.State

data class BreweryScreenState(
    val query: String = "",
) : State

enum class Sort(val value: String) {
    ASCENDING("asc"),
    DESCENDING("desc")
}