package com.meta_force.meta_force.data.model

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val token: String,
    val user: User
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)

data class RegisterResponse(
    val token: String,
    val user: User
)

data class User(
    val id: String,
    val name: String,
    val email: String,
    val role: String,
    val profileImageUrl: String? = null
)
