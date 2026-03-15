package com.meta_force.meta_force.data.network

import com.meta_force.meta_force.data.model.LoginRequest
import com.meta_force.meta_force.data.model.LoginResponse
import com.meta_force.meta_force.data.model.RegisterRequest
import com.meta_force.meta_force.data.model.RegisterResponse
import com.meta_force.meta_force.data.model.UserProfile
import com.meta_force.meta_force.data.model.UpdateProfileRequest
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Multipart
import retrofit2.http.Part
import okhttp3.MultipartBody

/**
 * Retrofit interface for authentication and user profile API endpoints.
 */
interface AuthApi {
    /**
     * Authenticates a user with email and password.
     *
     * @param request The [LoginRequest] credentials.
     * @return A [LoginResponse] containing the auth token and user info.
     */
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    /**
     * Registers a new user in the system.
     *
     * @param request The [RegisterRequest] with registration details.
     * @return A [RegisterResponse] containing the new auth token and user info.
     */
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): RegisterResponse

    /**
     * Fetches the current authenticated user's profile information.
     *
     * @return The [UserProfile] object representing the current user.
     */
    @GET("users/me")
    suspend fun getProfile(): UserProfile

    /**
     * Updates the current user's profile details.
     *
     * @param request The [UpdateProfileRequest] object with updated fields.
     * @return The updated [UserProfile] information.
     */
    @PATCH("users/me")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): UserProfile

    /**
     * Uploads a new profile image for the current user.
     *
     * @param avatar The image file part in a multipart request.
     * @return The updated [UserProfile] information including the new image URL.
     */
    @Multipart
    @POST("users/me/profile-image")
    suspend fun uploadAvatar(@Part avatar: MultipartBody.Part): UserProfile
}
