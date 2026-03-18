package com.louis.mycomposeproject.domain.usecase

import com.louis.mycomposeproject.domain.model.User
import com.louis.mycomposeproject.domain.repository.AuthRepository

class RegisterUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String, name: String): Result<User> {
        if (email.isBlank() || password.isBlank() || name.isBlank()) {
            return Result.failure(Exception("All fields are required"))
        }
        if (password.length < 6) {
            return Result.failure(Exception("Password must be at least 6 characters"))
        }
        return repository.register(email, password, name)
    }
}
