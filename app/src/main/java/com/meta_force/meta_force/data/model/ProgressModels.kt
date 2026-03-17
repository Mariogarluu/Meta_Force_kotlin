package com.meta_force.meta_force.data.model

import java.util.Date

/**
 * Represents a log entry for a physical measurement (e.g., weight, body fat).
 */
data class UserMeasurement(
    val id: String,
    val userId: String,
    val date: String,
    val weight: Double?,
    val bodyFat: Double?,
    val bmi: Double?,
    val createdAt: String
)

/**
 * Represents a log entry for an exercise performance.
 */
data class ExercisePerformanceLog(
    val id: String,
    val userId: String,
    val exerciseId: String,
    val date: String, // ISO string
    val sets: String?,
    val reps: String?,
    val weight: Double?,
    val notes: String?,
    val createdAt: String,
    val exercise: Exercise? = null
)

/**
 * Request object for logging exercise performance.
 */
data class LogPerformanceRequest(
    val exerciseId: String,
    val sets: String?,
    val reps: String?,
    val weight: Double?,
    val notes: String?
)
