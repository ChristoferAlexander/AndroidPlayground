package com.alex.androidplayground.breweryScreen.ui.state

import com.alex.androidplayground.core.ui.state.Action

sealed interface BreweryScreenAction : Action {
    data class UpdateQuery(val query: String) : BreweryScreenAction
    data class BrewerySelected(val id: String) : BreweryScreenAction
}