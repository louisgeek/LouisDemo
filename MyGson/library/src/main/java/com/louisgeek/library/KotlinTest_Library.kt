package com.louisgeek.library

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object KotlinTest_Library {
    fun test() {
        val listJson = getListJson()

        val newsList1 = parseJson<List<NewsBean>>(listJson)
        println("newsList1=$newsList1") //

        val newsList2 = parseJsonToList<NewsBean>(listJson)
        println("newsList2=$newsList2") //


    }
    inline fun <reified T> parseJson(json: String): T {
        //仍旧借助 TypeToken 类获取具体的泛型类型
        val type = object : TypeToken<T>() {}.type
        return Gson().fromJson(json, type)
    }

    inline fun <reified T> parseJsonToList(json: String): List<T> {
        //在 app module 写 parseJsonToList 然后在 app module 调用，在 id("org.jetbrains.kotlin.android") version "1.8.10" 及以下版本会丢失泛型，1.8.20 及以上没问题
        //但是如果在 library module 写 parseJsonToList 然后在 app module 调用，不管 kotlin 啥版本都没问题
        //如果在 library module 写 parseJsonToList 然后在 library module 调用，在 id("org.jetbrains.kotlin.android") version "1.8.10" 及以下版本会丢失泛型，1.8.20 及以上没问题
        return parseJson<List<T>>(json)
    }




    private fun getListJson(): String {
        val newsBeans: MutableList<NewsBean> = ArrayList()
        for (i in 0..2) {
            val userBean = NewsBean()
            userBean.newsid = (i + 1)
            userBean.newsname = "news_" + (i + 1)
            newsBeans.add(userBean)
        }
        return Gson().toJson(newsBeans)
    }
}