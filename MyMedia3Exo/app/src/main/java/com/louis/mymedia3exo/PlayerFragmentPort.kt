package com.louis.mymedia3exo

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.OptIn
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.media3.common.Player.Listener
import androidx.media3.common.VideoSize
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView


class PlayerFragmentPort : Fragment() {

    companion object {
        private const val TAG = "PlayerFragmentPort"

        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PlayerFragmentPort().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private lateinit var viewModel: PlayerViewModel

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

        return inflater.inflate(R.layout.fragment_player_port, container, false)
    }


    private lateinit var playerView: PlayerView
    private lateinit var fullscreen: View

    @OptIn(UnstableApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //activity
        viewModel = ViewModelProvider(requireActivity())[PlayerViewModel::class.java]

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
//        this.initializePlayer()

        // 将播放器与视图绑定
        playerView.setPlayer(viewModel.getPlayer())

//        if (savedInstanceState == null) {
//            val uri =
//                "http://devimages.apple.com.edgekey.net/streaming/examples/bipbop_4x3/bipbop_4x3_variant.m3u8"
//            val mediaItem = MediaItem.fromUri(uri)
////            viewModel.setMediaItemAndPrepare(mediaItem)
//        }

        val videoZoomHelper = VideoZoomHelper(playerView)
        val exp = viewModel.getPlayer() as ExoPlayer
        exp.addListener(object : Listener {
            override fun onVideoSizeChanged(videoSize: VideoSize) {
                super.onVideoSizeChanged(videoSize)
//                videoZoomHelper.setVideoSize(Size(videoSize.width, videoSize.height))
            }
        })

    }

    override fun onStart() {
        super.onStart()
//        this.startPlayer()
//        this.resumePlayer()
        viewModel.play()
    }

    override fun onStop() {
        super.onStop()
//        this.stopPlayer()
//        this.pausePlayer()
        viewModel.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
//        this.releasePlayer()
    }

//    private var exoPlayer: ExoPlayer? = null
//    private var vidSize = Size(0, 0)


    private fun setFullScreen(enableFullScreen: Boolean) {
        if (enableFullScreen) {
            requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
        } else {
            requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        }
    }


    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

    }
}