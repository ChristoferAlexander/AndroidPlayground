package com.alex.androidplayground

import android.Manifest
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.test.rule.GrantPermissionRule
import com.alex.androidplayground.mainScreen.MainActivity
import com.alex.androidplayground.weatherScreen.data.source.remote.WeatherApi
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
@HiltAndroidTest
class WeatherScreenStateTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @get:Rule(order = 2)
    val permissionRule: GrantPermissionRule? = GrantPermissionRule.grant(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    @Inject
    lateinit var weatherApi: WeatherApi

    @Before
    fun setup() {
        hiltRule.inject()
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }


    private fun navigateToWeatherScreen() {
        composeTestRule.onNodeWithContentDescription("Menu toggle icon").performClick()
        composeTestRule.onNodeWithTag("NavItem_Weather").performClick()
    }

    @Test
    fun weatherScreen_autoLocation_toggle_disables_textFields() = runTest {
        navigateToWeatherScreen()
        composeTestRule.onNodeWithTag("AutoLocation_Switch").performClick()
        composeTestRule.onNodeWithTag("Latitude_TextField").assertIsNotEnabled()
        composeTestRule.onNodeWithTag("Longitude_TextField").assertIsNotEnabled()
    }

    @Test
    fun validLatLong_enablesFetchWeatherButton() = runTest {
        navigateToWeatherScreen()
        composeTestRule.onNodeWithTag("Latitude_TextField").performTextClearance()
        composeTestRule.onNodeWithTag("Latitude_TextField").performTextInput("45")
        composeTestRule.onNodeWithTag("Longitude_TextField").performTextClearance()
        composeTestRule.onNodeWithTag("Longitude_TextField").performTextInput("90")
        composeTestRule.onNodeWithTag("FetchWeather_Button").assertIsEnabled()
    }

    @Test
    fun invalidLatLong_disablesFetchWeatherButton() = runTest {
        navigateToWeatherScreen()
        composeTestRule.onNodeWithTag("Latitude_TextField").performTextClearance()
        composeTestRule.onNodeWithTag("Latitude_TextField").performTextInput("200")
        composeTestRule.onNodeWithTag("Longitude_TextField").performTextClearance()
        composeTestRule.onNodeWithTag("Longitude_TextField").performTextInput("-999")
        composeTestRule.onNodeWithTag("FetchWeather_Button").assertIsNotEnabled()
    }

    @Test
    fun weatherDataDisplayed_whenAvailable() = runTest {
        navigateToWeatherScreen()
        composeTestRule.onNodeWithTag("Latitude_TextField").performTextClearance()
        composeTestRule.onNodeWithTag("Latitude_TextField").performTextInput("35")
        composeTestRule.onNodeWithTag("Longitude_TextField").performTextClearance()
        composeTestRule.onNodeWithTag("Longitude_TextField").performTextInput("139")
        composeTestRule.onNodeWithTag("FetchWeather_Button").performClick()
        composeTestRule.onNodeWithTag("CurrentWeather_Container").assertExists()
        composeTestRule.onNodeWithTag("WeeklyForecast_Container").assertExists()
        composeTestRule.onAllNodesWithTag("WeeklyForecast_Item").fetchSemanticsNodes().isNotEmpty()
    }

    @Test
    fun weatherDataDisplayed_whenAutoLocation_is_on() = runTest {
        navigateToWeatherScreen()
        composeTestRule.onNodeWithTag("AutoLocation_Switch").performClick()
        composeTestRule.onNodeWithTag("Latitude_TextField").assertIsNotEnabled()
        composeTestRule.onNodeWithTag("Longitude_TextField").assertIsNotEnabled()
        composeTestRule.onNodeWithTag("FetchWeather_Button").assertIsNotEnabled()
        // TODO fix mock emission
        advanceTimeBy(10000)
        composeTestRule.onNodeWithTag("CurrentWeather_Container").assertExists()
        composeTestRule.onNodeWithTag("WeeklyForecast_Container").assertExists()
        composeTestRule.onAllNodesWithTag("WeeklyForecast_Item").fetchSemanticsNodes().isNotEmpty()
    }
}