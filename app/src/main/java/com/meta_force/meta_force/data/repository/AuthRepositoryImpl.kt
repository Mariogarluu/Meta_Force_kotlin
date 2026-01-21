package com.meta_force.meta_force.data.repository

import com.meta_force.meta_force.data.local.SessionManager
import com.meta_force.meta_force.data.model.LoginRequest
import com.meta_force.meta_force.data.model.LoginResponse
import com.meta_force.meta_force.data.model.RegisterRequest
import com.meta_force.meta_force.data.model.RegisterResponse
import com.meta_force.meta_force.data.network.AuthApi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApi,
    private val sessionManager: SessionManager
) : AuthRepository {

    override suspend fun login(request: LoginRequest): Result<LoginResponse> {
        return try {
            val response = api.login(request)
            sessionManager.saveAuthToken(response.token)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(request: RegisterRequest): Result<RegisterResponse> {
        return try {
            val response = api.register(request)
            sessionManager.saveAuthToken(response.token)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        sessionManager.clearAuthToken()
    }

    override fun getAuthToken(): Flow<String?> {
        return sessionManager.authToken
    }
}
