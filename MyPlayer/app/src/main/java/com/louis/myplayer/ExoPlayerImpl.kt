package com.louis.myplayer

import android.content.Context
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.louis.myplayer.palyer.BasePlayer

class ExoPlayerImpl(context: Context) : BasePlayer() {
    private val player = ExoPlayer.Builder(context).build()

    init {
        player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                when (state) {
                    Player.STATE_READY -> {
                        cancelTimeout()
                        notifyPrepared()
                    }
                    Player.STATE_ENDED -> {
                        notifyCompleted()
                    }
                }
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                if (isPlaying) {
                    notifyPlaying()
                    startProgressUpdate()
                } else {
                    notifyPaused()
                    stopProgressUpdate()
                }
            }

            override fun onPlayerError(error: com.google.android.exoplayer2.PlaybackException) {
                cancelTimeout()
                notifyError(error.message ?: "Unknown error")
            }
        })
    }

    override fun prepare(url: String) {
        player.setMediaItem(MediaItem.fromUri(url))
        player.prepare()
        startTimeout()
    }

    override fun play() = player.play()
    override fun pause() = player.pause()
    override fun seekTo(position: Long) = player.seekTo(position)
    override fun getDuration() = if (player.duration > 0) player.duration else 0L
    override fun getCurrentPosition() = player.currentPosition
    override fun isPlaying() = player.isPlaying
    override fun releasePlayer() = player.release()
}
