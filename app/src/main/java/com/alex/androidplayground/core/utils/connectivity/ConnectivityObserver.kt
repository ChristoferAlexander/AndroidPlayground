package com.alex.androidplayground.core.utils.connectivity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject


interface ConnectivityObserver {
    val isConnected: Flow<Boolean>
    val isGpsEnabled: Flow<Boolean>
}

class ConnectivityObserverImpl @Inject constructor(val context: Context) : ConnectivityObserver {

    private val connectivityManager = context.getSystemService<ConnectivityManager>()!!
    private val locationManager = context.getSystemService<LocationManager>()!!

    override val isConnected: Flow<Boolean>
        get() = callbackFlow {
            val callback = object : ConnectivityManager.NetworkCallback() {
                override fun onCapabilitiesChanged(
                    network: Network,
                    networkCapabilities: NetworkCapabilities
                ) {
                    super.onCapabilitiesChanged(network, networkCapabilities)
                    val connected = networkCapabilities.hasCapability(
                        NetworkCapabilities.NET_CAPABILITY_VALIDATED
                    )
                    trySend(connected)
                }

                override fun onUnavailable() {
                    super.onUnavailable()
                    trySend(false)
                }

                override fun onLost(network: Network) {
                    super.onLost(network)
                    trySend(false)
                }

                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    trySend(true)
                }
            }
            connectivityManager.registerDefaultNetworkCallback(callback)
            awaitClose {
                connectivityManager.unregisterNetworkCallback(callback)
            }
        }

    override val isGpsEnabled: Flow<Boolean>
        get() = callbackFlow {
            val gpsReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    val isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                    trySend(isEnabled)
                }
            }
            val filter = IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION).apply {
                addAction(Intent.ACTION_PROVIDER_CHANGED)
            }
            ContextCompat.registerReceiver(context, gpsReceiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED)
            trySend(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            awaitClose {
                context.unregisterReceiver(gpsReceiver)
            }
        }
}