package com.vasist.blackbox.core.hardware

import android.content.Context
import android.database.ContentObserver
import android.media.AudioManager
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudioVolumeMonitor @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val audioManager =
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    fun observeSpeakerVolume(): Flow<Int> = callbackFlow {
        fun getCurrentVolumePercent(): Int {
            val current = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
            val max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            return if (max > 0) ((current.toFloat() / max) * 100).toInt() else 0
        }

        val observer = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) {
                trySend(getCurrentVolumePercent())
            }
        }

        context.contentResolver.registerContentObserver(
            Settings.System.CONTENT_URI,
            true,
            observer
        )

        trySend(getCurrentVolumePercent())

        awaitClose {
            context.contentResolver.unregisterContentObserver(observer)
        }
    }
}
