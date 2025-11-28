package com.dataguard.di

import android.content.Context
import com.dataguard.data.dao.AppLimitDao
import com.dataguard.data.dao.BlockedAppDao
import com.dataguard.data.dao.DataToggleHistoryDao
import com.dataguard.data.dao.UsageDao
import com.dataguard.data.database.DataGuardDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDataGuardDatabase(
        @ApplicationContext context: Context
    ): DataGuardDatabase = DataGuardDatabase.getInstance(context)

    @Singleton
    @Provides
    fun provideUsageDao(database: DataGuardDatabase): UsageDao = database.usageDao()

    @Singleton
    @Provides
    fun provideBlockedAppDao(database: DataGuardDatabase): BlockedAppDao = database.blockedAppDao()

    @Singleton
    @Provides
    fun provideAppLimitDao(database: DataGuardDatabase): AppLimitDao = database.appLimitDao()

    @Singleton
    @Provides
    fun provideDataToggleHistoryDao(database: DataGuardDatabase): DataToggleHistoryDao =
        database.dataToggleHistoryDao()
}
