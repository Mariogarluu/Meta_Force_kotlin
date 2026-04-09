package com.meta_force.meta_force.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [DietEntity::class, WorkoutEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class MetaForceDatabase : RoomDatabase() {
    abstract fun dietDao(): DietDao
    abstract fun workoutDao(): WorkoutDao
}
