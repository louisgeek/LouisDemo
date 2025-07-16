package com.louis.myflow

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET
import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.concurrent.thread
import kotlin.random.Random

/**
 * Created by louisgeek on 2024/12/17.
 */
class MainViewModel {

//    fun simple(): Flow<Int> = flow { // 流构建
//        GlobalScope// 器
//
//        for (i in 1..3) {
//            delay(100) // 假装我们在这里做了一些有用的事情
//            emit(i) // 发送下一个值
//            withContext(Dispatchers.IO) {
//                //骚操作
//                emit(i)
//            }
//            supervisorScope { }
//            coroutineScope { }
//        }
//    }.cancellable().flowOn()

    //    fun main() = runBlocking {
//        simple().collect { value -> println(value) }
//    }


    fun TextView.textWatcherFlow(): Flow<String> {
        return callbackFlow {
            val textWatcher = object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    s?.let {
                        trySend(s.toString())
                    }
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                }
            }
            //TextView#addTextChangedListener
            addTextChangedListener(textWatcher)
            //awaitClose 方法是必须的，用于处理资源的关闭
            awaitClose {
                //通常在这里移除取消回调注册
                //TextView#removeTextChangedListener
                removeTextChangedListener(textWatcher)
            }
        }
    }


    @SuppressLint("MissingPermission")
    fun locationFlow(locationManager: LocationManager): Flow<Location> = callbackFlow {
        val locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                trySend(location) // 将位置数据发送到 Flow
            }
        }
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            0L,
            0f,
            locationListener
        )
        awaitClose {
            locationManager.removeUpdates(locationListener) // 协程取消时移除监听器
        }
    }


    @SuppressLint("MissingPermission")
    fun isConnected(context: Context): Flow<Boolean> = callbackFlow {
        val connectivityManager =
            ContextCompat.getSystemService(context, ConnectivityManager::class.java)
        connectivityManager?.let {
            val callback = object : ConnectivityManager.NetworkCallback() {
                override fun onCapabilitiesChanged(
                    network: Network,
                    networkCapabilities: NetworkCapabilities,
                ) {
                    trySendBlocking(networkCapabilities.hasCapability(NET_CAPABILITY_INTERNET))
                }

                override fun onLost(network: Network) {
                    trySendBlocking(false)
                }
            }

            connectivityManager.registerDefaultNetworkCallback(callback)
            awaitClose { connectivityManager.unregisterNetworkCallback(callback) }
        }
    }


//    fun (x: Any): Unit {
//        //使用
//        val callback = object : Callback {
//            override fun onSuccess(data: String) {
//                println("onSuccess: data=$data")
//            }
//            override fun onFailure(e: Exception) {
//                println("onFailure: msg=${e.message}")
//            }
//        }
//        fetchData(callback) //设置回调
//    }

}

