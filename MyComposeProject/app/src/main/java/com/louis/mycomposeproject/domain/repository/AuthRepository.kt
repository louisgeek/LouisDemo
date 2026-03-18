package com.louis.mycomposeproject.domain.repository

import com.louis.mycomposeproject.domain.model.User

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<User>
    suspend fun register(email: String, password: String, name: String): Result<User>
    fun logout()
    fun isLoggedIn(): Boolean
}
