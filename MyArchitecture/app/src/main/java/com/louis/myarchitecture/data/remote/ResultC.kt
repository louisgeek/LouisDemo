package com.louis.myarchitecture.data.remote

/**
 * 密闭类
 */
@Deprecated("")
sealed class ResultC<out T> {
    data object Loading : ResultC<Nothing>() //data object 优化了 toString
    data class Success<T>(val data: T) : ResultC<T>()
    data class Error(val msg: String) : ResultC<Nothing>()
}