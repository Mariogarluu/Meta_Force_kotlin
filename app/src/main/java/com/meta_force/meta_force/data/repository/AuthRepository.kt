package com.meta_force.meta_force.data.repository

import com.meta_force.meta_force.data.model.LoginRequest
import com.meta_force.meta_force.data.model.LoginResponse
import com.meta_force.meta_force.data.model.RegisterRequest
import com.meta_force.meta_force.data.model.RegisterResponse
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(request: LoginRequest): Result<LoginResponse>
    suspend fun register(request: RegisterRequest): Result<RegisterResponse>
    suspend fun logout()
    fun getAuthToken(): Flow<String?>
}
