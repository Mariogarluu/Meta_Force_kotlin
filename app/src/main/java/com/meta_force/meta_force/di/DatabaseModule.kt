package com.meta_force.meta_force.di

import android.content.Context
import androidx.room.Room
import com.meta_force.meta_force.data.local.DietDao
import com.meta_force.meta_force.data.local.MetaForceDatabase
import com.meta_force.meta_force.data.local.WorkoutDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MetaForceDatabase {
        return Room.databaseBuilder(
            context,
            MetaForceDatabase::class.java,
            "meta_force_db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideDietDao(database: MetaForceDatabase): DietDao {
        return database.dietDao()
    }

    @Provides
    @Singleton
    fun provideWorkoutDao(database: MetaForceDatabase): WorkoutDao {
        return database.workoutDao()
    }
}
