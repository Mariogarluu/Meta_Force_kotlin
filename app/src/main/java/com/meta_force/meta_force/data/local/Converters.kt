package com.meta_force.meta_force.data.local

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.meta_force.meta_force.data.model.DietMeal
import com.meta_force.meta_force.data.model.WorkoutExercise

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromDietMealList(meals: List<DietMeal>?): String {
        return gson.toJson(meals)
    }

    @TypeConverter
    fun toDietMealList(mealsString: String?): List<DietMeal> {
        if (mealsString == null) return emptyList()
        val type = object : TypeToken<List<DietMeal>>() {}.type
        return gson.fromJson(mealsString, type)
    }

    @TypeConverter
    fun fromWorkoutExerciseList(exercises: List<WorkoutExercise>?): String {
        return gson.toJson(exercises)
    }

    @TypeConverter
    fun toWorkoutExerciseList(exercisesString: String?): List<WorkoutExercise> {
        if (exercisesString == null) return emptyList()
        val type = object : TypeToken<List<WorkoutExercise>>() {}.type
        return gson.fromJson(exercisesString, type)
    }
}
