package com.meta_force.meta_force.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

/**
 * Manages the user's session data using Jetpack DataStore.
 *
 * @property context Application context used to access DataStore.
 */
@Singleton
class SessionManager @Inject constructor(@ApplicationContext private val context: Context) {

    private val USER_TOKEN_KEY = stringPreferencesKey("user_token")
    private val USER_REFRESH_TOKEN_KEY = stringPreferencesKey("user_refresh_token")

    /**
     * Observable flow of the authentication token.
     * Emits null if no token is saved.
     */
    val authToken: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[USER_TOKEN_KEY]
    }

    /**
     * Observable flow of the refresh token.
     * Emits null if no token is saved.
     */
    val refreshToken: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[USER_REFRESH_TOKEN_KEY]
    }

    /**
     * Saves the authentication and refresh tokens to local storage.
     *
     * @param token The JWT access token to save.
     * @param refresh The refresh token to save.
     */
    suspend fun saveAuthToken(token: String, refresh: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_TOKEN_KEY] = token
            preferences[USER_REFRESH_TOKEN_KEY] = refresh
        }
    }

    /**
     * Clears the authentication and refresh tokens from local storage, effectively logging the user out.
     */
    suspend fun clearAuthToken() {
        context.dataStore.edit { preferences ->
            preferences.remove(USER_TOKEN_KEY)
            preferences.remove(USER_REFRESH_TOKEN_KEY)
        }
    }
}
