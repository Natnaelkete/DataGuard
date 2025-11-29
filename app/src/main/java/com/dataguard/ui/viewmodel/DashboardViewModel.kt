package com.dataguard.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dataguard.data.entity.UsageEntity
import com.dataguard.data.repository.DataRepository
import com.dataguard.domain.usecase.GetUsageStatsUseCase
import com.dataguard.domain.usecase.ToggleMobileDataUseCase
import com.dataguard.utils.DataFormatter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getUsageStatsUseCase: GetUsageStatsUseCase,
    private val toggleMobileDataUseCase: ToggleMobileDataUseCase,
    private val repository: DataRepository
) : ViewModel() {

    private val _latestUsage = MutableStateFlow<UsageEntity?>(null)
    val latestUsage: StateFlow<UsageEntity?> = _latestUsage.asStateFlow()

    private val _dailyUsage = MutableStateFlow<List<UsageEntity>>(emptyList())
    val dailyUsage: StateFlow<List<UsageEntity>> = _dailyUsage.asStateFlow()

    private val _weeklyUsage = MutableStateFlow<List<UsageEntity>>(emptyList())
    val weeklyUsage: StateFlow<List<UsageEntity>> = _weeklyUsage.asStateFlow()

    private val _monthlyUsage = MutableStateFlow<List<UsageEntity>>(emptyList())
    val monthlyUsage: StateFlow<List<UsageEntity>> = _monthlyUsage.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadUsageData()
    }

    fun loadUsageData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                getUsageStatsUseCase("daily")
                
                // Get latest usage
                try {
                    val usage = repository.getLatestUsage().first()
                    _latestUsage.value = usage
                } catch (e: Exception) {
                    _latestUsage.value = null
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
        
        // Load period data in separate coroutines to avoid blocking
        viewModelScope.launch {
            try {
                repository.getUsageByPeriod("daily", 100).collect { usage ->
                    _dailyUsage.value = usage
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
        
        viewModelScope.launch {
            try {
                repository.getUsageByPeriod("weekly", 100).collect { usage ->
                    _weeklyUsage.value = usage
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
        
        viewModelScope.launch {
            try {
                repository.getUsageByPeriod("monthly", 100).collect { usage ->
                    _monthlyUsage.value = usage
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun toggleMobileData(enabled: Boolean) {
        viewModelScope.launch {
            val result = toggleMobileDataUseCase(enabled)
            if (result is ToggleMobileDataUseCase.Result.Error) {
                _error.value = "Please toggle mobile data manually in Settings"
            } else {
                _error.value = "Opening mobile data settings..."
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun getTotalDailyUsageMB(): Double {
        val total = _dailyUsage.value.sumOf { it.totalMobileTx + it.totalMobileRx }
        return total / (1024.0 * 1024.0)
    }

    fun getTotalWeeklyUsageMB(): Double {
        val total = _weeklyUsage.value.sumOf { it.totalMobileTx + it.totalMobileRx }
        return total / (1024.0 * 1024.0)
    }

    fun getTotalMonthlyUsageMB(): Double {
        val total = _monthlyUsage.value.sumOf { it.totalMobileTx + it.totalMobileRx }
        return total / (1024.0 * 1024.0)
    }
}
