package com.alex.androidplayground.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alex.androidplayground.model.ui.getNavDrawerItems
import com.alex.androidplayground.weather.service.WeatherService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val service: WeatherService
): ViewModel() {

    val navDrawerItems = getNavDrawerItems()

    init {
        viewModelScope.launch {
           val weather = service.getCurrentWeather(52.0107097, 4.3355464)
            if(weather.isSuccessful){
                println(weather.body())
            } else {
                println(weather.errorBody())
            }
        }
    }

}