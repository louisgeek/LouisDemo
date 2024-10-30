package com.example.louisworkmanager

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager

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

        //
        //Worker 定义工作单元，WorkRequest（及其子类）则定义工作运行方式和时间。在最简单的情况下，可以使用 OneTimeWorkRequest
        val uploadWorkRequest = OneTimeWorkRequestBuilder<NewsWorker>().build()
//        val uploadWorkRequest = OneTimeWorkRequest.Builder(NewsWorker::class.java).build()
//        val uploadWorkRequest = OneTimeWorkRequest.from(NewsWorker::class.java)
        WorkManager.getInstance(this).enqueue(uploadWorkRequest)

    }
}