package com.louis.mycomposeproject.domain.usecase

import com.louis.mycomposeproject.domain.model.User
import com.louis.mycomposeproject.domain.repository.AuthRepository
import com.louis.mycomposeproject.util.Validator

class RegisterUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String, name: String): Result<User> {
        if (name.isBlank()) {
            return Result.failure(Exception("Name cannot be empty"))
        }
        if (!Validator.isValidName(name)) {
            return Result.failure(Exception("Name must be at least 2 characters"))
        }
        if (email.isBlank()) {
            return Result.failure(Exception("Email cannot be empty"))
        }
        if (!Validator.isValidEmail(email)) {
            return Result.failure(Exception("Invalid email format"))
        }
        if (password.isBlank()) {
            return Result.failure(Exception("Password cannot be empty"))
        }
        if (!Validator.isValidPassword(password)) {
            return Result.failure(Exception("Password must be at least 6 characters"))
        }
        return repository.register(email, password, name)
    }
}
