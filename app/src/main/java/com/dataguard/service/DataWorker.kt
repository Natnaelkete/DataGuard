package com.dataguard.service

import android.content.Context
import android.net.TrafficStats
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.dataguard.domain.usecase.DetectIdleUseCase
import com.dataguard.domain.usecase.GetUsageStatsUseCase
import com.dataguard.domain.usecase.ToggleMobileDataUseCase
import com.dataguard.utils.NotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class DataWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val getUsageStatsUseCase: GetUsageStatsUseCase,
    private val detectIdleUseCase: DetectIdleUseCase,
    private val toggleMobileDataUseCase: ToggleMobileDataUseCase,
    private val notificationHelper: NotificationHelper
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        return try {
            // Get current usage stats
            val result = getUsageStatsUseCase("daily")

            // Check for idle state
            val isIdle = detectIdleUseCase(5)
            if (isIdle) {
                toggleMobileDataUseCase(false, "idle")
                notificationHelper.showIdleNotification()
            }

            // Check total mobile data usage
            val totalMobile = TrafficStats.getMobileTxBytes() + TrafficStats.getMobileRxBytes()
            val limitBytes = 500 * 1024 * 1024 // 500MB default
            if (totalMobile > limitBytes) {
                notificationHelper.showHighUsageNotification(totalMobile)
            }

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}
