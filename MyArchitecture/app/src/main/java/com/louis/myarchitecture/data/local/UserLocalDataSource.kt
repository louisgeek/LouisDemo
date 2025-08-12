package com.louis.myarchitecture.data.local

import com.louis.myarchitecture.data.local.entity.User

class UserLocalDataSource {
    fun getCachedUser(): User? {
        return User()
    }

    fun saveUser(fetchedUser: User) {

    }
}