package com.vasist.blackbox.domain.usecase

import com.vasist.blackbox.domain.model.Challenge
import com.vasist.blackbox.domain.repository.ChallengeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetChallengesUseCase @Inject constructor(
    private val challengeRepository: ChallengeRepository
) {
    operator fun invoke(): Flow<List<Challenge>> = challengeRepository.getChallenges()
}
