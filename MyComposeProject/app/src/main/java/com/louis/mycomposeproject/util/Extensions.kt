package com.louis.mycomposeproject.util

import android.content.Context
import android.widget.Toast

fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

fun String.isValidEmail(): Boolean {
    return Validator.isValidEmail(this)
}

fun String.isValidPassword(): Boolean {
    return Validator.isValidPassword(this)
}
