package com.vasist.blackbox.ui.main

import com.vasist.blackbox.domain.model.Record

sealed interface MainUiState {
    data object Loading : MainUiState
    data class Success(val records: List<Record>) : MainUiState
    data class Error(val message: String) : MainUiState
    data object Empty : MainUiState
}
