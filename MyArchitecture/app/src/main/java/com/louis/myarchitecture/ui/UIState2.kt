package com.louis.myarchitecture.ui

sealed interface BaseUIState<out T> {
    object Loading : BaseUIState<Nothing>
    object Empty : BaseUIState<Nothing>
    data class Success<T>(val value: T) : BaseUIState<T>
    data class Error(val msg: String) : BaseUIState<Nothing>
}