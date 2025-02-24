package com.alex.androidplayground.model.ui

import com.alex.androidplayground.model.navigation.FileSelectorDest
import com.alex.androidplayground.model.navigation.ForegroundServiceDest
import com.alex.androidplayground.model.navigation.NavDestinationItem
import com.alex.androidplayground.model.navigation.WorkManagerDest
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf


sealed class NavDrawerItem(val text: String, val destination: NavDestinationItem)

data object FileSelectorItem : NavDrawerItem("Files", FileSelectorDest)

data object ForegroundServiceItem : NavDrawerItem("ForegroundService", ForegroundServiceDest)

data object WorkManagerItem : NavDrawerItem("WorkManager", WorkManagerDest)

fun getNavDrawerItems(): PersistentList<NavDrawerItem> {
    return persistentListOf(
        FileSelectorItem,
        ForegroundServiceItem,
        WorkManagerItem
    )
}
