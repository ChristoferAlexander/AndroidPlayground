package com.alex.androidplayground.foregroundServiceScreen.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.alex.androidplayground.R
import com.alex.androidplayground.core.model.result.Result
import com.alex.androidplayground.core.utils.location.LocationService
import com.alex.androidplayground.foregroundServiceScreen.domain.repository.ForegroundServiceStatusRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.ExperimentalAtomicApi

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalAtomicApi::class)
@AndroidEntryPoint
class LocationForegroundService : Service() {

    @Inject
    lateinit var locationService: LocationService

    @Inject
    lateinit var statusRepository: ForegroundServiceStatusRepository

    private var scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private var isRunning = AtomicBoolean(false)
    private lateinit var currentLocation: StateFlow<Location?>

    private val channelId = "location_channel"
    private val notificationId = 1

    override fun onCreate() {
        super.onCreate()
        currentLocation = locationService
            .getLocationUpdates(5000)
            .filter { it is Result.Success }
            .mapLatest { (it as Result.Success).data }
            .stateIn(scope, SharingStarted.WhileSubscribed(5000), null)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            Actions.START.toString() -> startLocationTracking()
            Actions.STOP.toString() -> stopSelf()
        }
        return START_STICKY
    }

    private fun startLocationTracking() {
        if (isRunning.load()) return
        scope.launch {
            createNotificationChannel()
            currentLocation.collect { location ->
                val notification = createNotification(location)
                if (!isRunning.load()) {
                    startForeground(notificationId, notification)
                    statusRepository.setRunning(true)
                    isRunning.store(true)
                }
                getSystemService(NotificationManager::class.java)
                    .notify(notificationId, notification)
            }
        }
    }

    private fun createNotification(location: Location?): Notification {
        val locationText = location?.let {
            "Lat: %.5f, Lng: %.5f".format(it.latitude, it.longitude)
        } ?: "Getting location..."

        return NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Location Tracking Active")
            .setContentText(locationText)
            .setStyle(NotificationCompat.BigTextStyle().bigText(locationText))
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Location Updates",
                NotificationManager.IMPORTANCE_LOW
            )
            getSystemService(NotificationManager::class.java)
                .createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        scope.launch(NonCancellable) { statusRepository.setRunning(false) }
        isRunning.store(false)
        scope.cancel()
        super.onDestroy()
    }

    enum class Actions {
        START, STOP
    }
}
