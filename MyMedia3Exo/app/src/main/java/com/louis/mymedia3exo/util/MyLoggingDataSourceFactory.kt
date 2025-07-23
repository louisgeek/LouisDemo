//@UnstableApi package com.louis.mymedia3exo.util

import android.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DataSpec
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.HttpDataSource
import java.io.BufferedInputStream
import java.net.HttpURLConnection
import java.net.URI


//class LoggingDataSourceFactory(
//    private val delegate: DataSource.Factory
//) : DataSource.Factory {
//    override fun createDataSource(): DataSource {
//        return LoggingDataSource(delegate.createDataSource())
//    }
//}
//
//class LoggingDataSource(delegate: DataSource) : DefaultHttpDataSource() {
//    override fun open(dataSpec: DataSpec): Long {
//        val response = (delegate as? DefaultHttpDataSource)?.let {
//            // 拦截 HTTP 请求并获取响应内容
//            val connection = it.openConnection(dataSpec.uri) as HttpURLConnection
//            val content = connection.inputStream.bufferedReader().use { it.readText() }
//            println("M3U8 内容：\n$content") // 打印 M3U8 文件内容
//            connection.disconnect()
//        }
//        return super.open(dataSpec)
//    }
////}


//class LoggingHttpDataSourceFactory(
//    private val defaultFactory: DefaultHttpDataSource.Factory
//) : DataSource.Factory {
//    override fun createDataSource(): DataSource {
//        return LoggingHttpDataSource(defaultFactory.createDataSource())
//    }
//}
//
//class LoggingHttpDataSource(
//    private val delegate: HttpDataSource
//) : HttpDataSource by delegate {
//
//    override fun open(dataSpec: DataSpec): Long {
//        val length = delegate.open(dataSpec)
//        val uri = dataSpec.uri.toString()
//        (delegate as? DefaultHttpDataSource)?.let {
//
//            val url = dataSpec.uri.toURL()
//            val connection = it.openConnection(dataSpec.uri) as HttpURLConnection
//            val connection = dataSpec.uri
//            val content = connection.inputStream.bufferedReader().use { it.readText() }
//        }
//
//
//        if (uri.endsWith(".m3u8") || uri.contains(".m3u8")) {
//            val inputStream = BufferedInputStream(delegate.open(dataSpec))
//            val content = inputStream.bufferedReader().use { it.readText() }
//            Log.d("M3U8_CONTENT", "URL: $uri\n$content")
//        }
//        return length
//    }
//}