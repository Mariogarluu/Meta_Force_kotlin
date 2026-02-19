package com.meta_force.meta_force.data.model

data class UserProfile(
    val id: String,
    val email: String,
    val name: String,
    val role: String,
    val profileImageUrl: String?,
    val height: Double?,
    val weight: Double?,
    val age: Int?,
    val gender: String?, // 'MALE', 'FEMALE', 'OTHER'
    val activityLevel: String?,
    val goal: String?
)

data class UpdateProfileRequest(
    val name: String?,
    val height: Double?,
    val weight: Double?,
    val age: Int?,
    val gender: String?,
    val activityLevel: String?,
    val goal: String?
)
