package com.vasist.blackbox.domain.model

data class Challenge(
    val id: String,
    val title: String,
    val description: String,
    val hint: String,
    val type: ChallengeType,
    val isUnlocked: Boolean = true,
    val isCompleted: Boolean = false,
    val progress: Float = 0f,
    val colorHex: Long = 0xFF00E676
)
