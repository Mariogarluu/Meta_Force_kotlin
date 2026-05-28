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
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.put
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.contentOrNull
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.SessionStatus
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.rpc
import io.github.jan.supabase.storage.storage
import com.meta_force.meta_force.data.supabase.SupabaseProvider
import java.io.File
import javax.inject.Inject

import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

/**
 * Implementation of [AuthRepository] that interacts with [AuthApi] and [SessionManager].
 *
 * @property sessionManager Instance for local session/token persistence.
 */
class AuthRepositoryImpl @Inject constructor(
    private val sessionManager: SessionManager
) : AuthRepository {
    private val supabase = SupabaseProvider.client

    init {
        CoroutineScope(Dispatchers.IO).launch {
            supabase.auth.sessionStatus.collect { status ->
                if (status is SessionStatus.Authenticated) {
                    val session = status.session
                    sessionManager.saveAuthToken(session.accessToken, session.refreshToken)
                }
            }
        }
    }

    @Serializable
    private data class RoleRow(val role: String)

    private suspend fun getRoleFromRpcOrNull(): String? {
        return runCatching {
            val rows = supabase.postgrest.rpc("get_my_role", buildJsonObject {}).decodeList<RoleRow>()
            rows.firstOrNull()?.role
        }.getOrNull()
    }

    override suspend fun login(request: LoginRequest): NetworkResult<LoginResponse> {
        return safeApiCall {
            supabase.auth.signInWith(Email) {
                email = request.email
                password = request.password
            }
            val token = supabase.auth.currentAccessTokenOrNull()
                ?: throw IllegalStateException("No access token returned by Supabase")
            val refreshToken = supabase.auth.currentSessionOrNull()?.refreshToken ?: ""
            sessionManager.saveAuthToken(token, refreshToken)

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
            val refreshToken = supabase.auth.currentSessionOrNull()?.refreshToken ?: ""
            sessionManager.saveAuthToken(token, refreshToken)

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

    override fun getRefreshToken(): Flow<String?> {
        return sessionManager.refreshToken
    }

    override suspend fun getProfile(): NetworkResult<UserProfile> {
        return safeApiCall {
            val user = supabase.auth.currentUserOrNull()
                ?: throw IllegalStateException("No session")

            // Read from legacy public.User (works with current RLS that checks id == auth uid text)
            val row = supabase.postgrest["User"]
                .select {
                    filter { eq("auth_user_id", user.id) }
                }
                .decodeSingle<JsonObject>()

            val roleFromRpc = getRoleFromRpcOrNull()

            UserProfile(
                id = row["id"]?.jsonPrimitive?.content ?: user.id,
                email = row["email"]?.jsonPrimitive?.content ?: (user.email ?: ""),
                name = row["name"]?.jsonPrimitive?.content ?: (user.userMetadata?.get("name")?.toString() ?: ""),
                role = roleFromRpc ?: row["role"]?.jsonPrimitive?.content ?: "USER",
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

            try {
                val updateData = buildJsonObject {
                    request.name?.let { put("name", it) }
                    request.height?.let { put("height", it) }
                    request.currentWeight?.let { put("currentWeight", it) }
                    request.birthDate?.let { put("birthDate", it) }
                    request.gender?.let { put("gender", it) }
                    request.medicalNotes?.let { put("medicalNotes", it) }
                    request.activityLevel?.let { put("activityLevel", it) }
                    request.goal?.let { put("goal", it) }
                }

                supabase.postgrest["User"]
                    .update(updateData) {
                        filter { eq("auth_user_id", user.id) }
                    }
            } catch (e: Exception) {
                e.printStackTrace()
                throw Exception("Error al actualizar perfil en BD (PostgREST Update): ${e.message ?: e.javaClass.simpleName}", e)
            }

            // Reload and return the freshly updated profile using the robust getProfile query
            when (val profileResult = getProfile()) {
                is NetworkResult.Success -> profileResult.data
                is NetworkResult.Error -> throw Exception(profileResult.message)
                is NetworkResult.Exception -> throw profileResult.e
            }
        }
    }

    override suspend fun uploadAvatar(file: java.io.File): NetworkResult<UserProfile> {
        return safeApiCall {
            val user = supabase.auth.currentUserOrNull()
                ?: throw IllegalStateException("No session")

            val bucket = supabase.storage.from("profiles")
            val ext = file.extension.ifBlank { "jpg" }
            val path = "${user.id}/avatar.$ext"
            
            try {
                bucket.upload(path, file.readBytes(), upsert = true)
            } catch (e: Exception) {
                e.printStackTrace()
                throw Exception("Error en Almacenamiento (Storage Upload): ${e.message ?: e.javaClass.simpleName}", e)
            }
            
            val publicUrl = bucket.publicUrl(path)

            try {
                val updateData = buildJsonObject {
                    put("profileImageUrl", publicUrl)
                }
                supabase.postgrest["User"]
                    .update(updateData) {
                        filter { eq("auth_user_id", user.id) }
                    }
            } catch (e: Exception) {
                e.printStackTrace()
                throw Exception("Error en Base de Datos (PostgREST Update): ${e.message ?: e.javaClass.simpleName}", e)
            }

            // Reload and return the freshly updated profile using the robust getProfile query
            when (val profileResult = getProfile()) {
                is NetworkResult.Success -> {
                    val profile = profileResult.data
                    val updatedAvatarUrl = profile.profileImageUrl?.let { "$it?t=${System.currentTimeMillis()}" }
                    profile.copy(profileImageUrl = updatedAvatarUrl)
                }
                is NetworkResult.Error -> throw Exception(profileResult.message)
                is NetworkResult.Exception -> throw profileResult.e
            }
        }
    }
}