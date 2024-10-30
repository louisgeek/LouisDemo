package com.louisgeek.louisaidlserver

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log


class MyAidlService : Service() {
    companion object {
        private const val TAG = "MyAidlService"
    }
    //private val binder = MyAidlInterfaceBinder()
    //private class MyAidlInterfaceBinder : IMyAidlInterface.Stub() {
    //}
    private val binder: IMyAidlInterface.Stub = object : IMyAidlInterface.Stub() {
        override fun basicTypes(
            anInt: Int,
            aLong: Long,
            aBoolean: Boolean,
            aFloat: Float,
            aDouble: Double,
            aString: String?
        ): String {
            val result = "$anInt $aLong $aBoolean $aFloat $aDouble $aString"
            Log.e(TAG, "basicTypes: louis===")
            return "MyAidlService服务端：result=$result"
        }
    }

    override fun onBind(intent: Intent): IBinder {
        Log.e(TAG, "onBind: louis===")
        return binder
    }
}