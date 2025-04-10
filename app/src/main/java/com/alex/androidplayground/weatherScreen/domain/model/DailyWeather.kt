package com.alex.androidplayground.weatherScreen.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DailyWeather(
    val date: String,
    val temperatureMax: Double,  // Max temperature
    val temperatureMin: Double,  // Min temperature
    val precipitation: Double,   // Precipitation in mm
    val windSpeedMax: Double     // Max wind speed in km/h
) : Parcelable