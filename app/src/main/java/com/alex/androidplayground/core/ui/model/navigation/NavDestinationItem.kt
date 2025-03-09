package com.alex.androidplayground.core.ui.model.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class NavDestinationItem

// Main routes

@Serializable
data object FileSelectorDest : NavDestinationItem()

@Serializable
data object ForegroundServiceDest : NavDestinationItem()

@Serializable
data object WorkManagerDest : NavDestinationItem()

@Serializable
data object  WeatherDest : NavDestinationItem()