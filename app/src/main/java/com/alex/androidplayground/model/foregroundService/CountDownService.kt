package com.alex.androidplayground.model.foregroundService

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.alex.androidplayground.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicInteger

class CountDownService : Service() {

    private val channelId = "test_channel"
    private val notificationId = 1
    private val seconds = AtomicInteger(0)
    private var job: Job? = null
    private var isRunning = false

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            Actions.START.toString() -> startTimer()
            Actions.STOP.toString() -> stopSelf()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun startTimer() {
        if (isRunning) return
        isRunning = true
        createNotificationChannel()
        startService()
        seconds.set(0)
        job = CoroutineScope(Dispatchers.Default).launch {
            while (isRunning) {
                withContext(Dispatchers.Main) {
                    updateNotification()
                }
                delay(1000L)
                seconds.incrementAndGet()
            }
        }
    }

    private fun createNotification(): Notification {
        val formattedTime = "${seconds.get() / 60}:${seconds.get() % 60}"
        return NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Timer Running")
            .setContentText(formattedTime)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun updateNotification() {
        getSystemService(NotificationManager::class.java).notify(notificationId, createNotification())
    }

    private fun startService() {
        startForeground(notificationId, createNotification())
    }

    override fun onDestroy() {
        isRunning = false
        job?.cancel()
        super.onDestroy()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Foreground Timer Service",
                NotificationManager.IMPORTANCE_LOW
            )
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }

    enum class Actions {
        START, STOP, PAUSE
    }
}
