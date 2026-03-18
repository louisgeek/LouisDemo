package com.louis.mycomposeproject.util

import android.util.Log

object Logger {
    private const val TAG = "MyComposeProject"
    private var isDebug = true
    
    fun d(message: String, tag: String = TAG) {
        if (isDebug) {
            Log.d(tag, message)
        }
    }
    
    fun e(message: String, throwable: Throwable? = null, tag: String = TAG) {
        if (isDebug) {
            Log.e(tag, message, throwable)
        }
    }
    
    fun i(message: String, tag: String = TAG) {
        if (isDebug) {
            Log.i(tag, message)
        }
    }
    
    fun w(message: String, tag: String = TAG) {
        if (isDebug) {
            Log.w(tag, message)
        }
    }
    
    fun setDebugMode(debug: Boolean) {
        isDebug = debug
    }
}
