package com.vasist.blackbox.domain.repository

import com.vasist.blackbox.domain.model.Challenge
import kotlinx.coroutines.flow.Flow

interface ChallengeRepository {
    fun getChallenges(): Flow<List<Challenge>>
    suspend fun updateChallengeProgress(challengeId: String, progress: Float, isCompleted: Boolean)
    suspend fun resetAllChallenges()
}
