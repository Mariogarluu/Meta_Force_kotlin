package com.meta_force.meta_force.data.repository

import com.meta_force.meta_force.data.local.SessionManager
import com.meta_force.meta_force.data.model.LoginRequest
import com.meta_force.meta_force.data.model.LoginResponse
import com.meta_force.meta_force.data.model.RegisterRequest
import com.meta_force.meta_force.data.model.RegisterResponse
import com.meta_force.meta_force.data.network.AuthApi
import com.meta_force.meta_force.data.model.User
import com.meta_force.meta_force.data.network.NetworkResult
import com.meta_force.meta_force.data.network.safeApiCall
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApi,
    private val sessionManager: SessionManager
) : AuthRepository {

    override suspend fun login(request: LoginRequest): NetworkResult<LoginResponse> {
        return safeApiCall {
            val response = api.login(request)
            sessionManager.saveAuthToken(response.token)
            response
        }
    }

    override suspend fun register(request: RegisterRequest): NetworkResult<RegisterResponse> {
        return safeApiCall {
            val response = api.register(request)
            sessionManager.saveAuthToken(response.token)
            response
        }
    }

    override suspend fun logout() {
        sessionManager.clearAuthToken()
    }

    override fun getAuthToken(): Flow<String?> {
        return sessionManager.authToken
    }

    override suspend fun getProfile(): NetworkResult<User> {
        return safeApiCall {
            val response = api.getProfile()
            response.user
        }
    }

    override suspend fun updateProfile(name: String): NetworkResult<User> {
        return safeApiCall {
            val user = User(id = "", name = name, email = "", role = "")
            api.updateProfile(user)
        }
    }

    override suspend fun uploadAvatar(file: java.io.File): NetworkResult<User> {
        return safeApiCall {
            val requestFile = okhttp3.RequestBody.create("image/*".toMediaTypeOrNull(), file)
            val body = okhttp3.MultipartBody.Part.createFormData("avatar", file.name, requestFile)
            api.uploadAvatar(body)
        }
    }
}