package com.vasist.blackbox.domain.usecase

import com.vasist.blackbox.core.hardware.HardwareSensorRepository
import com.vasist.blackbox.domain.model.SensorReadings
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveSensorReadingsUseCase @Inject constructor(
    private val sensorRepository: HardwareSensorRepository
) {
    operator fun invoke(): Flow<SensorReadings> = sensorRepository.observeAllSensors()
}
