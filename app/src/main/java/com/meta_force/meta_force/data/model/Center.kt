package com.meta_force.meta_force.data.model

/**
 * Represents a fitness center or gym.
 *
 * @property id Unique identifier for the center. Nullable if not yet persisted.
 * @property name The name of the fitness center.
 * @property description A brief description of the center's facilities or services.
 * @property address The physical street address of the center.
 * @property city The city where the center is located.
 * @property country The country where the center is located.
 * @property phone Contact telephone number for the center.
 * @property email Contact email address for the center.
 * @property createdAt Timestamp when the center record was created.
 * @property updatedAt Timestamp when the center record was last modified.
 */
data class Center(
    val id: String? = null,
    val name: String,
    val description: String? = null,
    val address: String? = null,
    val city: String? = null,
    val country: String? = null,
    val phone: String? = null,
    val email: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

/**
 * Input format for creating a new center.
 *
 * @property name The name of the new fitness center.
 * @property description Optional description for the center.
 * @property address Optional physical address.
 * @property city Optional city location.
 * @property country Optional country location.
 * @property phone Optional contact phone.
 * @property email Optional contact email.
 */
data class CreateCenterInput(
    val name: String,
    val description: String? = null,
    val address: String? = null,
    val city: String? = null,
    val country: String? = null,
    val phone: String? = null,
    val email: String? = null
)

/**
 * Input format for updating an existing center's information.
 * All fields are optional; only provided fields will be updated.
 *
 * @property name New name for the center.
 * @property description New description.
 * @property address New address.
 * @property city New city.
 * @property country New country.
 * @property phone New phone number.
 * @property email New email address.
 */
data class UpdateCenterInput(
    val name: String? = null,
    val description: String? = null,
    val address: String? = null,
    val city: String? = null,
    val country: String? = null,
    val phone: String? = null,
    val email: String? = null
)
