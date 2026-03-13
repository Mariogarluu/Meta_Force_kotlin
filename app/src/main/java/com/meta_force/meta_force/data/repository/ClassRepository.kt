package com.meta_force.meta_force.data.repository

import com.meta_force.meta_force.data.model.GymClass
import com.meta_force.meta_force.data.model.CreateClassInput
import com.meta_force.meta_force.data.model.AddCenterToClassInput
import com.meta_force.meta_force.data.model.UpdateClassInput
import com.meta_force.meta_force.data.network.ClassApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Repository interface for gym class operations.
 */
interface ClassRepository {
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
 * Implementation of [ClassRepository] using [ClassApi].
 */
class ClassRepositoryImpl @Inject constructor(
    private val api: ClassApi
) : ClassRepository {
    override fun getClasses(centerId: String?): Flow<List<GymClass>> = flow {
        emit(api.getClasses(centerId))
    }

    override fun joinClass(id: String): Flow<Unit> = flow {
        emit(api.joinClass(id))
    }

    override fun leaveClass(id: String): Flow<Unit> = flow {
        emit(api.leaveClass(id))
    }

    override fun createClass(input: CreateClassInput): Flow<GymClass> = flow {
        emit(api.createClass(input))
    }

    override fun updateClass(id: String, input: UpdateClassInput): Flow<GymClass> = flow {
        emit(api.updateClass(id, input))
    }

    override fun deleteClass(id: String): Flow<Unit> = flow {
        emit(api.deleteClass(id))
    }

    override fun addCenterToClass(id: String, input: AddCenterToClassInput): Flow<GymClass> = flow {
        emit(api.addCenterToClass(id, input))
    }

    override fun updateCenterInClass(classId: String, centerId: String, input: UpdateClassInput): Flow<GymClass> = flow {
        emit(api.updateCenterInClass(classId, centerId, input))
    }

    override fun removeCenterFromClass(classId: String, centerId: String): Flow<GymClass> = flow {
        emit(api.removeCenterFromClass(classId, centerId))
    }
}
