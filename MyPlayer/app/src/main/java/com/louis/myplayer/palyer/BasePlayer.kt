package com.louis.myplayer.palyer

import android.os.Handler
import android.os.Looper
import android.util.Log
import java.util.concurrent.CopyOnWriteArrayList

abstract class BasePlayer : IPlayer {
    companion object {
        private const val TAG = "BasePlayer"
    }
//    private val listeners = CopyOnWriteArrayList<IPlayerEventListener>()
    protected val listeners: CopyOnWriteArrayList<IPlayerEventListener> = CopyOnWriteArrayList()
    private val mainHandler = Handler(Looper.getMainLooper())
    private var timeoutRunnable: Runnable? = null
    private val progressRunnable = object : Runnable {
        override fun run() {
            if (isPlaying()) {
                val position = getCurrentPosition()
                val duration = getDuration()
                listeners.forEach { it.onProgress(position, duration) }
                mainHandler.postDelayed(this, 500)
            }
        }
    }

    override fun addPlayerListener(listener: IPlayerEventListener) {
//        if (!listeners.contains(listener)) {
//            listeners.add(listener)
//        }
        val success = listeners.addIfAbsent(listener)
        Log.e(TAG, "addPlayerListener:$success " + listeners.size)
    }

    override fun removePlayerListener(listener: IPlayerEventListener) {
        listeners.remove(listener)
    }

    override fun removeAllListeners() {
        listeners.clear()
    }

    protected fun notifyPrepared() = listeners.forEach { it.onPrepared() }
    protected fun notifyPlaying() = listeners.forEach { it.onPlaying() }
    protected fun notifyPaused() = listeners.forEach { it.onPaused() }
    protected fun notifyCompleted() = listeners.forEach { it.onCompleted() }
    protected fun notifyError(error: String) = listeners.forEach { it.onError(error) }
    protected fun notifyTimeout() = listeners.forEach { it.onTimeout() }

    protected fun startProgressUpdate() {
        mainHandler.post(progressRunnable)
    }

    protected fun stopProgressUpdate() {
        mainHandler.removeCallbacks(progressRunnable)
    }

    protected fun startTimeout(timeout: Long = 10000) {
        cancelTimeout()
        timeoutRunnable = Runnable {
            notifyTimeout()
        }.also { mainHandler.postDelayed(it, timeout) }

    }

    protected fun cancelTimeout() {
        timeoutRunnable?.let { mainHandler.removeCallbacks(it) }
        timeoutRunnable = null
    }

    override fun release() {
        stopProgressUpdate()
        cancelTimeout()
        releasePlayer()
    }

    protected abstract fun releasePlayer()
}