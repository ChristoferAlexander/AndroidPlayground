package com.alex.androidplayground.core.utils.location


fun validateLatitude(lat: String): Boolean {
    return lat.toFloatOrNull()?.let { it in -90.0..90.0 } == true
}

fun validateLongitude(long: String): Boolean {
    return long.toFloatOrNull()?.let { it in -180.0..180.0 } == true
}

fun validateLatLong(lat: String, long: String): Boolean {
    return validateLatitude(lat) && validateLongitude(long)
}