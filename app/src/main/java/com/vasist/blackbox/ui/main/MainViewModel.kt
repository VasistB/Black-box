package com.vasist.blackbox.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vasist.blackbox.domain.usecase.AddRecordUseCase
import com.vasist.blackbox.domain.usecase.GetRecordsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    getRecordsUseCase: GetRecordsUseCase,
    private val addRecordUseCase: AddRecordUseCase,
) : ViewModel() {

    val uiState: StateFlow<MainUiState> = getRecordsUseCase()
        .map { records ->
            if (records.isEmpty()) {
                MainUiState.Empty
            } else {
                MainUiState.Success(records)
            }
        }
        .catch { throwable ->
            emit(MainUiState.Error(throwable.message ?: "An unexpected error occurred"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = MainUiState.Loading
        )

    fun addRecord(title: String, description: String, imageUrl: String) {
        viewModelScope.launch {
            try {
                addRecordUseCase(
                    title = title,
                    description = description,
                    imageUrl = imageUrl
                )
            } catch (_: Exception) {
                // Error handling can be posted to a SharedFlow/Channel for one-time UI events
            }
        }
    }
}
