package com.vasist.blackbox.data.repository

import com.vasist.blackbox.core.database.dao.RecordDao
import com.vasist.blackbox.core.database.entity.RecordEntity
import com.vasist.blackbox.core.network.IoDispatcher
import com.vasist.blackbox.domain.model.Record
import com.vasist.blackbox.domain.repository.RecordRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RecordRepositoryImpl @Inject constructor(
    private val recordDao: RecordDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : RecordRepository {

    override fun getRecords(): Flow<List<Record>> {
        return recordDao.getAllRecords().map { entities ->
            entities.map { entity ->
                Record(
                    id = entity.id,
                    title = entity.title,
                    description = entity.description,
                    imageUrl = entity.imageUrl,
                    timestamp = entity.timestamp
                )
            }
        }.flowOn(ioDispatcher)
    }

    override suspend fun addRecord(title: String, description: String, imageUrl: String) {
        withContext(ioDispatcher) {
            val entity = RecordEntity(
                title = title,
                description = description,
                imageUrl = imageUrl
            )
            recordDao.insertRecord(entity)
        }
    }

    override suspend fun deleteRecord(record: Record) {
        withContext(ioDispatcher) {
            val entity = RecordEntity(
                id = record.id,
                title = record.title,
                description = record.description,
                imageUrl = record.imageUrl,
                timestamp = record.timestamp
            )
            recordDao.deleteRecord(entity)
        }
    }
}
