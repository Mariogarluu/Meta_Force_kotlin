package com.meta_force.meta_force.data.repository

import com.meta_force.meta_force.data.model.LoginRequest
import com.meta_force.meta_force.data.model.LoginResponse
import com.meta_force.meta_force.data.model.RegisterRequest
import com.meta_force.meta_force.data.model.RegisterResponse
import com.meta_force.meta_force.data.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(request: LoginRequest): Result<LoginResponse>
    suspend fun register(request: RegisterRequest): Result<RegisterResponse>
    suspend fun logout()
    fun getAuthToken(): Flow<String?>
    suspend fun getProfile(): Result<User>
    suspend fun updateProfile(name: String): Result<User>
    suspend fun uploadAvatar(file: java.io.File): Result<User>
}
