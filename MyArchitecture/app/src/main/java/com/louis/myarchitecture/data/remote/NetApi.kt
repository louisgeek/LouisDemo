package com.louis.myarchitecture.data.remote

import com.louis.myarchitecture.data.remote.model.NewsModel
import retrofit2.http.GET

interface NetApi {
    @GET("https://www.wanandroid.com/article/list/0/json")
    suspend fun getHomeList(): BaseResult<NewsModel>
}