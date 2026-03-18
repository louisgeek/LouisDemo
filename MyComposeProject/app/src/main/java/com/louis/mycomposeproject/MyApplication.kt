package com.louis.mycomposeproject

import android.app.Application
import com.louis.mycomposeproject.util.Logger

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // 初始化依赖
        Dependencies.init(applicationContext)
        
        // 设置日志模式
        Logger.setDebugMode(BuildConfig.DEBUG)
        
        Logger.i("Application started")
    }
}
