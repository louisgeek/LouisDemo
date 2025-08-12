package com.louis.myarchitecture

import android.content.Context
import androidx.room.Room
import com.louis.myarchitecture.data.local.AppDatabase
import kotlin.jvm.java

object ModuleP {
    private const val DATABASE_NAME = "app-news-db"

    fun provideDataBase(context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            DATABASE_NAME
        ).build()
    }
}