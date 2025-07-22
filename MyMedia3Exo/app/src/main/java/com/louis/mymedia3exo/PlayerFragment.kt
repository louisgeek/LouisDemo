package com.louis.mymedia3exo

import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Matrix
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.OptIn
import androidx.fragment.app.Fragment
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.VideoSize
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.analytics.AnalyticsListener
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView


class PlayerFragment : Fragment() {

    companion object {
        private const val TAG = "PlayerFragment"
        private const val SP_NAME = "PlaybackSP"


        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PlayerFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

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

        return inflater.inflate(R.layout.fragment_player, container, false)
    }


    private lateinit var playerView: PlayerView
    private lateinit var fullscreen: View

    @OptIn(UnstableApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playerView = view.findViewById(R.id.playerView)
        fullscreen = view.findViewById(R.id.fullscreen)

        fullscreen.setOnClickListener {
            //自定义全屏按钮
            if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                //ORIENTATION_PORTRAIT 纵向 -> 横向
                setFullScreen(true)
            } else if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                //ORIENTATION_LANDSCAPE 横向 -> 纵向
                setFullScreen(false)
            }
        }
        //初始化
        this.initializePlayer()
    }

    override fun onStart() {
        super.onStart()
//        this.startPlayer()
        this.resumePlayer()
    }

    override fun onStop() {
        super.onStop()
//        this.stopPlayer()
        this.pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        this.releasePlayer()
    }

    private var exoPlayer: ExoPlayer? = null
    private var vidSize = Size(0, 0)

    @OptIn(UnstableApi::class)
    private fun initializePlayer() {
        //API > 23 可以放在 onStart
        val context = requireContext()

        exoPlayer = ExoPlayer.Builder(context).build()
        // 与 PlayerView 关联
        playerView.player = exoPlayer
        playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT //保持比例，可能两侧留黑边
        playerView.setAspectRatioListener { targetAspectRatio, naturalAspectRatio, aspectRatioMismatch ->
//            val params = playerView.layoutParams as FrameLayout.LayoutParams
//            params.height = (params.width / targetAspectRatio).toInt()
//            playerView.layoutParams = params
        }
        playerView.setFullscreenButtonClickListener { isFullscreen ->
            //播放器自带的全屏按钮
            setFullScreen(isFullscreen)
        }

        exoPlayer?.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_READY) {
                    val sharedPreferences =
                        requireContext().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
                    val savedCurrentPosition = sharedPreferences.getLong("currentPosition", 0)
                    val savedPlayWhenReady = sharedPreferences.getBoolean("playWhenReady", true)
                    //恢复进度
//                    if (savedCurrentPosition > 0) {
//                        exoPlayer?.seekTo(savedCurrentPosition)
//                    }
//                    exoPlayer?.playWhenReady = savedPlayWhenReady
                    //恢复后移除
                    exoPlayer?.removeListener(this)
                    Log.e(TAG, "恢复进度: $savedCurrentPosition $savedPlayWhenReady")
                }
            }

            override fun onVideoSizeChanged(videoSize: VideoSize) {
                //拿到视频的宽高，将播放界面的宽高比例更改为视频自身的宽高比
                vidSize = Size(videoSize.width, videoSize.height)
                val screenWidth = resources.displayMetrics.widthPixels
                //
                val params = playerView.layoutParams as FrameLayout.LayoutParams

                params.width = screenWidth
                params.height = params.width * vidSize.height / vidSize.width
                playerView.layoutParams = params

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

        exoPlayer?.addAnalyticsListener(object : AnalyticsListener {

            override fun onPlayWhenReadyChanged(
                eventTime: AnalyticsListener.EventTime,
                playWhenReady: Boolean,
                reason: Int
            ) {
                Log.e(
                    TAG,
                    "Analytics onPlayWhenReadyChanged playWhenReady=$playWhenReady reason=$reason"
                )
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

//        exoPlayer?.prepare() //准备（会进行加载）
        this.startPlayer()


//        val values = FloatArray(9)
//        matrix.getValues(values)
//        val scale = values[Matrix.MSCALE_X]
//        val curTransX = values[Matrix.MTRANS_X]
//        val curTransY = values[Matrix.MTRANS_Y]
//
//        val scaledWidth = textureView.width * scale
//        val scaledHeight = textureView.height * scale
//
//        val fixX = when {
//            scaledWidth < textureView.width ->
//                (textureView.width - scaledWidth) / 2 - curTransX
//            curTransX > 0f -> -curTransX
//            scaledWidth + curTransX < textureView.width -> textureView.width - scaledWidth - curTransX
//            else -> 0f
//        }
//
//        val fixY = when {
//            scaledHeight < textureView.height -> (textureView.height - scaledHeight) / 2 - curTransY
//            curTransY > 0f -> -curTransY
//            scaledHeight + curTransY < textureView.height -> textureView.height - scaledHeight - curTransY
//            else -> 0f
//        }
    }

    private fun setFullScreen(enableFullScreen: Boolean) {
        if (enableFullScreen) {
            requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
        } else {
            requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        }
    }

    private fun releasePlayer() {
        //API > 23 可以放在 onStop
//        exoPlayer?.let { player ->
//            val currentPosition = player.currentPosition //获取当前播放位置（毫秒）
//            val playWhenReady = player.playWhenReady //是否正在播放
//
//            val sharedPreferences =
//                requireContext().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
//            sharedPreferences.edit()
//                .putLong("currentPosition", currentPosition)
//                .putBoolean("playWhenReady", playWhenReady)
//                .apply()
//        }
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

    private fun stopPlayer() {
        exoPlayer?.stop()
    }

    private fun startPlayer() {
        exoPlayer?.prepare() //准备（会进行加载）
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        val isLandscape = newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE
        resizePlayerView(isLandscape)

        if (isLandscape) {
//            hideSystemUI()

        } else {

//            showSystemUI()
        }
    }

    private fun resizePlayerView(isLandscape: Boolean) {
        //横竖屏切换-处理播放器布局
        val screenWidth = resources.displayMetrics.widthPixels

        val layoutParams = playerView.layoutParams

        if (isLandscape) {
            //横屏
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        } else {
            //竖屏
            layoutParams.width = screenWidth
            layoutParams.height = layoutParams.width * vidSize.height / vidSize.width
        }
        playerView.layoutParams = layoutParams
    }
}