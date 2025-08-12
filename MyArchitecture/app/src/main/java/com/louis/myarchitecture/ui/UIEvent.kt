package com.louis.myarchitecture.ui

sealed class UIEvent {
    object ShowLoading : UIEvent()
    object HideLoading : UIEvent()
    class ShowMsg(val msg: String) : UIEvent()
}
