package com.vasist.blackbox.ui.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vasist.blackbox.domain.model.Challenge
import com.vasist.blackbox.domain.repository.ChallengeRepository
import com.vasist.blackbox.domain.usecase.EvaluateChallengeProgressUseCase
import com.vasist.blackbox.domain.usecase.GetChallengesUseCase
import com.vasist.blackbox.domain.usecase.ObserveSensorReadingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    getChallengesUseCase: GetChallengesUseCase,
    observeSensorReadingsUseCase: ObserveSensorReadingsUseCase,
    private val evaluateChallengeProgressUseCase: EvaluateChallengeProgressUseCase,
    private val challengeRepository: ChallengeRepository
) : ViewModel() {

    private val selectedChallengeId = MutableStateFlow<String?>(null)

    val uiState: StateFlow<GameUiState> = combine(
        getChallengesUseCase(),
        observeSensorReadingsUseCase(),
        selectedChallengeId
    ) { challenges, readings, selectedId ->
        // Evaluate live sensor inputs against incomplete challenges
        viewModelScope.launch {
            evaluateChallengeProgressUseCase(challenges, readings)
        }

        val completed = challenges.count { it.isCompleted }
        val activeSelected = challenges.find { it.id == selectedId }

        GameUiState(
            challenges = challenges,
            selectedChallenge = activeSelected,
            sensorReadings = readings,
            completedCount = completed,
            totalCount = challenges.size,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = GameUiState()
    )

    fun selectChallenge(challenge: Challenge?) {
        selectedChallengeId.value = challenge?.id
    }

    fun resetGame() {
        viewModelScope.launch {
            challengeRepository.resetAllChallenges()
        }
    }
}
