package com.louis.mymedia3exo

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.annotation.OptIn
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.Player.PlayWhenReadyChangeReason
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer


class PlayerViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        private const val TAG = "PlayerViewModel"
    }

    private var exoPlayer: ExoPlayer? = null

    init {
        Log.e(TAG, "zzz initPlayer: ")
        initPlayer(getApplication())
    }

    @OptIn(UnstableApi::class)
    private fun initPlayer(context: Context) {
        exoPlayer = ExoPlayer.Builder(context).build()

        val uri =
            "http://devimages.apple.com.edgekey.net/streaming/examples/bipbop_4x3/bipbop_4x3_variant.m3u8"
        val mediaItem = MediaItem.fromUri(uri)

//        val dataSourceFactory = DefaultHttpDataSource.Factory()
//        val hlsMediaSource = HlsMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem)
//        exoPlayer?.setMediaSource(hlsMediaSource)

        exoPlayer?.setMediaItem(mediaItem)

        exoPlayer?.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)
                when (playbackState) {
                    Player.STATE_IDLE -> Log.d(TAG, "zzz onPlaybackStateChanged 无资源或播放结束")
                    Player.STATE_BUFFERING -> Log.d(TAG, "zzz onPlaybackStateChanged 加载中")
                    Player.STATE_READY -> Log.d(TAG, "zzz onPlaybackStateChanged 准备就绪")
                    Player.STATE_ENDED -> Log.d(TAG, "zzz onPlaybackStateChanged 播放完成")
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                super.onPlayerError(error)
                Log.e(TAG, "zzz onPlayerError: ", error)
            }
        })
//        exoPlayer?.setMediaItem(mediaItem)
        exoPlayer?.prepare() //准备（会进行加载）
        //
        exoPlayer?.play()
    }

    fun getPlayer(): ExoPlayer? {
        return exoPlayer
    }

//    fun setMediaItemAndPrepare(mediaItem: MediaItem) {
////        currentMediaItem.postValue(mediaItem)
//        exoPlayer?.setMediaItem(mediaItem)
//        exoPlayer?.prepare() //准备（会进行加载）
//    }

    fun play() {
        exoPlayer?.play()
        Log.e(TAG, "zzz play: ")
    }

    fun pause() {
        exoPlayer?.pause()
        Log.e(TAG, "zzz pause: ")
    }

    override fun onCleared() {
        super.onCleared()
        exoPlayer?.release()
        exoPlayer = null
        Log.e(TAG, "zzz release: ")
    }
}