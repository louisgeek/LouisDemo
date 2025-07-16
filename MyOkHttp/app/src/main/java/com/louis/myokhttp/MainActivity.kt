package com.louis.myokhttp

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.internal.sse.RealEventSource
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //SSE 是HTML5规范的一部分
        val okHttpClient = OkHttpClient.Builder()
            .retryOnConnectionFailure(true) //连接失败自动重试
            .connectTimeout(1, TimeUnit.DAYS)
            .readTimeout(1, TimeUnit.DAYS)
            .writeTimeout(50, TimeUnit.SECONDS)
            .build()

        val request = Request.Builder()
            .url("https://your-server/events")
            .addHeader("Accept", "text/event-stream") //SSE 固定 MIME 类型
            .build()

        val eventSource = RealEventSource(request, object : EventSourceListener() { //监听连接状态和事件
            override fun onOpen(eventSource: EventSource, response: Response) {
                Log.d("SSE", "连接已建立")
            }

            override fun onEvent(
                eventSource: EventSource,
                id: String?,
                type: String?,
                data: String
            ) {
                // 处理接收到的数据
                val message = data ?: return
                Log.d("SSE", "收到消息: $message")
            }

            override fun onClosed(eventSource: EventSource) {
                Log.d("SSE", "连接关闭")
            }

            override fun onFailure(eventSource: EventSource, t: Throwable?, response: Response?) {
                Log.e("SSE", "连接失败: ${t.message}")
            }
        })

        eventSource.connect(okHttpClient)
    }
}