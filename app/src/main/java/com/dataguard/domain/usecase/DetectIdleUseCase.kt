package com.dataguard.domain.usecase

import android.app.usage.UsageStatsManager
import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DetectIdleUseCase @Inject constructor(
    private val context: Context
) {
    private val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

    suspend operator fun invoke(idleThresholdMinutes: Long = 5): Boolean = withContext(Dispatchers.Default) {
        try {
            val currentTime = System.currentTimeMillis()
            val startTime = currentTime - (idleThresholdMinutes * 60 * 1000)

            val stats = usageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_DAILY,
                startTime,
                currentTime
            )

            // Check if any app has been in foreground in the last idleThresholdMinutes
            val hasForegroundApp = stats.any { stat ->
                stat.lastTimeUsed > startTime && stat.lastTimeUsed <= currentTime
            }

            !hasForegroundApp
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
