package com.louis.myarchitecture.domain

import com.louis.myarchitecture.data.repository.UserRepository
import com.louis.myarchitecture.data.local.entity.User
import kotlinx.coroutines.flow.Flow

class GetUserUseCase(private val userRepository: UserRepository) {
    operator fun invoke(): Flow<User> = userRepository.getUser()
}