package com.vasist.blackbox.core.hardware

import com.vasist.blackbox.domain.model.SensorReadings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HardwareSensorRepository @Inject constructor(
    private val orientationManager: OrientationSensorManager,
    private val chargingMonitor: ChargingStateMonitor,
    private val volumeMonitor: AudioVolumeMonitor,
    private val micMonitor: MicrophoneLoudnessMonitor,
    private val screenPowerMonitor: ScreenPowerStateMonitor,
    private val lightProximityMonitor: LightProximityMonitor,
    private val locationTracker: LocationTracker
) {
    fun observeAllSensors(): Flow<SensorReadings> {
        val flow1 = combine(
            orientationManager.observeOrientation(),
            chargingMonitor.observeChargingState(),
            volumeMonitor.observeSpeakerVolume(),
            micMonitor.observeDecibels()
        ) { orientation, isCharging, volume, micDb ->
            SensorReadings(
                isUpsideDown = orientation.isUpsideDown,
                pitch = orientation.pitch,
                roll = orientation.roll,
                isShaking = orientation.isShaking,
                isCharging = isCharging,
                speakerVolumePercent = volume,
                micDecibels = micDb
            )
        }

        val flow2 = combine(
            screenPowerMonitor.observeScreenState(),
            lightProximityMonitor.observeLightAndProximity(),
            locationTracker.observeDistanceMoved()
        ) { screenData, lightProx, distanceMeters ->
            Triple(screenData, lightProx, distanceMeters)
        }

        return combine(flow1, flow2) { readings, (screenData, lightProx, distanceMeters) ->
            readings.copy(
                isScreenOn = screenData.isScreenOn,
                lastScreenOffDurationMs = screenData.lastOffDurationMs,
                distanceMovedMeters = distanceMeters,
                lightLux = lightProx.lightLux,
                isProximityNear = lightProx.isProximityNear
            )
        }
    }
}
