package com.louis.mycomposeproject.data.local

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences = 
        context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    
    fun saveToken(token: String) {
        prefs.edit().putString("token", token).apply()
    }
    
    fun getToken(): String? {
        return prefs.getString("token", null)
    }
    
    fun saveUserId(userId: String) {
        prefs.edit().putString("user_id", userId).apply()
    }
    
    fun getUserId(): String? {
        return prefs.getString("user_id", null)
    }
    
    fun saveEmail(email: String) {
        prefs.edit().putString("email", email).apply()
    }
    
    fun getEmail(): String? {
        return prefs.getString("email", null)
    }
    
    fun isLoggedIn(): Boolean {
        return getToken() != null
    }
    
    fun clearAll() {
        prefs.edit().clear().apply()
    }
}
