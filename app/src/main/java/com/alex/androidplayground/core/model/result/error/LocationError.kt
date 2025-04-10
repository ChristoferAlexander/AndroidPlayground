package com.alex.androidplayground.core.model.result.error

enum class LocationError: Error {
    NoGps,
    NoNetwork,
    NoPermissions
}