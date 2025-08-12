package com.louis.myarchitecture.data.repository

import com.louis.myarchitecture.data.local.UserLocalDataSource
import com.louis.myarchitecture.data.local.entity.User
import com.louis.myarchitecture.data.remote.UserRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class UserRepository(
    private val localDataSource: UserLocalDataSource,
    private val remoteDataSource: UserRemoteDataSource
) {
    // 共享用户数据（例如：从本地或网络加载）
    fun getUser(): Flow<User> = flow {
        val cachedUser = localDataSource.getCachedUser()
        if (cachedUser != null) {
            emit(cachedUser)
        } else {
            val fetchedUser = remoteDataSource.fetchUser()
            localDataSource.saveUser(fetchedUser)
            emit(fetchedUser)
        }
    }
}