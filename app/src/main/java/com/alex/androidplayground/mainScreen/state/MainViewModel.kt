package com.alex.androidplayground.mainScreen.state

import androidx.lifecycle.ViewModel
import com.alex.androidplayground.core.ui.model.navigation.getNavDrawerItems
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(): ViewModel() {
    val navDrawerItems = getNavDrawerItems()
}