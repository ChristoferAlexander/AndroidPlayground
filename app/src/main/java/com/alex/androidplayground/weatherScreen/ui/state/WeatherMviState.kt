package com.alex.androidplayground.weatherScreen.ui.state

import android.os.Parcelable
import com.alex.androidplayground.weatherScreen.domain.model.CurrentWeather
import com.alex.androidplayground.weatherScreen.domain.model.DailyWeather
import kotlinx.parcelize.Parcelize

@Parcelize
sealed class WeatherMviState : Parcelable {
    @Parcelize
    data object Loading : WeatherMviState()

    @Parcelize
    data class Weather(
        val currentWeather: CurrentWeather? = null,
        val weeklyForecast: List<DailyWeather> = emptyList()
    ) : WeatherMviState()

    @Parcelize
    data object Error : WeatherMviState()
}
