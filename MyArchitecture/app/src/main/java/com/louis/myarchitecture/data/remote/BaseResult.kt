package com.louis.myarchitecture.data.remote

import androidx.annotation.Keep


@Keep
class BaseResult<T> {
    val errorMsg: String? = null
    val errorCode: Int = 0
    val data: T? = null
}


data class BaseResult2<T>(val data: T, val errorCode: Int, val errorMsg: String)

data class BaseResult3<T>(var code: Int, var msg: String?, var data: T?)




