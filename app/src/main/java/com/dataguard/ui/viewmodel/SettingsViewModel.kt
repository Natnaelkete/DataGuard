package com.dataguard.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dataguard.data.repository.DataRepository
import com.dataguard.utils.PermissionHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: DataRepository,
    private val permissionHelper: PermissionHelper
) : ViewModel() {

    private val _hasUsageStatsPermission = MutableStateFlow(false)
    val hasUsageStatsPermission: StateFlow<Boolean> = _hasUsageStatsPermission.asStateFlow()

    private val _hasBatteryOptimizationExemption = MutableStateFlow(false)
    val hasBatteryOptimizationExemption: StateFlow<Boolean> = _hasBatteryOptimizationExemption.asStateFlow()

    private val _hasNotificationPermission = MutableStateFlow(false)
    val hasNotificationPermission: StateFlow<Boolean> = _hasNotificationPermission.asStateFlow()

    private val _dailyLimitMB = MutableStateFlow(500L)
    val dailyLimitMB: StateFlow<Long> = _dailyLimitMB.asStateFlow()

    private val _weeklyLimitMB = MutableStateFlow(3500L)
    val weeklyLimitMB: StateFlow<Long> = _weeklyLimitMB.asStateFlow()

    private val _monthlyLimitMB = MutableStateFlow(15000L)
    val monthlyLimitMB: StateFlow<Long> = _monthlyLimitMB.asStateFlow()

    init {
        checkPermissions()
    }

    fun checkPermissions() {
        _hasUsageStatsPermission.value = permissionHelper.hasPackageUsageStatsPermission()
        _hasBatteryOptimizationExemption.value = permissionHelper.isBatteryOptimizationIgnored()
        _hasNotificationPermission.value = permissionHelper.hasNotificationPermission()
    }

    fun requestUsageStatsPermission() {
        permissionHelper.requestPackageUsageStatsPermission()
    }

    fun requestBatteryOptimizationExemption() {
        permissionHelper.requestBatteryOptimizationExemption()
        // Schedule a check after a delay to allow user to grant permission
        viewModelScope.launch {
            kotlinx.coroutines.delay(500)
            checkPermissions()
        }
    }

    fun requestNotificationPermission() {
        permissionHelper.requestNotificationPermission()
        // Schedule a check after a delay to allow user to grant permission
        viewModelScope.launch {
            kotlinx.coroutines.delay(500)
            checkPermissions()
        }
    }

    fun setDailyLimit(limitMB: Long) {
        _dailyLimitMB.value = limitMB
    }

    fun setWeeklyLimit(limitMB: Long) {
        _weeklyLimitMB.value = limitMB
    }

    fun setMonthlyLimit(limitMB: Long) {
        _monthlyLimitMB.value = limitMB
    }

    fun resetAllStats() {
        viewModelScope.launch {
            repository.clearAllUsage()
            repository.clearAllBlockedApps()
            repository.clearAllAppLimits()
            repository.clearAllHistory()
        }
    }
}
