package org.company.app.ui

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat

class MediaProjectionService : Service() {

    companion object {
        const val CHANNEL_ID = "MediaProjectionChannel"
        const val NOTIFICATION_ID = 100
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        // Create a minimal notification to run this service in foreground.
        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Screen Capture")
            .setContentText("Screen capture is active")
            .setSmallIcon(android.R.drawable.ic_menu_camera)
            .build()
        startForeground(NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Screen Capture",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Foreground service for screen capture"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
