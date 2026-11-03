package com.louis.myplayer.palyer

interface IPlayerEventListener {
    fun onPrepared() //播放器就绪
    fun onPlay() //开始/恢复播放
    fun onPause() //暂停播放
    fun onProgress(position: Long, duration: Long) //播放进度回调 (ms)
    fun onCompletion() //播放完成
    fun onError(error: String)
    fun onTimeout()
}