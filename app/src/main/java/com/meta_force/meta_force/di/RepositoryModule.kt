package com.meta_force.meta_force.di

import com.meta_force.meta_force.data.repository.AuthRepository
import com.meta_force.meta_force.data.repository.AuthRepositoryImpl
import com.meta_force.meta_force.data.repository.WorkoutRepository
import com.meta_force.meta_force.data.repository.WorkoutRepositoryImpl
import com.meta_force.meta_force.data.repository.DietRepository
import com.meta_force.meta_force.data.repository.DietRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindClassRepository(
        classRepositoryImpl: com.meta_force.meta_force.data.repository.ClassRepositoryImpl
    ): com.meta_force.meta_force.data.repository.ClassRepository

    @Binds
    @Singleton
    abstract fun bindWorkoutRepository(
        workoutRepositoryImpl: WorkoutRepositoryImpl
    ): WorkoutRepository

    @Binds
    @Singleton
    abstract fun bindDietRepository(
        dietRepositoryImpl: DietRepositoryImpl
    ): DietRepository
}
