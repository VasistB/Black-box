package com.vasist.blackbox.domain.model

data class Record(
    val id: Long,
    val title: String,
    val description: String,
    val imageUrl: String,
    val timestamp: Long
)
