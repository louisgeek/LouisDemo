package com.louis.myarchitecture.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.louis.myarchitecture.data.local.dao.UserDao
import com.louis.myarchitecture.data.local.entity.User

@Database(entities = [User::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}