package com.dataguard.domain.usecase

import android.content.Context
import android.os.Build
import com.dataguard.data.entity.BlockedAppEntity
import com.dataguard.data.repository.DataRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class BlockAppDataUseCase @Inject constructor(
    private val context: Context,
    private val repository: DataRepository
) {
    // Note: NetworkPolicyManager is a hidden API, so we use reflection
    private val networkPolicyManager by lazy {
        try {
            context.getSystemService("netpolicy")
        } catch (e: Exception) {
            null
        }
    }

    suspend operator fun invoke(
        uid: Int,
        packageName: String,
        appName: String,
        block: Boolean
    ): Result = withContext(Dispatchers.Default) {
        return@withContext try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                applyNetworkPolicy(uid, block)
            }

            if (block) {
                repository.insertBlockedApp(
                    BlockedAppEntity(
                        uid = uid,
                        packageName = packageName,
                        appName = appName
                    )
                )
            } else {
                repository.deleteBlockedApp(
                    BlockedAppEntity(
                        uid = uid,
                        packageName = packageName,
                        appName = appName
                    )
                )
            }

            Result.Success
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown error")
        }
    }

    private fun applyNetworkPolicy(uid: Int, block: Boolean) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && networkPolicyManager != null) {
                // Use reflection to call hidden API methods
                val method = networkPolicyManager!!.javaClass.getMethod("getUidPolicy", Int::class.java)
                val policy = method.invoke(networkPolicyManager, uid) as Int
                
                val newPolicy = if (block) {
                    policy or 0x01 // POLICY_REJECT_METERED_BACKGROUND
                } else {
                    policy and 0x01.inv()
                }
                
                val setMethod = networkPolicyManager!!.javaClass.getMethod("setUidPolicy", Int::class.java, Int::class.java)
                setMethod.invoke(networkPolicyManager, uid, newPolicy)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    sealed class Result {
        object Success : Result()
        data class Error(val message: String) : Result()
    }
}
