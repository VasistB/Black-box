package com.vasist.blackbox.core.hardware

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

data class ScreenPowerData(
    val isScreenOn: Boolean,
    val lastOffDurationMs: Long
)

@Singleton
class ScreenPowerStateMonitor @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var screenOffTimestamp = 0L

    fun observeScreenState(): Flow<ScreenPowerData> = callbackFlow {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                when (intent.action) {
                    Intent.ACTION_SCREEN_OFF -> {
                        screenOffTimestamp = System.currentTimeMillis()
                        trySend(ScreenPowerData(isScreenOn = false, lastOffDurationMs = 0L))
                    }
                    Intent.ACTION_SCREEN_ON -> {
                        val duration = if (screenOffTimestamp > 0) {
                            System.currentTimeMillis() - screenOffTimestamp
                        } else {
                            0L
                        }
                        trySend(ScreenPowerData(isScreenOn = true, lastOffDurationMs = duration))
                    }
                }
            }
        }

        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_OFF)
            addAction(Intent.ACTION_SCREEN_ON)
        }

        context.registerReceiver(receiver, filter)

        trySend(ScreenPowerData(isScreenOn = true, lastOffDurationMs = 0L))

        awaitClose {
            try {
                context.unregisterReceiver(receiver)
            } catch (_: Exception) {}
        }
    }
}
