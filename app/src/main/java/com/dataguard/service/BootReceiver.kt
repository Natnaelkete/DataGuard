package com.dataguard.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED && context != null) {
            val workManager = WorkManager.getInstance(context)
            val dataTrackingWork = PeriodicWorkRequestBuilder<DataWorker>(
                30,
                TimeUnit.SECONDS
            ).build()

            workManager.enqueueUniquePeriodicWork(
                "data_tracking",
                ExistingPeriodicWorkPolicy.KEEP,
                dataTrackingWork
            )
        }
    }
}
