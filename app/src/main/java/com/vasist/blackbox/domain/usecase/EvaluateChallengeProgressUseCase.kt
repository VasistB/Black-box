package com.vasist.blackbox.domain.usecase

import com.vasist.blackbox.domain.model.Challenge
import com.vasist.blackbox.domain.model.ChallengeType
import com.vasist.blackbox.domain.model.SensorReadings
import com.vasist.blackbox.domain.repository.ChallengeRepository
import javax.inject.Inject

class EvaluateChallengeProgressUseCase @Inject constructor(
    private val challengeRepository: ChallengeRepository
) {
    suspend operator fun invoke(challenges: List<Challenge>, readings: SensorReadings) {
        challenges.filter { !it.isCompleted }.forEach { challenge ->
            when (challenge.type) {
                ChallengeType.UPSIDE_DOWN -> {
                    if (readings.isUpsideDown) {
                        val newProgress = (challenge.progress + 0.35f).coerceAtMost(1.0f)
                        val isDone = newProgress >= 1.0f
                        challengeRepository.updateChallengeProgress(challenge.id, newProgress, isDone)
                    }
                }
                ChallengeType.CHARGER_PLUG -> {
                    if (readings.isCharging) {
                        challengeRepository.updateChallengeProgress(challenge.id, 1.0f, true)
                    }
                }
                ChallengeType.SHOUT_WHISPER -> {
                    if (readings.micDecibels > 75.0) {
                        challengeRepository.updateChallengeProgress(challenge.id, 1.0f, true)
                    } else if (readings.micDecibels > 50.0) {
                        val p = (readings.micDecibels / 75.0).toFloat()
                        challengeRepository.updateChallengeProgress(challenge.id, p, false)
                    }
                }
                ChallengeType.SCREEN_OFF_HOLD -> {
                    if (readings.lastScreenOffDurationMs >= 3000L || readings.isProximityNear || readings.lightLux < 2f) {
                        challengeRepository.updateChallengeProgress(challenge.id, 1.0f, true)
                    }
                }
                ChallengeType.VOLUME_EXTREMES -> {
                    if (readings.speakerVolumePercent >= 95 || readings.speakerVolumePercent == 0) {
                        challengeRepository.updateChallengeProgress(challenge.id, 1.0f, true)
                    }
                }
                ChallengeType.GPS_WANDERER -> {
                    val p = (readings.distanceMovedMeters / 10f).coerceIn(0f, 1f)
                    challengeRepository.updateChallengeProgress(challenge.id, p, p >= 1.0f)
                }
                ChallengeType.SHAKE_IT -> {
                    if (readings.isShaking) {
                        val newProgress = (challenge.progress + 0.4f).coerceAtMost(1.0f)
                        challengeRepository.updateChallengeProgress(challenge.id, newProgress, newProgress >= 1.0f)
                    }
                }
            }
        }
    }
}
