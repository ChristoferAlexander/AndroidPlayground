package com.alex.androidplayground.weatherScreen.ui.state


import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alex.androidplayground.core.utils.result.Result
import com.alex.androidplayground.weatherScreen.domain.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val service: WeatherRepository
) : ViewModel() {

    companion object {
        private const val LAT_KEY = "latitude"
        private const val LONG_KEY = "longitude"
        private const val STATE_KEY = "weather_state"
    }

    private val _state: MutableStateFlow<WeatherMviState> =
        MutableStateFlow(restoreState())
    val state = _state.asStateFlow()

    private val _events = Channel<WeatherEvents>()
    val events = _events.receiveAsFlow()

    private val _lat = MutableStateFlow(savedStateHandle.get<String>(LAT_KEY) ?: "")
    val lat = _lat.asStateFlow()

    private val _long = MutableStateFlow(savedStateHandle.get<String>(LONG_KEY) ?: "")
    val long = _long.asStateFlow()

    init {
        viewModelScope.launch {
            _state.collect { state ->
                saveState(state)
            }
        }
    }

    fun onAction(action: WeatherMviAction) {
        when (action) {
            is WeatherMviAction.FetchWeather, is WeatherMviAction.RetryFetchData -> {
                if (validateLatLong(_lat.value, _long.value)) {
                    fetchWeatherData(_lat.value, _long.value)
                } else {
                    displayLatLongError()
                }
            }

            is WeatherMviAction.UpdateLatLong -> updateLatLongState(action.lat, action.long)
            WeatherMviAction.InvalidLatLong -> displayLatLongError()
        }
    }

    private fun fetchWeatherData(latitude: String, longitude: String) {
        suspend fun switchToErrorState(error: String) {
            _state.update { WeatherMviState.Error }
            _events.send(WeatherEvents.DisplayErrorToast(error))
        }

        viewModelScope.launch {
            _state.update {
                WeatherMviState.Loading
            }
            coroutineScope {
                val currentWeatherDeferred = async { service.getCurrentWeather(latitude.toDouble(), longitude.toDouble()) }
                val weeklyForecastDeferred = async { service.getWeeklyForecast(latitude.toDouble(), longitude.toDouble()) }

                val currentWeather = currentWeatherDeferred.await()
                val weeklyForecast = weeklyForecastDeferred.await()

                ensureActive()

                when {
                    currentWeather is Result.Error -> switchToErrorState(currentWeather.error.toString())
                    weeklyForecast is Result.Error -> switchToErrorState(weeklyForecast.error.toString())
                    currentWeather is Result.Success && weeklyForecast is Result.Success -> {
                        val newState = WeatherMviState.Weather(
                            currentWeather = currentWeather.data.current_weather,
                            weeklyForecast = weeklyForecast.data.daily?.toDailyWeatherList() ?: emptyList()
                        )
                        _state.update { newState }
                        saveState(newState)
                    }
                }
            }
        }
    }

    private fun updateLatLongState(lat: String, long: String) {
        _lat.update { lat }
        _long.update { long }
        savedStateHandle[LAT_KEY] = lat
        savedStateHandle[LONG_KEY] = long
    }

    private fun displayLatLongError() {
        viewModelScope.launch {
            _events.send(WeatherEvents.DisplayErrorToast("Latitude and Longitude cannot be empty"))
        }
    }

    private fun saveState(state: WeatherMviState) {
        savedStateHandle[STATE_KEY] = state
    }

    private fun restoreState(): WeatherMviState {
        val state = savedStateHandle.get<WeatherMviState>(STATE_KEY)
        return state ?: WeatherMviState.Weather()
    }

    private fun validateLatLong(lat: String, long: String): Boolean {
        val latitude = lat.toDoubleOrNull()
        val longitude = long.toDoubleOrNull()
        return latitude != null && longitude != null &&
                latitude in -90.0..90.0 && longitude in -180.0..180.0
    }
}
