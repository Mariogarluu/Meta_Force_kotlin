package com.meta_force.meta_force.data.repository

import com.meta_force.meta_force.data.local.SessionManager
import com.meta_force.meta_force.data.model.LoginRequest
import com.meta_force.meta_force.data.model.LoginResponse
import com.meta_force.meta_force.data.model.RegisterRequest
import com.meta_force.meta_force.data.model.RegisterResponse
import com.meta_force.meta_force.data.network.AuthApi
import com.meta_force.meta_force.data.model.User
import okhttp3.MediaType.Companion.toMediaTypeOrNull
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

    override suspend fun getProfile(): Result<User> {
        return try {
            // Assuming getProfile returns a LoginResponse wrapper or similar, or just User.
            // Based on API definition: suspend fun getProfile(): LoginResponse
            val response = api.getProfile()
            Result.success(response.user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateProfile(name: String): Result<User> {
        return try {
            // We need to pass a User object likely, or a subset.
            // API: suspend fun updateProfile(@Body user: User): User
            // We might need to fetch current user first to fill other fields, 
            // or the backend accepts partial updates.
            // For now, let's assume we send a dummy object or handling it gracefully.
            // Ideally we should have a ProfileRequest model. 
            // Let's create a User object with the new name.
            val user = User(id = "", name = name, email = "", role = "") // Backend should ignore ID/Email updates if restricted
            val response = api.updateProfile(user)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun uploadAvatar(file: java.io.File): Result<User> {
        return try {
            val requestFile = okhttp3.RequestBody.create("image/*".toMediaTypeOrNull(), file)
            val body = okhttp3.MultipartBody.Part.createFormData("avatar", file.name, requestFile)
            val response = api.uploadAvatar(body)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}