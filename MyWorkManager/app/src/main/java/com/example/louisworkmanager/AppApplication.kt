package com.example.louisworkmanager

import android.app.Application
import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit


/**
 * Created by louisgeek on 2024/10/10.
 */
class AppApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        //
        initWorkManager(this)
    }


    private fun initWorkManager(appContext: Context) {

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val newsWorkRequest = PeriodicWorkRequest.Builder(
            NewsWorker::class.java,
            15,
            TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(appContext).enqueueUniquePeriodicWork(
            "newsWork",
            ExistingPeriodicWorkPolicy.KEEP,//KEEP 策略，如果已有相同任务，保留新任务并取消旧任务
            newsWorkRequest
        )
    }
}