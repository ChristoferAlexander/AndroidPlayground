package com.alex.androidplayground

import android.location.Location
import androidx.lifecycle.SavedStateHandle
import com.alex.androidplayground.core.model.result.Result
import com.alex.androidplayground.core.model.result.error.DataError
import com.alex.androidplayground.core.model.result.error.LocationError
import com.alex.androidplayground.core.utils.location.LocationService
import com.alex.androidplayground.mocks.core.utils.coroutines.TestDispatchersProvider
import com.alex.androidplayground.weatherScreen.data.source.remote.WeatherResponse
import com.alex.androidplayground.weatherScreen.domain.model.CurrentWeather
import com.alex.androidplayground.weatherScreen.domain.model.WeaklyForecast
import com.alex.androidplayground.weatherScreen.domain.repository.WeatherRepository
import com.alex.androidplayground.weatherScreen.ui.state.WeatherScreenAction
import com.alex.androidplayground.weatherScreen.ui.state.WeatherScreenEvent
import com.alex.androidplayground.weatherScreen.ui.state.WeatherViewModel
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WeatherViewModelTest {

    @MockK
    lateinit var weatherRepository: WeatherRepository

    @MockK
    lateinit var locationService: LocationService

    private lateinit var viewModel: WeatherViewModel

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxed = true)
        Dispatchers.setMain(UnconfinedTestDispatcher())
        viewModel = WeatherViewModel(SavedStateHandle(), weatherRepository, locationService, TestDispatchersProvider(UnconfinedTestDispatcher()))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is correct`() = runTest {
        val state = viewModel.state.value
        assertEquals("0", state.lat)
        assertEquals("0", state.long)
        assertFalse(state.autoLocation)
        assertTrue(state.isLatValid)
        assertTrue(state.isLongValid)
    }

    @Test
    fun `update lat and long with valid values updates state correctly`() = runTest {
        viewModel.onAction(WeatherScreenAction.UpdateLatLong("45.0", "-93.0"))
        val state = viewModel.state.value
        assertEquals("45.0", state.lat)
        assertEquals("-93.0", state.long)
        assertTrue(state.isLatValid)
        assertTrue(state.isLongValid)
    }

    @Test
    fun `update lat and long with invalid values flags errors`() = runTest {
        viewModel.onAction(WeatherScreenAction.UpdateLatLong("200", "-200"))
        val state = viewModel.state.value
        assertFalse(state.isLatValid)
        assertFalse(state.isLongValid)
    }

    @Test
    fun `toggle auto location updates state`() = runTest {
        viewModel.onAction(WeatherScreenAction.ToggleAutoLocation)
        assertTrue(viewModel.state.value.autoLocation)
    }

    @Test
    fun `fetchWeather with valid lat long sets current and forecast weather`() = runTest {
        coEvery { weatherRepository.getCurrentWeather(45.0F, -93.0F) } returns Result.Success(WeatherResponse(current_weather = fakeCurrentWeather))
        coEvery { weatherRepository.getWeeklyForecast(45.0F, -93.0F) } returns Result.Success(WeatherResponse(daily = fakeWeaklyForecast))
        viewModel.onAction(WeatherScreenAction.UpdateLatLong("45.0", "-93.0"))
        viewModel.onAction(WeatherScreenAction.FetchWeatherScreen)
        advanceUntilIdle()
        val state = viewModel.state.value
        assertEquals("45.0", state.lat)
        assertEquals("-93.0", state.long)
        assertEquals(fakeCurrentWeather, state.currentWeather)
        assertEquals(fakeWeaklyForecast.toDailyWeatherList(), state.weeklyForecast)
    }

    @Test
    fun `fetchWeather returns error on failure`() = runTest {
        coEvery { weatherRepository.getCurrentWeather(any(), any()) } returns Result.Error(DataError.Network.NETWORK_ERROR)
        coEvery { weatherRepository.getWeeklyForecast(any(), any()) } returns Result.Success(WeatherResponse(daily = fakeWeaklyForecast))
        viewModel.onAction(WeatherScreenAction.UpdateLatLong("45.0", "-93.0"))
        viewModel.onAction(WeatherScreenAction.FetchWeatherScreen)
        advanceUntilIdle()
        val state = viewModel.state.value
        assertEquals("45.0", state.lat)
        assertEquals("-93.0", state.long)
        assertNull(state.currentWeather)
        assertTrue(state.weeklyForecast.isEmpty())
        val event = viewModel.events.first()
        assertTrue(event is WeatherScreenEvent.DisplayErrorToast)
    }

    @Test
    fun `autoLocation fetches weather on location success`() = runTest {
        val mockedLocation: Location = mockk {
            every { latitude } returns 45.0
            every { longitude } returns -93.0
        }
        coEvery { locationService.getLocationUpdates(1000) } returns flowOf(Result.Success(mockedLocation))
        coEvery { weatherRepository.getCurrentWeather(45.0F, -93.0F) } returns Result.Success(WeatherResponse(current_weather = fakeCurrentWeather))
        coEvery { weatherRepository.getWeeklyForecast(45.0F, -93.0F) } returns Result.Success(WeatherResponse(daily = fakeWeaklyForecast))
        viewModel.onAction(WeatherScreenAction.ToggleAutoLocation)
        advanceUntilIdle()
        val state = viewModel.state.value
        assertEquals("45.0", state.lat)
        assertEquals("-93.0", state.long)
        assertEquals(fakeCurrentWeather, state.currentWeather)
        assertEquals(fakeWeaklyForecast.toDailyWeatherList(), state.weeklyForecast)
    }

    @Test
    fun `autoLocation handles location error correctly`() = runTest {
        coEvery { locationService.getLocationUpdates(1000) } returns flowOf(Result.Error(LocationError.NoGps))
        viewModel.onAction(WeatherScreenAction.ToggleAutoLocation)
        advanceUntilIdle()
        val event = viewModel.events.first()
        assertTrue(event is WeatherScreenEvent.DisplayErrorToast)
        assertEquals("NoGps", (event as WeatherScreenEvent.DisplayErrorToast).error)
    }

    @Test
    fun `autoLocation handles permission error correctly`() = runTest {
        coEvery { locationService.getLocationUpdates(1000) } returns flowOf(
            Result.Error(LocationError.NoPermissions)
        )
        viewModel.onAction(WeatherScreenAction.ToggleAutoLocation)
        advanceUntilIdle()
        val event = viewModel.events.first()
        assertTrue(event is WeatherScreenEvent.DisplayErrorToast)
        assertEquals("NoPermissions", (event as WeatherScreenEvent.DisplayErrorToast).error)
    }

    @Test
    fun `autoLocation handles network error correctly`() = runTest {
        coEvery { locationService.getLocationUpdates(1000) } returns flowOf(
            Result.Error(LocationError.NoNetwork)
        )
        viewModel.onAction(WeatherScreenAction.ToggleAutoLocation)
        advanceUntilIdle()
        val event = viewModel.events.first()
        assertTrue(event is WeatherScreenEvent.DisplayErrorToast)
        assertEquals("NoNetwork", (event as WeatherScreenEvent.DisplayErrorToast).error)
    }

    companion object {
        val fakeCurrentWeather = CurrentWeather(
            temperature = 20.0,
            windspeed = 3.5,
            time = "1:00 PM"
        )

        val fakeWeaklyForecast = WeaklyForecast(
            time = listOf("2025-04-09", "2025-04-10"),
            temperatureMax = listOf(22.0, 24.0),
            temperatureMin = listOf(12.0, 14.0),
            precipitation = listOf(1.2, 0.0),
            windSpeedMax = listOf(10.5, 8.3)
        )
    }
}
