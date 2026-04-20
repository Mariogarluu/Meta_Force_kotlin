package com.meta_force.meta_force.data.repository

import com.meta_force.meta_force.data.local.SessionManager
import com.meta_force.meta_force.data.model.LoginRequest
import com.meta_force.meta_force.data.model.LoginResponse
import com.meta_force.meta_force.data.model.RegisterRequest
import com.meta_force.meta_force.data.model.RegisterResponse
import com.meta_force.meta_force.data.network.AuthApi
import com.meta_force.meta_force.data.model.UserProfile
import com.meta_force.meta_force.data.model.UpdateProfileRequest
import com.meta_force.meta_force.data.network.NetworkResult
import com.meta_force.meta_force.data.network.safeApiCall
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.contentOrNull
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
import com.meta_force.meta_force.data.supabase.SupabaseProvider
import java.io.File
import javax.inject.Inject

/**
 * Implementation of [AuthRepository] that interacts with [AuthApi] and [SessionManager].
 *
 * @property api The authentication API interface.
 * @property sessionManager Instance for local session/token persistence.
 */
class AuthRepositoryImpl @Inject constructor(
    private val sessionManager: SessionManager
) : AuthRepository {
    private val supabase = SupabaseProvider.client

    override suspend fun login(request: LoginRequest): NetworkResult<LoginResponse> {
        return safeApiCall {
            supabase.auth.signInWith(Email) {
                email = request.email
                password = request.password
            }
            val token = supabase.auth.currentAccessTokenOrNull()
                ?: throw IllegalStateException("No access token returned by Supabase")
            sessionManager.saveAuthToken(token)

            val authUser = supabase.auth.currentUserOrNull()
                ?: throw IllegalStateException("No user returned by Supabase")

            LoginResponse(
                token = token,
                user = com.meta_force.meta_force.data.model.User(
                    id = authUser.id,
                    name = authUser.userMetadata?.get("name")?.toString() ?: (authUser.email ?: ""),
                    email = authUser.email ?: request.email,
                    role = "USER",
                    profileImageUrl = null
                )
            )
        }
    }

    override suspend fun register(request: RegisterRequest): NetworkResult<RegisterResponse> {
        return safeApiCall {
            supabase.auth.signUpWith(Email) {
                email = request.email
                password = request.password
                data = buildJsonObject { put("name", request.name) }
            }
            val token = supabase.auth.currentAccessTokenOrNull()
                ?: throw IllegalStateException("No access token returned by Supabase")
            sessionManager.saveAuthToken(token)

            val authUser = supabase.auth.currentUserOrNull()
                ?: throw IllegalStateException("No user returned by Supabase")

            RegisterResponse(
                token = token,
                user = com.meta_force.meta_force.data.model.User(
                    id = authUser.id,
                    name = request.name,
                    email = authUser.email ?: request.email,
                    role = "USER",
                    profileImageUrl = null
                )
            )
        }
    }

    override suspend fun logout() {
        runCatching { supabase.auth.signOut() }
        sessionManager.clearAuthToken()
    }

    override fun getAuthToken(): Flow<String?> {
        return sessionManager.authToken
    }

    override suspend fun getProfile(): NetworkResult<UserProfile> {
        return safeApiCall {
            val user = supabase.auth.currentUserOrNull()
                ?: throw IllegalStateException("No session")

            // Read from legacy public.User (works with current RLS that checks id == auth uid text)
            val row = supabase.postgrest["User"]
                .select {
                    filter { eq("id", user.id) }
                }
                .decodeSingle<JsonObject>()

            UserProfile(
                id = row["id"]?.jsonPrimitive?.content ?: user.id,
                email = row["email"]?.jsonPrimitive?.content ?: (user.email ?: ""),
                name = row["name"]?.jsonPrimitive?.content ?: (user.userMetadata?.get("name")?.toString() ?: ""),
                role = row["role"]?.jsonPrimitive?.content ?: "USER",
                profileImageUrl = row["profileImageUrl"]?.jsonPrimitive?.contentOrNull,
                height = row["height"]?.jsonPrimitive?.doubleOrNull,
                currentWeight = row["currentWeight"]?.jsonPrimitive?.doubleOrNull,
                birthDate = row["birthDate"]?.jsonPrimitive?.contentOrNull,
                gender = row["gender"]?.jsonPrimitive?.contentOrNull,
                medicalNotes = row["medicalNotes"]?.jsonPrimitive?.contentOrNull,
                activityLevel = row["activityLevel"]?.jsonPrimitive?.contentOrNull,
                goal = row["goal"]?.jsonPrimitive?.contentOrNull
            )
        }
    }

    override suspend fun updateProfile(request: UpdateProfileRequest): NetworkResult<UserProfile> {
        return safeApiCall {
            val user = supabase.auth.currentUserOrNull()
                ?: throw IllegalStateException("No session")

            val updated = supabase.postgrest["User"]
                .update(
                    mapOf(
                        "name" to request.name,
                        "height" to request.height,
                        "currentWeight" to request.currentWeight,
                        "birthDate" to request.birthDate,
                        "gender" to request.gender,
                        "medicalNotes" to request.medicalNotes,
                        "activityLevel" to request.activityLevel,
                        "goal" to request.goal
                    ).filterValues { it != null }
                ) {
                    filter { eq("id", user.id) }
                }
                .decodeSingle<JsonObject>()

            UserProfile(
                id = updated["id"]?.jsonPrimitive?.content ?: user.id,
                email = updated["email"]?.jsonPrimitive?.content ?: (user.email ?: ""),
                name = updated["name"]?.jsonPrimitive?.content ?: "",
                role = updated["role"]?.jsonPrimitive?.content ?: "USER",
                profileImageUrl = updated["profileImageUrl"]?.jsonPrimitive?.contentOrNull,
                height = updated["height"]?.jsonPrimitive?.doubleOrNull,
                currentWeight = updated["currentWeight"]?.jsonPrimitive?.doubleOrNull,
                birthDate = updated["birthDate"]?.jsonPrimitive?.contentOrNull,
                gender = updated["gender"]?.jsonPrimitive?.contentOrNull,
                medicalNotes = updated["medicalNotes"]?.jsonPrimitive?.contentOrNull,
                activityLevel = updated["activityLevel"]?.jsonPrimitive?.contentOrNull,
                goal = updated["goal"]?.jsonPrimitive?.contentOrNull
            )
        }
    }

    override suspend fun uploadAvatar(file: java.io.File): NetworkResult<UserProfile> {
        return safeApiCall {
            val user = supabase.auth.currentUserOrNull()
                ?: throw IllegalStateException("No session")

            val bucket = supabase.storage.from("profiles")
            val ext = file.extension.ifBlank { "jpg" }
            val path = "${user.id}/avatar.$ext"
            bucket.upload(path, file.readBytes(), upsert = true)
            val publicUrl = bucket.publicUrl(path)

            val updated = supabase.postgrest["User"]
                .update(mapOf("profileImageUrl" to publicUrl)) {
                    filter { eq("id", user.id) }
                }
                .decodeSingle<JsonObject>()

            UserProfile(
                id = updated["id"]?.jsonPrimitive?.content ?: user.id,
                email = updated["email"]?.jsonPrimitive?.content ?: (user.email ?: ""),
                name = updated["name"]?.jsonPrimitive?.content ?: "",
                role = updated["role"]?.jsonPrimitive?.content ?: "USER",
                profileImageUrl = updated["profileImageUrl"]?.jsonPrimitive?.contentOrNull,
                height = updated["height"]?.jsonPrimitive?.doubleOrNull,
                currentWeight = updated["currentWeight"]?.jsonPrimitive?.doubleOrNull,
                birthDate = updated["birthDate"]?.jsonPrimitive?.contentOrNull,
                gender = updated["gender"]?.jsonPrimitive?.contentOrNull,
                medicalNotes = updated["medicalNotes"]?.jsonPrimitive?.contentOrNull,
                activityLevel = updated["activityLevel"]?.jsonPrimitive?.contentOrNull,
                goal = updated["goal"]?.jsonPrimitive?.contentOrNull
            )
        }
    }
}