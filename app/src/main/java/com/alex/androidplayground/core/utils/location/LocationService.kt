package com.alex.androidplayground.core.utils.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import com.alex.androidplayground.core.model.result.Result
import com.alex.androidplayground.core.model.result.error.LocationError
import com.alex.androidplayground.core.utils.permissions.hasLocationPermission
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

interface LocationService {
    fun getLocationUpdates(interval: Long): Flow<Result<Location, LocationError>>
}

class LocationServiceImpl @Inject constructor(
    private val context: Context,
    private val client: FusedLocationProviderClient
) : LocationService {

    @SuppressLint("MissingPermission")
    override fun getLocationUpdates(interval: Long): Flow<Result<Location, LocationError>> {
        return callbackFlow {
            val callback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    ensureActive()
                    result.locations.lastOrNull()?.let { trySend(Result.Success(it)) }
                }
            }
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            when {
                !context.hasLocationPermission() -> trySend(Result.Error(LocationError.NoPermissions))
                !isGpsEnabled -> trySend(Result.Error(LocationError.NoGps))
                !isNetworkEnabled -> trySend(Result.Error(LocationError.NoNetwork))
                else -> {
                    val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, interval).build()
                    client.requestLocationUpdates(request, callback, Looper.getMainLooper())
                }
            }
            awaitClose {
                client.removeLocationUpdates(callback)
            }
        }
    }
}
