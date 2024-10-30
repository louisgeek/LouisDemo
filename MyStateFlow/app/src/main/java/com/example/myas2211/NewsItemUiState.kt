package com.example.myas2211

data class NewsItemUiState(
    val title: String,
    val body: String,
    val bookmarked: Boolean = false,
)