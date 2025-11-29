package com.dataguard.domain.usecase

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import com.dataguard.data.entity.DataToggleHistoryEntity
import com.dataguard.data.repository.DataRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ToggleMobileDataUseCase @Inject constructor(
    private val context: Context,
    private val repository: DataRepository
) {
    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

    suspend operator fun invoke(enabled: Boolean, reason: String = "manual"): Result = withContext(Dispatchers.Main) {
        return@withContext try {
            val success = when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                    toggleViaConnectivityManager(enabled)
                }
                else -> {
                    toggleViaSettings(enabled)
                }
            }

            if (success) {
                repository.insertToggleHistory(
                    DataToggleHistoryEntity(
                        timestamp = System.currentTimeMillis(),
                        enabled = enabled,
                        reason = reason
                    )
                )
                Result.Success
            } else {
                Result.Error("Failed to toggle mobile data")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown error")
        }
    }

    private fun toggleViaConnectivityManager(enabled: Boolean): Boolean {
        return try {
            // Modern Android (8+) restricts direct mobile data toggling
            // We need to open the mobile data settings for user to toggle manually
            val intent = Intent(Settings.ACTION_DATA_ROAMING_SETTINGS)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun toggleViaSettings(enabled: Boolean): Boolean {
        return try {
            val intent = Intent(Settings.ACTION_DATA_ROAMING_SETTINGS)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    sealed class Result {
        object Success : Result()
        data class Error(val message: String) : Result()
    }
}
