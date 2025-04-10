package com.alex.androidplayground.breweryScreen.ui.state

import com.alex.androidplayground.breweryScreen.domain.model.Brewery
import com.alex.androidplayground.core.ui.state.State

data class BreweryDetailsScreenState(val brewery: Brewery? = null) : State
