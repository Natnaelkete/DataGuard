package com.dataguard.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.dataguard.utils.NotificationHelper
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class DataMonitorService : Service() {

    @Inject
    lateinit var notificationHelper: NotificationHelper

    private lateinit var workManager: WorkManager

    override fun onCreate() {
        super.onCreate()
        workManager = WorkManager.getInstance(this)
        startDataTracking()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = notificationHelper.createForegroundNotification()
        startForeground(NotificationHelper.FOREGROUND_NOTIFICATION_ID, notification)
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun startDataTracking() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val dataTrackingWork = PeriodicWorkRequestBuilder<DataWorker>(
            30,
            TimeUnit.SECONDS
        ).setConstraints(constraints).build()

        workManager.enqueueUniquePeriodicWork(
            "data_tracking",
            ExistingPeriodicWorkPolicy.KEEP,
            dataTrackingWork
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        workManager.cancelUniqueWork("data_tracking")
    }
}
