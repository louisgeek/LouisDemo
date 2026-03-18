package com.louis.mycomposeproject.util

object Validator {
    fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
    
    fun isValidPassword(password: String): Boolean {
        return password.length >= Constants.MIN_PASSWORD_LENGTH
    }
    
    fun isValidName(name: String): Boolean {
        return name.isNotBlank() && name.length >= Constants.MIN_NAME_LENGTH
    }
}
