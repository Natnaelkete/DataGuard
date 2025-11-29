package com.dataguard.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.dataguard.domain.usecase.ToggleMobileDataUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ScreenLockReceiver : BroadcastReceiver() {
    @Inject
    lateinit var toggleMobileDataUseCase: ToggleMobileDataUseCase

    private var screenLockTimer: Handler? = null
    private val lockTimeoutMs = 60000L // 1 minute

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null) return

        when (intent?.action) {
            Intent.ACTION_SCREEN_OFF -> {
                // Screen locked - start timer to disable mobile data after 1 minute
                Log.d("ScreenLockReceiver", "Screen locked")
                startScreenLockTimer(context)
            }
            Intent.ACTION_SCREEN_ON -> {
                // Screen unlocked - cancel timer
                Log.d("ScreenLockReceiver", "Screen unlocked")
                cancelScreenLockTimer()
            }
        }
    }

    private fun startScreenLockTimer(context: Context) {
        // Cancel any existing timer
        cancelScreenLockTimer()

        screenLockTimer = Handler(Looper.getMainLooper())
        screenLockTimer?.postDelayed({
            Log.d("ScreenLockReceiver", "Disabling mobile data after screen lock timeout")
            // Disable mobile data after 1 minute of screen being locked
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    toggleMobileDataUseCase(false, "screen_lock")
                } catch (e: Exception) {
                    Log.e("ScreenLockReceiver", "Failed to disable mobile data", e)
                }
            }
        }, lockTimeoutMs)
    }

    private fun cancelScreenLockTimer() {
        screenLockTimer?.removeCallbacksAndMessages(null)
        screenLockTimer = null
    }
}
