package com.alex.androidplayground.mocks.core.utils.location

import android.location.Location
import com.alex.androidplayground.core.model.result.Result
import com.alex.androidplayground.core.model.result.error.LocationError
import com.alex.androidplayground.core.utils.location.LocationService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class LocationServiceMock : LocationService {
    private val _locationFlow = MutableStateFlow<Result<Location, LocationError>>(Result.Success(Location("Mock")))
    override fun getLocationUpdates(interval: Long): Flow<Result<Location, LocationError>> = _locationFlow
}