package com.alex.androidplayground.weatherScreen.domain.model

import com.google.gson.annotations.SerializedName

data class WeaklyForecast(
    val time: List<String>,                // List of dates for the forecast
    @SerializedName("temperature_2m_max")
    val temperatureMax: List<Double>,       // Max temperatures
    @SerializedName("temperature_2m_min")
    val temperatureMin: List<Double>,       // Min temperatures
    @SerializedName("precipitation_sum")
    val precipitation: List<Double>,        // Precipitation in mm
    @SerializedName("windspeed_10m_max")
    val windSpeedMax: List<Double>          // Maximum wind speed in km/h
) {
    fun toDailyWeatherList(): List<DailyWeather> {
        return time.mapIndexed { index, date ->
            DailyWeather(
                date = date,
                temperatureMax = temperatureMax[index],
                temperatureMin = temperatureMin[index],
                precipitation = precipitation[index],
                windSpeedMax = windSpeedMax[index]
            )
        }
    }
}
