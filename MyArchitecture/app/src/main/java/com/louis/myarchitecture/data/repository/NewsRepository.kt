package com.louis.myarchitecture.data.repository

import com.louis.myarchitecture.data.remote.BaseResult
import com.louis.myarchitecture.data.remote.model.NewsModel
import kotlinx.coroutines.flow.Flow

interface NewsRepository {
    fun getHomeList(): Flow<BaseResult<NewsModel>>
}