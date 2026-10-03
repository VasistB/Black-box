package com.vasist.blackbox.domain.usecase

import com.vasist.blackbox.domain.repository.RecordRepository
import javax.inject.Inject

class AddRecordUseCase @Inject constructor(
    private val repository: RecordRepository
) {
    suspend operator fun invoke(title: String, description: String, imageUrl: String) {
        if (title.isBlank()) {
            throw IllegalArgumentException("Title cannot be empty")
        }
        repository.addRecord(
            title = title.trim(),
            description = description.trim(),
            imageUrl = imageUrl.trim()
        )
    }
}
