package com.vasist.blackbox.core.hardware

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs
import kotlin.math.log10

@Singleton
class MicrophoneLoudnessMonitor @Inject constructor(
    @ApplicationContext private val context: Context
) {
    @SuppressLint("MissingPermission")
    fun observeDecibels(): Flow<Double> = callbackFlow {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            trySend(0.0)
            close()
            return@callbackFlow
        }

        val sampleRate = 44100
        val channelConfig = AudioFormat.CHANNEL_IN_MONO
        val audioFormat = AudioFormat.ENCODING_PCM_16BIT
        val bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)

        try {
            val audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                sampleRate,
                channelConfig,
                audioFormat,
                bufferSize
            )

            audioRecord.startRecording()

            val job = launch(Dispatchers.IO) {
                val buffer = ShortArray(bufferSize)
                while (isActive && audioRecord.recordingState == AudioRecord.RECORDSTATE_RECORDING) {
                    val readSize = audioRecord.read(buffer, 0, buffer.size)
                    if (readSize > 0) {
                        var maxAmplitude = 0
                        for (i in 0 until readSize) {
                            val absVal = abs(buffer[i].toInt())
                            if (absVal > maxAmplitude) {
                                maxAmplitude = absVal
                            }
                        }

                        val db = if (maxAmplitude > 0) {
                            20 * log10(maxAmplitude.toDouble() / 32767.0) + 90
                        } else {
                            0.0
                        }
                        trySend(db)
                    }
                }
            }

            awaitClose {
                job.cancel()
                try {
                    audioRecord.stop()
                    audioRecord.release()
                } catch (_: Exception) {}
            }
        } catch (_: Exception) {
            trySend(0.0)
            close()
        }
    }
}
