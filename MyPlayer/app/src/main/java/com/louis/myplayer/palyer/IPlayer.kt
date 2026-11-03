package com.louis.myplayer.palyer

interface IPlayer {
    fun prepare(url: String)
    fun play() //开始/恢复播放
    fun pause() //暂停播放
    fun seekTo(position: Long) //跳转到指定位置 (ms)
    fun stop() //停止播放
    fun release()

    fun getDuration(): Long //获取总时长 (ms)
    fun getCurrentPosition(): Long //获取当前播放位置 (ms)
    fun isPlaying(): Boolean //是否正在播放

    fun setVolume(volume: Float) //设置音量 (0.0 - 1.0)
    fun setSpeed(speed: Float) //设置播放速度

    fun addPlayerListener(listener: IPlayerEventListener)
    fun removePlayerListener(listener: IPlayerEventListener)
    fun removeAllListeners()
}