package com.vasist.blackbox.core.hardware

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.atan2
import kotlin.math.sqrt

data class OrientationData(
    val pitch: Float,
    val roll: Float,
    val isUpsideDown: Boolean,
    val isShaking: Boolean
)

@Singleton
class OrientationSensorManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val sensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    fun observeOrientation(): Flow<OrientationData> = callbackFlow {
        val accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)

        var lastShakeTime = 0L

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                if (event.sensor.type == Sensor.TYPE_ACCELEROMETER ||
                    event.sensor.type == Sensor.TYPE_GRAVITY
                ) {
                    val x = event.values[0]
                    val y = event.values[1]
                    val z = event.values[2]

                    // Calculate pitch and roll
                    val pitch = Math.toDegrees(atan2(-x.toDouble(), sqrt(y * y + z * z).toDouble())).toFloat()
                    val roll = Math.toDegrees(atan2(y.toDouble(), z.toDouble())).toFloat()

                    // Upside down detection (y axis pointing down strongly or roll around 180/-180)
                    val isUpsideDown = y < -7.0f || (roll > 150f || roll < -150f)

                    // Shake detection
                    val acceleration = sqrt((x * x + y * y + z * z).toDouble()).toFloat()
                    val now = System.currentTimeMillis()
                    val isShaking = if (acceleration > 18.0f && now - lastShakeTime > 500) {
                        lastShakeTime = now
                        true
                    } else {
                        false
                    }

                    trySend(
                        OrientationData(
                            pitch = pitch,
                            roll = roll,
                            isUpsideDown = isUpsideDown,
                            isShaking = isShaking
                        )
                    )
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        val targetSensor = gravitySensor ?: accelSensor
        if (targetSensor != null) {
            sensorManager.registerListener(listener, targetSensor, SensorManager.SENSOR_DELAY_GAME)
        }

        awaitClose {
            sensorManager.unregisterListener(listener)
        }
    }
}
