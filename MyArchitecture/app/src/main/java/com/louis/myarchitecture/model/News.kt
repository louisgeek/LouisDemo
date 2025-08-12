package com.louis.myarchitecture.model

/**
 * data class 优先使用 val
 */
data class News(
    val id: String,
    val title: String,
    val content: String,
    val publishedAt: String
)