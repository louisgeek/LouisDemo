package com.louis.mycomposeproject.data.local

import android.content.Context
import android.content.SharedPreferences
import com.louis.mycomposeproject.util.Constants

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences = 
        context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE)
    
    fun saveToken(token: String) {
        prefs.edit().putString(Constants.KEY_TOKEN, token).apply()
    }
    
    fun getToken(): String? {
        return prefs.getString(Constants.KEY_TOKEN, null)
    }
    
    fun saveUserId(userId: String) {
        prefs.edit().putString(Constants.KEY_USER_ID, userId).apply()
    }
    
    fun getUserId(): String? {
        return prefs.getString(Constants.KEY_USER_ID, null)
    }
    
    fun saveEmail(email: String) {
        prefs.edit().putString(Constants.KEY_EMAIL, email).apply()
    }
    
    fun getEmail(): String? {
        return prefs.getString(Constants.KEY_EMAIL, null)
    }
    
    fun isLoggedIn(): Boolean {
        return getToken() != null
    }
    
    fun clearAll() {
        prefs.edit().clear().apply()
    }
}
