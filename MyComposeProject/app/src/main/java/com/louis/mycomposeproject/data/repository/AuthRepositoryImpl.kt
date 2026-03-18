package com.louis.mycomposeproject.data.repository

import com.louis.mycomposeproject.data.local.PreferencesManager
import com.louis.mycomposeproject.data.remote.AuthApi
import com.louis.mycomposeproject.data.remote.LoginRequest
import com.louis.mycomposeproject.data.remote.RegisterRequest
import com.louis.mycomposeproject.domain.model.User
import com.louis.mycomposeproject.domain.repository.AuthRepository
import com.louis.mycomposeproject.util.NetworkErrorHandler

class AuthRepositoryImpl(
    private val api: AuthApi,
    private val preferencesManager: PreferencesManager
) : AuthRepository {
    override suspend fun login(email: String, password: String): Result<User> {
        return try {
            val response = api.login(LoginRequest(email, password))
            val user = User(response.id, response.email, response.token)
            preferencesManager.saveToken(user.token)
            preferencesManager.saveUserId(user.id)
            preferencesManager.saveEmail(user.email)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(Exception(NetworkErrorHandler.getErrorMessage(e)))
        }
    }
    
    override suspend fun register(email: String, password: String, name: String): Result<User> {
        return try {
            val response = api.register(RegisterRequest(email, password, name))
            val user = User(response.id, response.email, response.token)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(Exception(NetworkErrorHandler.getErrorMessage(e)))
        }
    }
    
    override fun logout() {
        preferencesManager.clearAll()
    }
    
    override fun isLoggedIn(): Boolean {
        return preferencesManager.isLoggedIn()
    }
}
