package com.alex.androidplayground.core.utils.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import androidx.core.content.ContextCompat
import com.alex.androidplayground.core.utils.result.Result
import com.alex.androidplayground.core.utils.result.error.LocationError
import com.google.android.gms.location.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*

interface LocationService {
    fun getLocationUpdates(interval: Long): Flow<Result<Location, LocationError>>
}

class LocationServiceImpl(
    private val context: Context,
    private val client: FusedLocationProviderClient
) : LocationService {

    private val gpsAndNetworkState = callbackFlow {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context?, intent: Intent?) {
                trySend(Unit)
            }
        }
        context.registerReceiver(receiver, IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION))
        awaitClose { context.unregisterReceiver(receiver) }
    }.map { context.isGpsEnabled() to context.isNetworkEnabled() }
        .distinctUntilChanged()

    @OptIn(ExperimentalCoroutinesApi::class)
    @SuppressLint("MissingPermission")
    override fun getLocationUpdates(interval: Long): Flow<Result<Location, LocationError>> {
        return gpsAndNetworkState.flatMapLatest { (isGpsOn, isNetworkOn) ->
            when {
                !context.hasLocationPermission() -> throw Exception("Location permissions need to be granted to start LocationService updates")
                !isGpsOn -> flowOf(Result.Error(LocationError.NoGps))
                !isNetworkOn -> flowOf(Result.Error(LocationError.NoNetwork))
                else -> startLocationUpdates(interval)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates(interval: Long): Flow<Result<Location, LocationError>> = callbackFlow {
        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, interval).build()
        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.locations.lastOrNull()?.let { trySend(Result.Success(it)) }
            }
        }
        client.requestLocationUpdates(request, callback, Looper.getMainLooper())
        awaitClose { client.removeLocationUpdates(callback) }
    }
}

// Extension functions for checking permissions and GPS/network status
fun Context.hasLocationPermission() = ContextCompat.checkSelfPermission(
    this, Manifest.permission.ACCESS_FINE_LOCATION
) == PackageManager.PERMISSION_GRANTED

fun Context.isGpsEnabled(): Boolean =
    (getSystemService(Context.LOCATION_SERVICE) as LocationManager).isProviderEnabled(LocationManager.GPS_PROVIDER)

fun Context.isNetworkEnabled(): Boolean =
    (getSystemService(Context.LOCATION_SERVICE) as LocationManager).isProviderEnabled(LocationManager.NETWORK_PROVIDER)
