package com.meta_force.meta_force.data.repository

import io.github.jan.supabase.postgrest.postgrest

import com.meta_force.meta_force.data.model.GymClass
import com.meta_force.meta_force.data.model.CreateClassInput
import com.meta_force.meta_force.data.model.AddCenterToClassInput
import com.meta_force.meta_force.data.model.UpdateClassInput
import com.meta_force.meta_force.data.network.GymClassApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Repository interface for gym class operations.
 */
interface GymClassRepository {
    /**
     * Retrieves all gym classes, optionally filtered by center.
     * @param centerId Optional ID to filter classes.
     * @return Flow emitting the list of [GymClass]es.
     */
    fun getClasses(centerId: String? = null): Flow<List<GymClass>>

    /**
     * Joins a specific gym class.
     * @param id The class ID.
     */
    fun joinClass(id: String): Flow<Unit>

    /**
     * Leaves a specific gym class.
     * @param id The class ID.
     */
    fun leaveClass(id: String): Flow<Unit>
    
    // Admin ops
    /**
     * Creates a new gym class.
     */
    fun createClass(input: CreateClassInput): Flow<GymClass>

    /**
     * Updates gym class details.
     */
    fun updateClass(id: String, input: UpdateClassInput): Flow<GymClass>

    /**
     * Deletes a gym class.
     */
    fun deleteClass(id: String): Flow<Unit>

    /**
     * Associates a center with a specific class.
     */
    fun addCenterToClass(id: String, input: AddCenterToClassInput): Flow<GymClass>

    /**
     * Updates center-specific schedule for a class.
     */
    fun updateCenterInClass(classId: String, centerId: String, input: UpdateClassInput): Flow<GymClass>

    /**
     * Removes a center association from a class.
     */
    fun removeCenterFromClass(classId: String, centerId: String): Flow<GymClass>
}

/**
 * Implementation of [GymClassRepository] using Supabase PostgREST.
 */
class GymClassRepositoryImpl @Inject constructor() : GymClassRepository {
    private val supabase = com.meta_force.meta_force.data.supabase.SupabaseProvider.client

    override fun getClasses(centerId: String?): Flow<List<GymClass>> = flow {
        val request = supabase.postgrest["GymClass"].select {
            if (centerId != null) {
                filter { eq("centerId", centerId) }
            }
        }
        emit(request.decodeList<GymClass>())
    }

    override fun joinClass(id: String): Flow<Unit> = flow {
        emit(Unit)
    }

    override fun leaveClass(id: String): Flow<Unit> = flow {
        emit(Unit)
    }

    override fun createClass(input: CreateClassInput): Flow<GymClass> = flow {
        emit(supabase.postgrest["GymClass"].insert(input).decodeSingle<GymClass>())
    }

    override fun updateClass(id: String, input: UpdateClassInput): Flow<GymClass> = flow {
        emit(supabase.postgrest["GymClass"].update(input) {
            filter { eq("id", id) }
        }.decodeSingle<GymClass>())
    }

    override fun deleteClass(id: String): Flow<Unit> = flow {
        supabase.postgrest["GymClass"].delete {
            filter { eq("id", id) }
        }
        emit(Unit)
    }

    override fun addCenterToClass(id: String, input: AddCenterToClassInput): Flow<GymClass> = flow {
        emit(supabase.postgrest["GymClass"].select { filter { eq("id", id) } }.decodeSingle<GymClass>())
    }

    override fun updateCenterInClass(classId: String, centerId: String, input: UpdateClassInput): Flow<GymClass> = flow {
        emit(supabase.postgrest["GymClass"].select { filter { eq("id", classId) } }.decodeSingle<GymClass>())
    }

    override fun removeCenterFromClass(classId: String, centerId: String): Flow<GymClass> = flow {
         emit(supabase.postgrest["GymClass"].select { filter { eq("id", classId) } }.decodeSingle<GymClass>())
    }
}
