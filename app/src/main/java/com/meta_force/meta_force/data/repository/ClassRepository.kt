package com.meta_force.meta_force.data.repository

import com.meta_force.meta_force.data.model.GymClass
import com.meta_force.meta_force.data.model.CreateClassInput
import com.meta_force.meta_force.data.model.AddCenterToClassInput
import com.meta_force.meta_force.data.model.UpdateClassInput
import com.meta_force.meta_force.data.network.ClassApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface ClassRepository {
    fun getClasses(centerId: String? = null): Flow<List<GymClass>>
    fun joinClass(id: String): Flow<Unit>
    fun leaveClass(id: String): Flow<Unit>
    
    // Admin ops
    fun createClass(input: CreateClassInput): Flow<GymClass>
    fun updateClass(id: String, input: UpdateClassInput): Flow<GymClass>
    fun deleteClass(id: String): Flow<Unit>
    fun addCenterToClass(id: String, input: AddCenterToClassInput): Flow<GymClass>
    fun updateCenterInClass(classId: String, centerId: String, input: UpdateClassInput): Flow<GymClass>
    fun removeCenterFromClass(classId: String, centerId: String): Flow<GymClass>
}

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
