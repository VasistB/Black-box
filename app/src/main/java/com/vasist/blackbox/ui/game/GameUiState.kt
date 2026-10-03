package com.vasist.blackbox.ui.game

import com.vasist.blackbox.domain.model.Challenge
import com.vasist.blackbox.domain.model.SensorReadings

data class GameUiState(
    val challenges: List<Challenge> = emptyList(),
    val selectedChallenge: Challenge? = null,
    val sensorReadings: SensorReadings = SensorReadings(),
    val completedCount: Int = 0,
    val totalCount: Int = 0,
    val isLoading: Boolean = true
)
