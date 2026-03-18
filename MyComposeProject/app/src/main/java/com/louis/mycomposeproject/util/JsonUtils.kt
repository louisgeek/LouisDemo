package com.louis.mycomposeproject.util

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object JsonUtils {
    private val gson = Gson()
    
    fun <T> toJson(obj: T): String {
        return gson.toJson(obj)
    }
    
    fun <T> fromJson(json: String, clazz: Class<T>): T? {
        return try {
            gson.fromJson(json, clazz)
        } catch (e: Exception) {
            Logger.e("JSON parse error", e)
            null
        }
    }
    
    inline fun <reified T> fromJson(json: String): T? {
        return try {
            gson.fromJson(json, object : TypeToken<T>() {}.type)
        } catch (e: Exception) {
            Logger.e("JSON parse error", e)
            null
        }
    }
    
    fun isValidJson(json: String): Boolean {
        return try {
            gson.fromJson(json, Any::class.java)
            true
        } catch (e: Exception) {
            false
        }
    }
}
