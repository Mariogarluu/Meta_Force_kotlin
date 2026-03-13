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

    /**
     * Observable flow of the authentication token.
     * Emits null if no token is saved.
     */
    val authToken: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[USER_TOKEN_KEY]
    }

    /**
     * Saves the authentication token to local storage.
     *
     * @param token The JWT token to save.
     */
    suspend fun saveAuthToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_TOKEN_KEY] = token
        }
    }

    /**
     * Clears the authentication token from local storage, effectively logging the user out.
     */
    suspend fun clearAuthToken() {
        context.dataStore.edit { preferences ->
            preferences.remove(USER_TOKEN_KEY)
        }
    }
}
