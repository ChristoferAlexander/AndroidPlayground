package com.alex.androidplayground.model.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class NavDestinationItem

@Serializable
data object FileSelectorDest : NavDestinationItem()

@Serializable
data object ForegroundServiceDest : NavDestinationItem()

@Serializable
data object WorkManagerDest : NavDestinationItem()