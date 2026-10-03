package com.vasist.blackbox.data.repository

import com.vasist.blackbox.core.database.dao.ChallengeDao
import com.vasist.blackbox.core.database.entity.ChallengeEntity
import com.vasist.blackbox.core.network.IoDispatcher
import com.vasist.blackbox.domain.model.Challenge
import com.vasist.blackbox.domain.model.ChallengeType
import com.vasist.blackbox.domain.repository.ChallengeRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ChallengeRepositoryImpl @Inject constructor(
    private val challengeDao: ChallengeDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ChallengeRepository {

    private val defaultChallenges = listOf(
        ChallengeEntity(
            id = "c1_upside_down",
            title = "Gravity Inversion",
            description = "Flip your phone completely upside down.",
            hint = "Turn your world on its head.",
            type = ChallengeType.UPSIDE_DOWN.name,
            isUnlocked = true,
            isCompleted = false,
            progress = 0f,
            colorHex = 0xFFFF5252
        ),
        ChallengeEntity(
            id = "c2_charger_plug",
            title = "Spark of Life",
            description = "Connect a charger to power up the BlackBox.",
            hint = "Energy flows through the cable.",
            type = ChallengeType.CHARGER_PLUG.name,
            isUnlocked = true,
            isCompleted = false,
            progress = 0f,
            colorHex = 0xFFFFD700
        ),
        ChallengeEntity(
            id = "c3_shout_whisper",
            title = "Voice of Thunder",
            description = "Shout or talk loudly into the microphone!",
            hint = "Make some noise!",
            type = ChallengeType.SHOUT_WHISPER.name,
            isUnlocked = true,
            isCompleted = false,
            progress = 0f,
            colorHex = 0xFF7C4DFF
        ),
        ChallengeEntity(
            id = "c4_screen_off",
            title = "Dark Meditation",
            description = "Press power button to turn off screen for 5 seconds.",
            hint = "Embrace total darkness.",
            type = ChallengeType.SCREEN_OFF_HOLD.name,
            isUnlocked = true,
            isCompleted = false,
            progress = 0f,
            colorHex = 0xFF00E676
        ),
        ChallengeEntity(
            id = "c5_volume_extremes",
            title = "Sonic Spectrum",
            description = "Crank speaker volume to 100% or Mute to 0%.",
            hint = "Push volume to the max!",
            type = ChallengeType.VOLUME_EXTREMES.name,
            isUnlocked = true,
            isCompleted = false,
            progress = 0f,
            colorHex = 0xFF00B0FF
        ),
        ChallengeEntity(
            id = "c6_gps_wanderer",
            title = "The Wanderer",
            description = "Move at least 10 meters in the real world.",
            hint = "Step outside and take a walk.",
            type = ChallengeType.GPS_WANDERER.name,
            isUnlocked = true,
            isCompleted = false,
            progress = 0f,
            colorHex = 0xFFFF9100
        ),
        ChallengeEntity(
            id = "c7_shake_it",
            title = "Earthquake",
            description = "Shake your phone vigorously!",
            hint = "Break the box with motion.",
            type = ChallengeType.SHAKE_IT.name,
            isUnlocked = true,
            isCompleted = false,
            progress = 0f,
            colorHex = 0xFFFF4081
        )
    )

    override fun getChallenges(): Flow<List<Challenge>> {
        return challengeDao.getAllChallenges().map { entities ->
            if (entities.isEmpty()) {
                withContext(ioDispatcher) {
                    challengeDao.insertChallenges(defaultChallenges)
                }
                defaultChallenges.map { it.toDomain() }
            } else {
                entities.map { it.toDomain() }
            }
        }.flowOn(ioDispatcher)
    }

    override suspend fun updateChallengeProgress(challengeId: String, progress: Float, isCompleted: Boolean) {
        withContext(ioDispatcher) {
            val list = challengeDao.getAllChallenges().firstOrNull() ?: emptyList()
            val existing = list.find { it.id == challengeId }
            if (existing != null) {
                val updated = existing.copy(
                    progress = progress.coerceIn(0f, 1f),
                    isCompleted = existing.isCompleted || isCompleted
                )
                challengeDao.updateChallenge(updated)
            }
        }
    }

    override suspend fun resetAllChallenges() {
        withContext(ioDispatcher) {
            challengeDao.insertChallenges(defaultChallenges)
        }
    }

    private fun ChallengeEntity.toDomain() = Challenge(
        id = id,
        title = title,
        description = description,
        hint = hint,
        type = ChallengeType.valueOf(type),
        isUnlocked = isUnlocked,
        isCompleted = isCompleted,
        progress = progress,
        colorHex = colorHex
    )
}
