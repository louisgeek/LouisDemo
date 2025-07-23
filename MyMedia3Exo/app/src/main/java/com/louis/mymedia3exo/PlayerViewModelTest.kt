package com.louis.mymedia3exo

import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.annotation.OptIn
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.util.Util
import androidx.media3.datasource.DataSpec
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.RawResourceDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.analytics.AnalyticsListener
import androidx.media3.exoplayer.hls.DefaultHlsExtractorFactory
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.exoplayer.hls.playlist.DefaultHlsPlaylistParserFactory
import androidx.media3.exoplayer.source.LoadEventInfo
import androidx.media3.exoplayer.source.MediaLoadData
import androidx.media3.exoplayer.upstream.LoadErrorHandlingPolicy
import com.louis.mymedia3exo.util.HMACSHA256Util
import com.louis.mymedia3exo.util.HeaderTool
import com.louis.mymedia3exo.util.UrlUtil
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.util.Date

class PlayerViewModelTest(application: Application) : AndroidViewModel(application) {
    companion object {
        private const val TAG = "PlayerViewModel"
    }

    private var exoPlayer: ExoPlayer? = null

    private val currentMediaItem: MutableLiveData<MediaItem> = MutableLiveData<MediaItem>()
    private val isPlaying = MutableLiveData(false)

    init {
        Log.e(TAG, "zzz initPlayer: ")
        initPlayer(getApplication())
    }

    @OptIn(UnstableApi::class)
    private fun initPlayer(context: Context) {
        exoPlayer = ExoPlayer.Builder(context).build()
        androidx.media3.common.util.Log.setLogLevel(androidx.media3.common.util.Log.LOG_LEVEL_ALL)
        val eventId = "1945662551216070656" //60
//        val eventId = "1945688997212954630" //50
        val force = true

        val params: MutableMap<String, Any> = HashMap()
        params["eventId"] = eventId
        params["force"] = force
        val baseUrl = "http://39.170.88.130:8888"
        val timestamp = Date().time
        val paramsStr: String = UrlUtil.toSortedQueryString(params)
        val signature: String = HMACSHA256Util.generateHMACSHA256Hex(timestamp, paramsStr)
        var urlString =
            baseUrl + String.format("/event/play.m3u8?eventId=%s&force=%b", eventId, force)

//        var uri = Uri.parse("android.resource://" + context.getPackageName() + "/raw/" + R.raw.vid_1945688997212954630)

        // 创建 HLS MediaSource
        val dataSourceFactory = DefaultHttpDataSource.Factory()
            .setConnectTimeoutMs(80 * 1000)
            .setReadTimeoutMs(80 * 1000)
            .setAllowCrossProtocolRedirects(true)
//            .setKeepPostFor302Redirects(true)
//            .setUserAgent("Mozilla/5.0 (Linux; Android 8.1.0; NX606J Build/OPM1.171019.026; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/61.0.3163.98 Mobile Safari/537.36")
            .setDefaultRequestProperties(HeaderTool.getHeaderMap(timestamp, signature))

//         urlString =
//            "http://44.222.168.138:3000/hls/hls-noaudio/WorkingWithPhotos.m3u8"
//        val dataSourceFactory =  LoggingHttpDataSourceFactory()
//        dataSourceFactory.setDefaultRequestProperties(HeaderTool.getHeaderMap(timestamp, signature))

//        val rawResourceDataSource = RawResourceDataSource(context)
//
//        val uri = RawResourceDataSource.buildRawResourceUri(R.raw.vid_1945688997212954630)
//        val dataSpec = DataSpec(uri)
//        try {
//            rawResourceDataSource.open(dataSpec);
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
        val mediaItem = MediaItem.fromUri(urlString)
//        val mediaItem = MediaItem.fromUri(rawResourceDataSource.uri!!)

        val hlsMediaSource = HlsMediaSource.Factory(dataSourceFactory)
            .setUseSessionKeys(true)
            .setAllowChunklessPreparation(true)  // 允许无分片准备（适用于低延迟播放）
            .setExtractorFactory(DefaultHlsExtractorFactory())  // 自定义提取器工厂
            .setPlaylistParserFactory(DefaultHlsPlaylistParserFactory())
            .createMediaSource(mediaItem)

//        val dataSourceFactory = RawResourceDataSource.F(this, R.raw.stream)
//        val mediaSource = HlsMediaSource.Factory(dataSourceFactory)
//            .createMediaSource(mediaItem)

//        fetchAndPrintM3U8Content(urlString,hlsMediaSource)
        exoPlayer?.setMediaSource(hlsMediaSource)

        exoPlayer?.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)
                when (playbackState) {
                    Player.STATE_IDLE -> Log.d(TAG, "onPlaybackStateChanged 无资源或播放结束")
                    Player.STATE_BUFFERING -> Log.d(TAG, "onPlaybackStateChanged 加载中")
                    Player.STATE_READY -> Log.d(TAG, "onPlaybackStateChanged 准备就绪")
                    Player.STATE_ENDED -> Log.d(TAG, "onPlaybackStateChanged 播放完成")
                }
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                this@PlayerViewModelTest.isPlaying.value = isPlaying
            }

            override fun onPlayerError(error: PlaybackException) {
                super.onPlayerError(error)
                Log.e(TAG, "onPlayerError: ", error)
            }
        })
        exoPlayer?.addAnalyticsListener(object : AnalyticsListener {
            override fun onLoadError(
                eventTime: AnalyticsListener.EventTime,
                loadEventInfo: LoadEventInfo,
                mediaLoadData: MediaLoadData,
                error: IOException,
                wasCanceled: Boolean
            ) {
                Log.e(TAG, "onPlayerError: loadEventInfo=$loadEventInfo")
                Log.e(TAG, "onPlayerError: mediaLoadData=$mediaLoadData")
                Log.e(TAG, "onPlayerError: error=$error")
                Log.e(TAG, "onPlayerError: wasCanceled=$wasCanceled")
            }
        })

//        exoPlayer?.setMediaItem(mediaItem)
        exoPlayer?.prepare() //准备（会进行加载）
    }

    fun getPlayer(): ExoPlayer? {
        return exoPlayer
    }

    fun getCurrentMediaItem(): LiveData<MediaItem> {
        return currentMediaItem
    }

    fun getIsPlaying(): LiveData<Boolean> {
        return isPlaying
    }

    fun setMediaItemAndPrepare(mediaItem: MediaItem) {
        currentMediaItem.postValue(mediaItem)
        exoPlayer?.setMediaItem(mediaItem)
        exoPlayer?.prepare() //准备（会进行加载）
    }

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
        Log.e(TAG, "zzz onCleared: ")
        exoPlayer?.release()
        exoPlayer = null
    }

//    fun fetchAndPrintM3U8Content(url: String,dataSource: DefaultHttpDataSource) {
//
//        try {
//            val dataSpec = DataSpec(Uri.parse(url))
//
//            dataSource.open(dataSpec)
//            val inputStream = dataSource.inputStream
//            val reader = BufferedReader(InputStreamReader(inputStream))
//            var line: String?
//
//            while (reader.readLine().also { line = it } != null) {
//                println(line)
//            }
//
//            reader.close()
//            dataSource.close()
//        } catch (e: Exception) {
//            e.printStackTrace()
//        } finally {
//            dataSource.close()
//        }
//    }
}