package com.dataguard.data.repository

import com.dataguard.data.dao.AppLimitDao
import com.dataguard.data.dao.BlockedAppDao
import com.dataguard.data.dao.DataToggleHistoryDao
import com.dataguard.data.dao.UsageDao
import com.dataguard.data.entity.AppLimitEntity
import com.dataguard.data.entity.BlockedAppEntity
import com.dataguard.data.entity.DataToggleHistoryEntity
import com.dataguard.data.entity.UsageEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DataRepository @Inject constructor(
    private val usageDao: UsageDao,
    private val blockedAppDao: BlockedAppDao,
    private val appLimitDao: AppLimitDao,
    private val dataToggleHistoryDao: DataToggleHistoryDao
) {
    // Usage operations
    suspend fun insertUsage(usage: UsageEntity) = usageDao.insertUsage(usage)

    fun getUsageByPeriod(period: String, limit: Int = 100): Flow<List<UsageEntity>> =
        usageDao.getUsageByPeriod(period, limit)

    fun getUsageInTimeRange(startTime: Long, endTime: Long): Flow<List<UsageEntity>> =
        usageDao.getUsageInTimeRange(startTime, endTime)

    fun getLatestUsage(): Flow<UsageEntity?> = usageDao.getLatestUsage()

    suspend fun deleteOldUsage(cutoffTime: Long) = usageDao.deleteOldUsage(cutoffTime)

    suspend fun clearAllUsage() = usageDao.clearAllUsage()

    // Blocked app operations
    suspend fun insertBlockedApp(app: BlockedAppEntity) = blockedAppDao.insertBlockedApp(app)

    suspend fun updateBlockedApp(app: BlockedAppEntity) = blockedAppDao.updateBlockedApp(app)

    suspend fun deleteBlockedApp(app: BlockedAppEntity) = blockedAppDao.deleteBlockedApp(app)

    fun getAllBlockedApps(): Flow<List<BlockedAppEntity>> = blockedAppDao.getAllBlockedApps()

    suspend fun getBlockedApp(uid: Int): BlockedAppEntity? = blockedAppDao.getBlockedApp(uid)

    suspend fun clearAllBlockedApps() = blockedAppDao.clearAllBlockedApps()

    // App limit operations
    suspend fun insertAppLimit(limit: AppLimitEntity) = appLimitDao.insertAppLimit(limit)

    suspend fun updateAppLimit(limit: AppLimitEntity) = appLimitDao.updateAppLimit(limit)

    suspend fun deleteAppLimit(limit: AppLimitEntity) = appLimitDao.deleteAppLimit(limit)

    fun getAllAppLimits(): Flow<List<AppLimitEntity>> = appLimitDao.getAllAppLimits()

    suspend fun getAppLimit(uid: Int): AppLimitEntity? = appLimitDao.getAppLimit(uid)

    suspend fun clearAllAppLimits() = appLimitDao.clearAllAppLimits()

    // Data toggle history operations
    suspend fun insertToggleHistory(history: DataToggleHistoryEntity) =
        dataToggleHistoryDao.insertToggleHistory(history)

    fun getToggleHistory(limit: Int = 100): Flow<List<DataToggleHistoryEntity>> =
        dataToggleHistoryDao.getToggleHistory(limit)

    suspend fun deleteOldHistory(cutoffTime: Long) = dataToggleHistoryDao.deleteOldHistory(cutoffTime)

    suspend fun clearAllHistory() = dataToggleHistoryDao.clearAllHistory()
}
