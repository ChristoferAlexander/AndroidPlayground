package com.alex.androidplayground.breweryScreen.domain.model

data class LocationFilter(
    val latitude: Double,
    val longitude: Double
) {
    override fun toString(): String {
        return "$latitude,$longitude"
    }
}