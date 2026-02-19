package com.meta_force.meta_force.data.repository

import com.meta_force.meta_force.data.model.GymClass
import com.meta_force.meta_force.data.network.ClassApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface ClassRepository {
    fun getClasses(): Flow<List<GymClass>>
    fun joinClass(id: String): Flow<Unit>
    fun leaveClass(id: String): Flow<Unit>
}

class ClassRepositoryImpl @Inject constructor(
    private val api: ClassApi
) : ClassRepository {
    override fun getClasses(): Flow<List<GymClass>> = flow {
        emit(api.getClasses())
    }

    override fun joinClass(id: String): Flow<Unit> = flow {
        emit(api.joinClass(id))
    }

    override fun leaveClass(id: String): Flow<Unit> = flow {
        emit(api.leaveClass(id))
    }
}
