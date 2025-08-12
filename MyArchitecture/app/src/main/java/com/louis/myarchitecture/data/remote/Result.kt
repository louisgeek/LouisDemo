package com.louis.myarchitecture.data.remote

/**
 * 密闭接口-配合枚举实现可嵌套的枚举
 */
sealed interface Result<out T> {
    data object Loading : Result<Nothing> //data object 优化了 toString
    data class Success<T>(val data: T) : Result<T>
    data class Error(val msg: String) : Result<Nothing>
}