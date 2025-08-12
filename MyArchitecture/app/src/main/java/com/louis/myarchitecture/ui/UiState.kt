package com.louis.myarchitecture.ui


sealed class UiState<out T> { //上界为 T，生产者
    object Loading : UiState<Nothing>()
    data class Success<out T>(val data: T) : UiState<T>()
    data class Error(val msg: String) : UiState<Nothing>()
}