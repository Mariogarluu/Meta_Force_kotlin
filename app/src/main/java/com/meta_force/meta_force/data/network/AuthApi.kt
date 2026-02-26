package com.meta_force.meta_force.data.network

import com.meta_force.meta_force.data.model.LoginRequest
import com.meta_force.meta_force.data.model.LoginResponse
import com.meta_force.meta_force.data.model.RegisterRequest
import com.meta_force.meta_force.data.model.RegisterResponse
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Multipart
import retrofit2.http.Part
import okhttp3.MultipartBody
import com.meta_force.meta_force.data.model.User

interface AuthApi {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): RegisterResponse

    @GET("users/me")
    suspend fun getProfile(): User

    @PUT("users/me") // Changed from auth/me which was incorrect
    suspend fun updateProfile(@Body user: User): User

    @Multipart
    @POST("users/me/profile-image") // Also correct avatar upload if needed, checking backend... backend is /api/users/me/profile-image
    suspend fun uploadAvatar(@Part avatar: MultipartBody.Part): User
}
