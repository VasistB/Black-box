package com.vasist.blackbox.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "records")
data class RecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val imageUrl: String,
    val timestamp: Long = System.currentTimeMillis()
)
