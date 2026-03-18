package com.louis.mycomposeproject.data.remote

data class ApiResponse<T>(
    val success: Boolean,
    val data: T?,
    val message: String?
)

data class ErrorResponse(
    val error: String,
    val code: Int
)
