package com.meta_force.meta_force.data.model

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

data class CreateCenterInput(
    val name: String,
    val description: String? = null,
    val address: String? = null,
    val city: String? = null,
    val country: String? = null,
    val phone: String? = null,
    val email: String? = null
)

data class UpdateCenterInput(
    val name: String? = null,
    val description: String? = null,
    val address: String? = null,
    val city: String? = null,
    val country: String? = null,
    val phone: String? = null,
    val email: String? = null
)
