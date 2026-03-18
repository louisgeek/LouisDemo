package com.louis.mycomposeproject.domain.usecase

import com.louis.mycomposeproject.domain.repository.AuthRepository

class LogoutUseCase(private val repository: AuthRepository) {
    operator fun invoke() {
        repository.logout()
    }
}
