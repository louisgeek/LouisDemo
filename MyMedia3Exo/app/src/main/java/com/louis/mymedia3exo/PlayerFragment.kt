package com.louis.mymedia3exo

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.OptIn
import androidx.fragment.app.Fragment
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.analytics.AnalyticsListener
import androidx.media3.ui.PlayerView


class PlayerFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_player, container, false)
    }

    companion object {
        private const val TAG = "PlayerFragment"
        private const val SP_NAME = "PlaybackSP"

        // TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PlayerFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PlayerFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private lateinit var playerView: PlayerView

    @OptIn(UnstableApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playerView = view.findViewById(R.id.playerView)
    }

    override fun onStart() {
        super.onStart()
        initializePlayer() //API > 23 在 onStart
    }

    override fun onStop() {
        super.onStop()
        //
        releasePlayer() //API > 23 在 onStop
    }

    override fun onPause() {
        super.onPause()
//        pausePlayer()
    }

    override fun onResume() {
        super.onResume()
//        resumePlayer()
    }

    private var exoPlayer: ExoPlayer? = null

    @OptIn(UnstableApi::class)
    private fun initializePlayer() {
        val context = requireContext()

        exoPlayer = ExoPlayer.Builder(context).build()
        // 与 PlayerView 关联
        playerView.player = exoPlayer

        exoPlayer?.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_READY) {
                    val sharedPreferences =
                        requireContext().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
                    val savedCurrentPosition = sharedPreferences.getLong("currentPosition", 0)
                    val savedPlayWhenReady = sharedPreferences.getBoolean("playWhenReady", true)
                    //恢复进度
                    if (savedCurrentPosition > 0) {
                        exoPlayer?.seekTo(savedCurrentPosition)
                    }
                    exoPlayer?.playWhenReady = savedPlayWhenReady
                    //恢复后移除
                    exoPlayer?.removeListener(this)
                    Log.e(TAG, "恢复进度: $savedCurrentPosition $savedPlayWhenReady")
                }
            }
        })
        val uri =
            "http://devimages.apple.com.edgekey.net/streaming/examples/bipbop_4x3/bipbop_4x3_variant.m3u8"
        val mediaItem = MediaItem.fromUri(uri)

//        val dataSourceFactory = DefaultHttpDataSource.Factory()
//        val hlsMediaSource = HlsMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem)
//        exoPlayer?.setMediaSource(hlsMediaSource)

        exoPlayer?.setMediaItem(mediaItem)

        exoPlayer?.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_IDLE -> Log.d(TAG, "onPlaybackStateChanged 无资源或播放结束")
                    Player.STATE_BUFFERING -> Log.d(TAG, "onPlaybackStateChanged 加载中")
                    Player.STATE_READY -> Log.d(TAG, "onPlaybackStateChanged 准备就绪")
                    Player.STATE_ENDED -> Log.d(TAG, "onPlaybackStateChanged 播放完成")
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                Log.e(TAG, "onPlayerError 播放错误=$error")
            }

        })
        exoPlayer?.addAnalyticsListener(object :AnalyticsListener{

            override fun onPlayWhenReadyChanged(
                eventTime: AnalyticsListener.EventTime,
                playWhenReady: Boolean,
                reason: Int
            ) {
                Log.e(TAG, "Analytics onPlayWhenReadyChanged playWhenReady=$playWhenReady reason=$reason")
            }

            override fun onIsPlayingChanged(
                eventTime: AnalyticsListener.EventTime,
                isPlaying: Boolean
            ) {
                Log.e(TAG, "Analytics onIsPlayingChanged isPlaying=$isPlaying")
            }
            override fun onRenderedFirstFrame(
                eventTime: AnalyticsListener.EventTime,
                output: Any,
                renderTimeMs: Long
            ) {
                Log.e(TAG, "Analytics onRenderedFirstFrame 获取到第一帧 renderTimeMs=$renderTimeMs")
            }
        })

        exoPlayer?.prepare() //准备（会进行加载）
    }

    private fun releasePlayer() {
        exoPlayer?.let { player ->
            val currentPosition = player.currentPosition //获取当前播放位置（毫秒）
            val playWhenReady = player.playWhenReady //是否正在播放

            val sharedPreferences =
                requireContext().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
            sharedPreferences.edit()
                .putLong("currentPosition", currentPosition)
                .putBoolean("playWhenReady", playWhenReady)
                .apply()
        }
        //
        exoPlayer?.release()
        exoPlayer = null
    }

    private fun pausePlayer() {
        exoPlayer?.pause() //就是 setPlayWhenReady(false)
    }

    private fun resumePlayer() {
        exoPlayer?.play() //就是 setPlayWhenReady(true)
    }

}