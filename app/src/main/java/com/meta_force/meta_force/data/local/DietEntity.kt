package com.meta_force.meta_force.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.meta_force.meta_force.data.model.Diet
import com.meta_force.meta_force.data.model.DietMeal

@Entity(tableName = "diets")
data class DietEntity(
    @PrimaryKey val id: String,
    val name: String?,
    val description: String?,
    val userId: String?,
    val caloriesTarget: Int?,
    val active: Boolean?,
    val meals: List<DietMeal>?, 
    val createdAt: String?,
    val updatedAt: String?
)

fun Diet.toEntity(): DietEntity = DietEntity(
    id = this.id ?: "",
    name = this.name,
    description = this.description,
    userId = this.userId,
    caloriesTarget = this.caloriesTarget,
    active = this.active,
    meals = this.meals,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt
)

fun DietEntity.toModel(): Diet = Diet(
    id = this.id,
    name = this.name,
    description = this.description,
    userId = this.userId,
    caloriesTarget = this.caloriesTarget,
    active = this.active,
    meals = this.meals,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt
)

fun List<Diet>.toEntityList(): List<DietEntity> = this.map { it.toEntity() }
fun List<DietEntity>.toModelList(): List<Diet> = this.map { it.toModel() }
