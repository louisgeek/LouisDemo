package com.louis.myarchitecture.ui

import com.louis.myarchitecture.model.News

sealed class NewsUiState {
    object Loading : NewsUiState()
    data class Success(val news: List<News>) : NewsUiState()
    data class Error(val msg: String) : NewsUiState()
}