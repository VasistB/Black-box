package com.vasist.blackbox.domain.model

data class SensorReadings(
    val isUpsideDown: Boolean = false,
    val pitch: Float = 0f,
    val roll: Float = 0f,
    val isShaking: Boolean = false,
    val isCharging: Boolean = false,
    val speakerVolumePercent: Int = 50,
    val micDecibels: Double = 0.0,
    val isScreenOn: Boolean = true,
    val lastScreenOffDurationMs: Long = 0L,
    val distanceMovedMeters: Float = 0f,
    val lightLux: Float = 100f,
    val isProximityNear: Boolean = false
)
