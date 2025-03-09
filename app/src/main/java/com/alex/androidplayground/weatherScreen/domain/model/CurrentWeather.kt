package com.alex.androidplayground.weatherScreen.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
@Parcelize
data class CurrentWeather(
    val temperature: Double,
    val windspeed: Double,
    val time: String
) : Parcelable