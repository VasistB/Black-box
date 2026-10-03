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

data class LightProximityData(
    val lightLux: Float,
    val isProximityNear: Boolean
)

@Singleton
class LightProximityMonitor @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val sensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    fun observeLightAndProximity(): Flow<LightProximityData> = callbackFlow {
        val lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        val proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)

        var currentLux = 100f
        var isNear = false

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                when (event.sensor.type) {
                    Sensor.TYPE_LIGHT -> {
                        currentLux = event.values[0]
                    }
                    Sensor.TYPE_PROXIMITY -> {
                        val maxRange = proximitySensor?.maximumRange ?: 5f
                        isNear = event.values[0] < maxRange
                    }
                }
                trySend(LightProximityData(lightLux = currentLux, isProximityNear = isNear))
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        if (lightSensor != null) {
            sensorManager.registerListener(listener, lightSensor, SensorManager.SENSOR_DELAY_UI)
        }
        if (proximitySensor != null) {
            sensorManager.registerListener(listener, proximitySensor, SensorManager.SENSOR_DELAY_UI)
        }

        awaitClose {
            sensorManager.unregisterListener(listener)
        }
    }
}
