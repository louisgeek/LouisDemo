package com.louis.mycomposeproject.util

import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

object NetworkErrorHandler {
    fun getErrorMessage(throwable: Throwable): String {
        return when (throwable) {
            is HttpException -> {
                when (throwable.code()) {
                    400 -> "Invalid request"
                    401 -> "Unauthorized - Invalid credentials"
                    403 -> "Access forbidden"
                    404 -> "Resource not found"
                    409 -> "User already exists"
                    500 -> "Server error"
                    else -> "Network error: ${throwable.code()}"
                }
            }
            is SocketTimeoutException -> "Connection timeout"
            is IOException -> "Network connection failed"
            else -> throwable.message ?: "Unknown error occurred"
        }
    }
}
