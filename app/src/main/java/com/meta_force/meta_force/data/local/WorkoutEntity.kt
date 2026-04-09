package com.meta_force.meta_force.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.meta_force.meta_force.data.model.Workout
import com.meta_force.meta_force.data.model.WorkoutExercise

@Entity(tableName = "workouts")
data class WorkoutEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String?,
    val userId: String,
    val exercises: List<WorkoutExercise>,
    val createdAt: String,
    val updatedAt: String
)

fun Workout.toEntity(): WorkoutEntity = WorkoutEntity(
    id = this.id,
    name = this.name,
    description = this.description,
    userId = this.userId,
    exercises = this.exercises,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt
)

fun WorkoutEntity.toModel(): Workout = Workout(
    id = this.id,
    name = this.name,
    description = this.description,
    userId = this.userId,
    exercises = this.exercises,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt
)

fun List<Workout>.toEntityList(): List<WorkoutEntity> = this.map { it.toEntity() }
fun List<WorkoutEntity>.toModelList(): List<Workout> = this.map { it.toModel() }
