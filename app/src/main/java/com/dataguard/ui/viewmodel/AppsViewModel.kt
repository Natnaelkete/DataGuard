package com.dataguard.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dataguard.data.entity.AppDataUsage
import com.dataguard.data.entity.BlockedAppEntity
import com.dataguard.data.repository.DataRepository
import com.dataguard.domain.usecase.BlockAppDataUseCase
import com.dataguard.domain.usecase.GetUsageStatsUseCase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppsViewModel @Inject constructor(
    private val getUsageStatsUseCase: GetUsageStatsUseCase,
    private val blockAppDataUseCase: BlockAppDataUseCase,
    private val repository: DataRepository,
    private val gson: Gson
) : ViewModel() {

    private val _topApps = MutableStateFlow<List<AppDataUsage>>(emptyList())
    val topApps: StateFlow<List<AppDataUsage>> = _topApps.asStateFlow()

    private val _blockedApps = MutableStateFlow<List<BlockedAppEntity>>(emptyList())
    val blockedApps: StateFlow<List<BlockedAppEntity>> = _blockedApps.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadAppsData()
    }

    fun loadAppsData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                getUsageStatsUseCase("daily")
                
                // Collect latest usage
                repository.getLatestUsage().collect { usage ->
                    if (usage != null) {
                        val type = object : TypeToken<List<AppDataUsage>>() {}.type
                        val apps = gson.fromJson<List<AppDataUsage>>(usage.topAppsJson, type) ?: emptyList()
                        _topApps.value = apps.sortedByDescending { it.totalBytes }
                    }
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
        
        // Load blocked apps in separate coroutine
        viewModelScope.launch {
            try {
                repository.getAllBlockedApps().collect { blocked ->
                    _blockedApps.value = blocked
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun toggleBlockApp(app: AppDataUsage, block: Boolean) {
        viewModelScope.launch {
            val result = blockAppDataUseCase(app.uid, app.packageName, app.appName, block)
            if (result is BlockAppDataUseCase.Result.Error) {
                _error.value = result.message
            } else {
                loadAppsData()
            }
        }
    }

    fun isAppBlocked(uid: Int): Boolean {
        return _blockedApps.value.any { it.uid == uid }
    }

    fun clearError() {
        _error.value = null
    }
}
