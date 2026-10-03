package com.vasist.blackbox.domain.repository

import com.vasist.blackbox.domain.model.Record
import kotlinx.coroutines.flow.Flow

interface RecordRepository {
    fun getRecords(): Flow<List<Record>>
    suspend fun addRecord(title: String, description: String, imageUrl: String)
    suspend fun deleteRecord(record: Record)
}
