package com.louisgeek.louisaidlclient

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.louisgeek.louisaidlserver.IMyAidlInterface


class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initView()
    }

    private var iMyAidlInterface: IMyAidlInterface? = null
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            iMyAidlInterface = IMyAidlInterface.Stub.asInterface(service)
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            iMyAidlInterface = null
        }
    }

    private fun initView() {
        val tv: TextView = findViewById(R.id.tv)
        tv.setOnClickListener {
            Log.e(TAG, "initView: iMyAidlInterface=$iMyAidlInterface")
            val result = iMyAidlInterface?.basicTypes(1, 2, true, 3.0F, 4.0, "5.0")
            Toast.makeText(this, "客户端点击收到：$result", Toast.LENGTH_LONG).show()
        }
    }

    override fun onStart() {
        super.onStart()
        val intent = Intent("actionMyAidlService")//服务端action
        intent.setPackage("com.louisgeek.louisaidlserver")//服务端包名
        val result = this.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        Log.e("TAG", "onStart: louis==result=$result")
    }

    override fun onStop() {
        super.onStop()
        this.unbindService(serviceConnection)
    }
}