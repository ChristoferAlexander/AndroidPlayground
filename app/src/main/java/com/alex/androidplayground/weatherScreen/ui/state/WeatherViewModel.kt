package com.alex.androidplayground.weatherScreen.ui.state

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.alex.androidplayground.core.model.result.Result
import com.alex.androidplayground.core.ui.state.BaseViewModel
import com.alex.androidplayground.core.utils.coroutines.DispatcherProvider
import com.alex.androidplayground.core.utils.location.LocationService
import com.alex.androidplayground.core.utils.location.validateLatLong
import com.alex.androidplayground.core.utils.location.validateLatitude
import com.alex.androidplayground.core.utils.location.validateLongitude
import com.alex.androidplayground.weatherScreen.domain.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.async
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val service: WeatherRepository,
    private val locationService: LocationService,
    private val dispatcherProvider: DispatcherProvider
) : BaseViewModel<WeatherScreenState, WeatherScreenAction, WeatherScreenEvent>(
    initialState = {
        val state = savedStateHandle.get<WeatherScreenState>(STATE_KEY)
        state ?: WeatherScreenState("0", "0")
    }
) {

    companion object {
        private const val STATE_KEY = "weather_state"
    }

    init {
        viewModelScope.launch {
            launch { saveStateCollector() }
            launch { autoLocationCollector() }
        }
    }

    override fun onAction(action: WeatherScreenAction) {
        when (action) {
            is WeatherScreenAction.UpdateLatLong -> updateLatLong(action.lat, action.long)
            is WeatherScreenAction.ToggleAutoLocation -> toggleAutoLocation()
            is WeatherScreenAction.ShowPermissionsRationale -> setPermissionsRationaleDialogVisibility(true)
            WeatherScreenAction.HidePermissionsRationale -> setPermissionsRationaleDialogVisibility(false)
            is WeatherScreenAction.FetchWeatherScreen,
            is WeatherScreenAction.RetryFetchData -> viewModelScope.launch {
                fetchWeatherData(state.value.lat, state.value.long)
            }
        }
    }

    /**
     *  Collects [WeatherScreenState.autoLocation]
     *
     * When [WeatherScreenState.autoLocation]:
     *  - TRUE fetches weather data from location updates
     *  - FALSE false does nothing
     */
    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    private suspend fun autoLocationCollector() = withContext(dispatcherProvider.io) {
        state
            .distinctUntilChanged { old, new -> old.autoLocation == new.autoLocation }
            .flatMapLatest { state ->
                when {
                    state.autoLocation -> {
                        setState { it.copy(isLoading = true) }
                        locationService.getLocationUpdates(1000)
                    }

                    else -> {
                        setState { it.copy(isLoading = false) }
                        flowOf()
                    }
                }
            }
            .debounce(1000)
            .collect { location ->
                setState { it.copy(isLoading = false) }
                when (location) {
                    is Result.Error -> sendEvent(WeatherScreenEvent.DisplayErrorToast("${location.error}"))
                    is Result.Success -> {
                        val lat = location.data.latitude.toString()
                        val long = location.data.longitude.toString()
                        updateLatLong(lat = lat, long = long)
                        fetchWeatherData(latitude = lat, longitude = long)
                    }
                }
            }
    }

    private suspend fun fetchWeatherData(latitude: String, longitude: String) = withContext(dispatcherProvider.io) {
        if (!validateLatLong(latitude, longitude)) {
            setState { it.copy(currentWeather = null, weeklyForecast = emptyList()) }
            sendEvent(WeatherScreenEvent.DisplayErrorToast("Lat must be in -90 to 90 and long in -180 to 180"))
            return@withContext
        }
        val currentWeatherDeferred = async { service.getCurrentWeather(latitude.toFloat(), longitude.toFloat()) }
        val weeklyForecastDeferred = async { service.getWeeklyForecast(latitude.toFloat(), longitude.toFloat()) }
        val currentWeather = currentWeatherDeferred.await()
        val weeklyForecast = weeklyForecastDeferred.await()
        ensureActive()
        when {
            currentWeather is Result.Error -> sendEvent(WeatherScreenEvent.DisplayErrorToast(currentWeather.error.toString()))
            weeklyForecast is Result.Error -> sendEvent(WeatherScreenEvent.DisplayErrorToast(weeklyForecast.error.toString()))
            currentWeather is Result.Success && weeklyForecast is Result.Success -> {
                setState {
                    it.copy(
                        currentWeather = currentWeather.data.current_weather,
                        weeklyForecast = weeklyForecast.data.daily?.toDailyWeatherList() ?: emptyList()
                    )
                }
            }
        }
    }

    private fun updateLatLong(lat: String, long: String) {
        val isLatValid = validateLatitude(lat)
        val isLongValid = validateLongitude(long)
        setState {
            it.copy(
                lat = lat,
                long = long,
                isLatValid = isLatValid,
                isLongValid = isLongValid
            )
        }
    }

    private fun toggleAutoLocation() {
        setState { it.copy(autoLocation = !it.autoLocation) }
    }

    private fun setPermissionsRationaleDialogVisibility(isVisible: Boolean) {
        setState { it.copy(showPermissionsRationale = isVisible) }
    }

    @OptIn(FlowPreview::class)
    private suspend fun saveStateCollector() {
        state
            .debounce(500)
            .collectLatest { state ->
                savedStateHandle[STATE_KEY] = state
            }
    }
}
