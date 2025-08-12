package com.louis.myarchitecture.data.remote

import com.louis.myarchitecture.data.local.entity.User

class UserRemoteDataSource {
    fun fetchUser(): User {
        return User()
    }
}