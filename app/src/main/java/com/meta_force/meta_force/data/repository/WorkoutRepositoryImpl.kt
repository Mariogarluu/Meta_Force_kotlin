package com.meta_force.meta_force.data.repository

import com.meta_force.meta_force.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

import kotlinx.coroutines.flow.emitAll
import com.meta_force.meta_force.data.local.WorkoutLocalDataSource
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.gotrue.auth
import com.meta_force.meta_force.data.supabase.SupabaseProvider
import kotlinx.serialization.json.*

/**
 * Implementation of [WorkoutRepository] using Supabase PostgREST and [WorkoutLocalDataSource].
 */
class WorkoutRepositoryImpl @Inject constructor(
    private val localDataSource: WorkoutLocalDataSource
) : WorkoutRepository {
    private val supabase = SupabaseProvider.client

    override fun getWorkouts(): Flow<List<Workout>> = flow {
        try {
            val user = supabase.auth.currentUserOrNull() ?: throw Exception("Usuario no autenticado")
            val remoteJsonList = supabase.postgrest["Workout"]
                .select(columns = "*, exercises:WorkoutExercise(*, exercise:Exercise(*))") {
                    filter { eq("userId", user.id) }
                }
                .decodeList<JsonObject>()
            val remoteWorkouts = remoteJsonList.map { it.toWorkout() }
            localDataSource.saveWorkouts(remoteWorkouts)
        } catch (e: Exception) {
            e.printStackTrace()
            // Ignorar en caso de no tener internet, usará local
        }
        emitAll(localDataSource.getWorkouts())
    }

    override fun getWorkout(id: String): Flow<Workout> = flow {
        try {
            val remoteJson = supabase.postgrest["Workout"]
                .select(columns = "*, exercises:WorkoutExercise(*, exercise:Exercise(*))") {
                    filter { eq("id", id) }
                }
                .decodeSingle<JsonObject>()
            val remoteWorkout = remoteJson.toWorkout()
            localDataSource.saveWorkout(remoteWorkout)
        } catch (e: Exception) {
            e.printStackTrace()
            // Ignorar en caso de no tener internet
        }
        val localWorkout = localDataSource.getWorkoutById(id).getOrNull()
        if (localWorkout != null) {
            emit(localWorkout)
        } else {
            throw Exception("Workout no encontrado")
        }
    }

    override fun createWorkout(
        name: String,
        description: String?,
        goal: String?,
        level: String?,
        daysPerWeek: Int?,
        userId: String
    ): Flow<Workout> = flow {
        val finalUserId = supabase.auth.currentUserOrNull()?.id ?: throw Exception("Usuario no autenticado")
        val requestObject = buildJsonObject {
            put("name", name)
            put("description", description)
            put("userId", finalUserId)
        }
        val insertedJson = supabase.postgrest["Workout"]
            .insert(requestObject)
            .decodeSingle<JsonObject>()
        
        val newWorkout = insertedJson.toWorkout()
        localDataSource.saveWorkout(newWorkout)
        emit(newWorkout)
    }

    override fun deleteWorkout(id: String): Flow<Unit> = flow {
        supabase.postgrest["Workout"].delete {
            filter { eq("id", id) }
        }
        localDataSource.deleteWorkout(id)
        emit(Unit)
    }

    override fun updateWorkout(id: String, request: UpdateWorkoutRequest): Flow<Workout> = flow {
        val updateObject = buildJsonObject {
            request.name?.let { put("name", it) }
            request.description?.let { put("description", it) }
        }
        val updatedJson = supabase.postgrest["Workout"]
            .update(updateObject) {
                filter { eq("id", id) }
            }
            .decodeSingle<JsonObject>()
        val updatedWorkout = updatedJson.toWorkout()
        localDataSource.saveWorkout(updatedWorkout)
        emit(updatedWorkout)
    }

    override fun addExerciseToWorkout(id: String, request: AddExerciseToWorkoutRequest): Flow<WorkoutExercise> = flow {
        val setsInt = request.sets?.toIntOrNull()
        val repsInt = request.reps?.toIntOrNull()
        val requestObject = buildJsonObject {
            put("workoutId", id)
            put("exerciseId", request.exerciseId)
            put("dayOfWeek", request.dayOfWeek)
            request.order?.let { put("order", it) }
            setsInt?.let { put("sets", it) }
            repsInt?.let { put("reps", it) }
            request.weight?.let { put("weight", it) }
            request.restSeconds?.let { put("restSeconds", it) }
            request.notes?.let { put("notes", it) }
        }
        val insertedJson = supabase.postgrest["WorkoutExercise"]
            .insert(requestObject)
            .decodeSingle<JsonObject>()
        
        val workoutExerciseId = insertedJson["id"]?.jsonPrimitive?.contentOrNull ?: ""
        val populatedJson = supabase.postgrest["WorkoutExercise"]
            .select(columns = "*, exercise:Exercise(*)") {
                filter { eq("id", workoutExerciseId) }
            }
            .decodeSingle<JsonObject>()
        val newExercise = populatedJson.toWorkoutExercise()
        
        val workout = localDataSource.getWorkoutById(id).getOrNull()
        if (workout != null) {
            val updatedExercises = (workout.exercises ?: emptyList()).toMutableList().apply { add(newExercise) }
            localDataSource.saveWorkout(workout.copy(exercises = updatedExercises))
        }
        emit(newExercise)
    }

    override fun updateWorkoutExercise(exerciseId: String, request: UpdateWorkoutExerciseRequest): Flow<WorkoutExercise> = flow {
        val setsInt = request.sets?.toIntOrNull()
        val repsInt = request.reps?.toIntOrNull()
        val updateObject = buildJsonObject {
            request.dayOfWeek?.let { put("dayOfWeek", it) }
            request.order?.let { put("order", it) }
            setsInt?.let { put("sets", it) }
            repsInt?.let { put("reps", it) }
            request.weight?.let { put("weight", it) }
            request.restSeconds?.let { put("restSeconds", it) }
            request.notes?.let { put("notes", it) }
        }
        supabase.postgrest["WorkoutExercise"]
            .update(updateObject) {
                filter { eq("id", exerciseId) }
            }
            
        val populatedJson = supabase.postgrest["WorkoutExercise"]
            .select(columns = "*, exercise:Exercise(*)") {
                filter { eq("id", exerciseId) }
            }
            .decodeSingle<JsonObject>()
        val updatedExercise = populatedJson.toWorkoutExercise()
        emit(updatedExercise)
    }

    override fun removeExerciseFromWorkout(exerciseId: String): Flow<Unit> = flow {
        supabase.postgrest["WorkoutExercise"].delete {
            filter { eq("id", exerciseId) }
        }
        emit(Unit)
    }
}

// Extensión mappers para decodificar JSON robustamente evitando errores de coerción
private fun JsonObject.toExercise(): Exercise {
    return Exercise(
        id = this["id"]?.jsonPrimitive?.contentOrNull,
        name = this["name"]?.jsonPrimitive?.contentOrNull,
        description = this["description"]?.jsonPrimitive?.contentOrNull,
        muscleGroup = this["muscleGroup"]?.jsonPrimitive?.contentOrNull,
        videoUrl = this["videoUrl"]?.jsonPrimitive?.contentOrNull,
        imageUrl = this["imageUrl"]?.jsonPrimitive?.contentOrNull
    )
}

private fun JsonObject.toWorkoutExercise(): WorkoutExercise {
    val exerciseObj = this["exercise"]?.jsonObject?.toExercise()
    return WorkoutExercise(
        id = this["id"]?.jsonPrimitive?.contentOrNull,
        workoutId = this["workoutId"]?.jsonPrimitive?.contentOrNull,
        exerciseId = this["exerciseId"]?.jsonPrimitive?.contentOrNull,
        exercise = exerciseObj,
        dayOfWeek = this["dayOfWeek"]?.jsonPrimitive?.intOrNull,
        order = this["order"]?.jsonPrimitive?.intOrNull,
        sets = this["sets"]?.jsonPrimitive?.contentOrNull ?: this["sets"]?.jsonPrimitive?.intOrNull?.toString(),
        reps = this["reps"]?.jsonPrimitive?.contentOrNull ?: this["reps"]?.jsonPrimitive?.intOrNull?.toString(),
        weight = this["weight"]?.jsonPrimitive?.doubleOrNull,
        restSeconds = this["restSeconds"]?.jsonPrimitive?.intOrNull,
        notes = this["notes"]?.jsonPrimitive?.contentOrNull
    )
}

private fun JsonObject.toWorkout(): Workout {
    val exercisesList = this["exercises"]?.jsonArray?.map { it.jsonObject.toWorkoutExercise() } ?: emptyList()
    return Workout(
        id = this["id"]?.jsonPrimitive?.contentOrNull,
        name = this["name"]?.jsonPrimitive?.contentOrNull,
        description = this["description"]?.jsonPrimitive?.contentOrNull,
        userId = this["userId"]?.jsonPrimitive?.contentOrNull,
        exercises = exercisesList,
        createdAt = this["createdAt"]?.jsonPrimitive?.contentOrNull,
        updatedAt = this["updatedAt"]?.jsonPrimitive?.contentOrNull
    )
}