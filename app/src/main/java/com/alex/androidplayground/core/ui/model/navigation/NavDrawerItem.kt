package com.alex.androidplayground.core.ui.model.navigation

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList

enum class NavDrawerItem(val text: String, val destination: NavDestinationItem) {
    FILE_SELECTOR("Files", FileSelectorDest),
    FOREGROUND_SERVICE("ForegroundService", ForegroundServiceDest),
    WORK_MANAGER("WorkManager", WorkManagerDest),
    WEATHER("Weather", WeatherDest),
    BREWERY("Brewery", BreweriesListDest);
}

fun getNavDrawerItems(): PersistentList<NavDrawerItem> {
    return NavDrawerItem.entries.toPersistentList()
}