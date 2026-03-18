package com.louis.mycomposeproject.util

object Constants {
    // API
    const val BASE_URL = "https://api.example.com/"
    const val CONNECT_TIMEOUT = 30L
    const val READ_TIMEOUT = 30L
    
    // Validation
    const val MIN_PASSWORD_LENGTH = 6
    const val MIN_NAME_LENGTH = 2
    
    // SharedPreferences Keys
    const val PREF_NAME = "auth_prefs"
    const val KEY_TOKEN = "token"
    const val KEY_USER_ID = "user_id"
    const val KEY_EMAIL = "email"
}
