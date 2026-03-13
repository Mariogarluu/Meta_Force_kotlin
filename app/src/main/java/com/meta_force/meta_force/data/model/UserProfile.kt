package com.meta_force.meta_force.data.model

/**
 * Represents the complete profile of a user, including physical and goal-related data.
 *
 * @property id Unique identifier for the user.
 * @property email User's email address.
 * @property name User's full name.
 * @property role User's role in the system.
 * @property profileImageUrl Optional URL for the user's profile picture.
 * @property height User's height in centimeters.
 * @property weight User's weight in kilograms.
 * @property age User's age in years.
 * @property gender User's gender (e.g., 'MALE', 'FEMALE', 'OTHER').
 * @property activityLevel User's current physical activity level.
 * @property goal User's primary fitness goal.
 */
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

/**
 * Request object for updating user profile information.
 * All fields are optional; only provided fields will be modified.
 *
 * @property name New name for the user.
 * @property height New height.
 * @property weight New weight.
 * @property age New age.
 * @property gender New gender designation.
 * @property activityLevel New activity level.
 * @property goal New primary goal.
 */
data class UpdateProfileRequest(
    val name: String?,
    val height: Double?,
    val weight: Double?,
    val age: Int?,
    val gender: String?,
    val activityLevel: String?,
    val goal: String?
)
