package com.louis.mycomposeproject.util

import android.content.Context
import android.os.Build
import android.provider.Settings

object DeviceUtils {
    fun getDeviceId(context: Context): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }
    
    fun getDeviceName(): String = Build.MODEL
    
    fun getDeviceManufacturer(): String = Build.MANUFACTURER
    
    fun getAndroidVersion(): String = Build.VERSION.RELEASE
    
    fun getApiLevel(): Int = Build.VERSION.SDK_INT
    
    fun getDeviceInfo(): String {
        return """
            Device: ${getDeviceName()}
            Manufacturer: ${getDeviceManufacturer()}
            Android: ${getAndroidVersion()}
            API Level: ${getApiLevel()}
        """.trimIndent()
    }
}
