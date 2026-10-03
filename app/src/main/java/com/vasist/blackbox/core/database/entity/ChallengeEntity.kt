package com.vasist.blackbox.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "challenges")
data class ChallengeEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String,
    val hint: String,
    val type: String,
    val isUnlocked: Boolean,
    val isCompleted: Boolean,
    val progress: Float,
    val colorHex: Long
)
