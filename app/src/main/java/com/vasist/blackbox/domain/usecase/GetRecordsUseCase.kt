package com.vasist.blackbox.domain.usecase

import com.vasist.blackbox.domain.model.Record
import com.vasist.blackbox.domain.repository.RecordRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRecordsUseCase @Inject constructor(
    private val repository: RecordRepository
) {
    operator fun invoke(): Flow<List<Record>> {
        return repository.getRecords()
    }
}
