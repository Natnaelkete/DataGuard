package com.dataguard.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.dataguard.R
import com.dataguard.ui.MainActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Foreground service channel
            val foregroundChannel = NotificationChannel(
                FOREGROUND_CHANNEL_ID,
                "Data Monitoring",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Monitoring mobile data usage"
                setShowBadge(false)
            }
            notificationManager.createNotificationChannel(foregroundChannel)

            // High usage alerts channel
            val alertChannel = NotificationChannel(
                ALERT_CHANNEL_ID,
                "Data Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "High data usage alerts"
            }
            notificationManager.createNotificationChannel(alertChannel)

            // Idle detection channel
            val idleChannel = NotificationChannel(
                IDLE_CHANNEL_ID,
                "Idle Detection",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Device idle notifications"
            }
            notificationManager.createNotificationChannel(idleChannel)
        }
    }

    fun createForegroundNotification(): Notification {
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(context, FOREGROUND_CHANNEL_ID)
            .setContentTitle("DataGuard")
            .setContentText("Monitoring mobile data usage")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    fun showHighUsageNotification(totalBytes: Long) {
        val mb = totalBytes / (1024 * 1024)
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            1,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, ALERT_CHANNEL_ID)
            .setContentTitle("High Data Usage")
            .setContentText("Used ${mb}MB today")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManager.notify(HIGH_USAGE_NOTIFICATION_ID, notification)
    }

    fun showIdleNotification() {
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            2,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, IDLE_CHANNEL_ID)
            .setContentTitle("Device Idle")
            .setContentText("Mobile data disabled due to inactivity")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notificationManager.notify(IDLE_NOTIFICATION_ID, notification)
    }

    fun showAppHighUsageNotification(appName: String, bytes: Long) {
        val mb = bytes / (1024 * 1024)
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            3,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, ALERT_CHANNEL_ID)
            .setContentTitle("App High Usage")
            .setContentText("$appName used ${mb}MB")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManager.notify(APP_USAGE_NOTIFICATION_ID, notification)
    }

    companion object {
        const val FOREGROUND_CHANNEL_ID = "dataguard_foreground"
        const val ALERT_CHANNEL_ID = "dataguard_alerts"
        const val IDLE_CHANNEL_ID = "dataguard_idle"

        const val FOREGROUND_NOTIFICATION_ID = 1
        const val HIGH_USAGE_NOTIFICATION_ID = 2
        const val IDLE_NOTIFICATION_ID = 3
        const val APP_USAGE_NOTIFICATION_ID = 4
    }
}
