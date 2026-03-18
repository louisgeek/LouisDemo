package com.louis.mycomposeproject.data.remote

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val email: String,
    val password: String,
    val name: String
)

data class AuthResponse(
    val id: String,
    val email: String,
    val token: String
)
