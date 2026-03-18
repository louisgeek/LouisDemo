package com.louis.mycomposeproject.domain.usecase

import com.louis.mycomposeproject.domain.model.User
import com.louis.mycomposeproject.domain.repository.AuthRepository
import com.louis.mycomposeproject.util.Validator

class LoginUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String): Result<User> {
        if (email.isBlank()) {
            return Result.failure(Exception("Email cannot be empty"))
        }
        if (!Validator.isValidEmail(email)) {
            return Result.failure(Exception("Invalid email format"))
        }
        if (password.isBlank()) {
            return Result.failure(Exception("Password cannot be empty"))
        }
        return repository.login(email, password)
    }
}
