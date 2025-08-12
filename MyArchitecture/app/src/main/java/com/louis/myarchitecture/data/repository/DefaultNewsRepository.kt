package com.louis.myarchitecture.data.repository

import com.louis.myarchitecture.ModuleP
import com.louis.myarchitecture.data.local.dao.UserDao
import com.louis.myarchitecture.data.local.entity.User
import com.louis.myarchitecture.data.remote.NetApi
import com.louis.myarchitecture.data.remote.RetrofitClient
import kotlinx.coroutines.flow.flow
import kotlin.jvm.java

class DefaultNewsRepository(private val dao: UserDao) : NewsRepository {
    private val netApi by lazy { RetrofitClient.retrofit.create(NetApi::class.java) }

    override fun getHomeList() = flow {

//        val db = ModuleP.provideDataBase()
//        val userDao = db.userDao()
        val users: List<User> = dao.getAll()


        emit(netApi.getHomeList())
    }
}