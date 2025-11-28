package com.dataguard.domain.usecase

import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.PackageManager
import android.net.TrafficStats
import android.os.Build
import com.dataguard.data.entity.AppDataUsage
import com.dataguard.data.entity.UsageEntity
import com.dataguard.data.repository.DataRepository
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetUsageStatsUseCase @Inject constructor(
    private val context: Context,
    private val repository: DataRepository,
    private val gson: Gson
) {
    private val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
    private val packageManager = context.packageManager

    suspend operator fun invoke(period: String = "daily"): Result = withContext(Dispatchers.Default) {
        try {
            val totalTx = TrafficStats.getMobileTxBytes()
            val totalRx = TrafficStats.getMobileRxBytes()

            val topApps = getTopDataConsumingApps(10)
            val topAppsJson = gson.toJson(topApps)

            val usage = UsageEntity(
                timestamp = System.currentTimeMillis(),
                totalMobileTx = totalTx,
                totalMobileRx = totalRx,
                topAppsJson = topAppsJson,
                period = period
            )

            repository.insertUsage(usage)
            Result.Success(usage)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    private fun getTopDataConsumingApps(limit: Int): List<AppDataUsage> {
        val appDataMap = mutableMapOf<Int, AppDataUsage>()

        try {
            val packages = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.getInstalledPackages(PackageManager.PackageInfoFlags.of(0L))
            } else {
                @Suppress("DEPRECATION")
                packageManager.getInstalledPackages(0)
            }

            for (pkg in packages) {
                val uid = pkg.applicationInfo.uid
                val tx = TrafficStats.getUidTxBytes(uid)
                val rx = TrafficStats.getUidRxBytes(uid)

                if (tx > 0 || rx > 0) {
                    val appName = try {
                        packageManager.getApplicationLabel(pkg.applicationInfo).toString()
                    } catch (e: Exception) {
                        pkg.packageName
                    }

                    appDataMap[uid] = AppDataUsage(
                        uid = uid,
                        packageName = pkg.packageName,
                        appName = appName,
                        txBytes = tx,
                        rxBytes = rx,
                        totalBytes = tx + rx
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return appDataMap.values
            .sortedByDescending { it.totalBytes }
            .take(limit)
    }

    sealed class Result {
        data class Success(val usage: UsageEntity) : Result()
        data class Error(val exception: Exception) : Result()
    }
}
