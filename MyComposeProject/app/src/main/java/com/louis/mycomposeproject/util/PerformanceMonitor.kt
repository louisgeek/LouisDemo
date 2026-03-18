package com.louis.mycomposeproject.util

import android.os.SystemClock

object PerformanceMonitor {
    private val timers = mutableMapOf<String, Long>()
    
    fun startTimer(tag: String) {
        timers[tag] = SystemClock.elapsedRealtime()
    }
    
    fun endTimer(tag: String): Long {
        val startTime = timers[tag] ?: return 0
        val elapsed = SystemClock.elapsedRealtime() - startTime
        Logger.d("$tag took ${elapsed}ms")
        timers.remove(tag)
        return elapsed
    }
    
    fun measureBlock(tag: String, block: () -> Unit) {
        startTimer(tag)
        block()
        endTimer(tag)
    }
}
