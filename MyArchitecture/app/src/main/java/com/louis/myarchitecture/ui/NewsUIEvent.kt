package com.louis.myarchitecture.ui

sealed class NewsUIEvent {
    data class ShowMessage(val message: String) : NewsUIEvent()
}