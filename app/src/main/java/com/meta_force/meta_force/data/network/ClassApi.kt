package com.meta_force.meta_force.data.network

import com.meta_force.meta_force.data.model.GymClass
import com.meta_force.meta_force.data.model.CreateClassInput
import com.meta_force.meta_force.data.model.AddCenterToClassInput
import com.meta_force.meta_force.data.model.UpdateClassInput
import retrofit2.http.*

/**
 * Retrofit interface for gym class management and participation API endpoints.
 */
interface ClassApi {
    /**
     * Retrieves a list of available gym classes, optionally filtered by center.
     *
     * @param centerId Optional ID of a center to filter classes by location.
     * @return A list of [GymClass] objects.
     */
    @GET("classes")
    suspend fun getClasses(@Query("centerId") centerId: String? = null): List<GymClass>

    /**
     * Enrolls the current user in a specific gym class.
     *
     * @param id The ID of the class to join.
     */
    @POST("classes/{id}/join")
    suspend fun joinClass(@Path("id") id: String)

    /**
     * Unenrolls the current user from a specific gym class.
     *
     * @param id The ID of the class to leave.
     */
    @DELETE("classes/{id}/join")
    suspend fun leaveClass(@Path("id") id: String)

    /**
     * Creates a new gym class template. (Admin only)
     *
     * @param input The [CreateClassInput] data.
     * @return The newly created [GymClass].
     */
    @POST("classes")
    suspend fun createClass(@Body input: CreateClassInput): GymClass

    /**
     * Updates basic information for a gym class. (Admin only)
     *
     * @param id The ID of the class to update.
     * @param input The [UpdateClassInput] data.
     * @return The updated [GymClass].
     */
    @PATCH("classes/{id}")
    suspend fun updateClass(@Path("id") id: String, @Body input: UpdateClassInput): GymClass

    /**
     * Deletes a gym class and its associations. (Admin only)
     *
     * @param id The ID of the class to delete.
     */
    @DELETE("classes/{id}")
    suspend fun deleteClass(@Path("id") id: String)

    /**
     * Assigns a specific center and trainers to a gym class. (Admin only)
     *
     * @param id The ID of the class.
     * @param input The [AddCenterToClassInput] details.
     * @return The updated [GymClass] with the new association.
     */
    @POST("classes/{id}/centers")
    suspend fun addCenterToClass(@Path("id") id: String, @Body input: AddCenterToClassInput): GymClass

    /**
     * Updates the schedule or trainers for a class at a specific center. (Admin only)
     *
     * @param classId The ID of the gym class.
     * @param centerId The ID of the center association to update.
     * @param input The [UpdateClassInput] data.
     * @return The updated [GymClass].
     */
    @PATCH("classes/{classId}/centers/{centerId}")
    suspend fun updateCenterInClass(
        @Path("classId") classId: String, 
        @Path("centerId") centerId: String, 
        @Body input: UpdateClassInput
    ): GymClass

    /**
     * Removes a class association from a specific center. (Admin only)
     *
     * @param classId The ID of the class.
     * @param centerId The ID of the center to remove.
     * @return The updated [GymClass].
     */
    @DELETE("classes/{classId}/centers/{centerId}")
    suspend fun removeCenterFromClass(
        @Path("classId") classId: String, 
        @Path("centerId") centerId: String
    ): GymClass
}
