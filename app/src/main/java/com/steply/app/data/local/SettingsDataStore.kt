package com.steply.app.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.steplySettingsDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "steply_settings",
)

class SettingsDataStore(context: Context) {
    private val dataStore = context.steplySettingsDataStore

    val selectedUserId: Flow<String?> = dataStore.data.map { preferences ->
        preferences[SELECTED_USER_ID]
    }

    val onboardingCompleted: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[ONBOARDING_COMPLETED] ?: false
    }

    val remoteCameraHost: Flow<String?> = dataStore.data.map { preferences ->
        preferences[REMOTE_CAMERA_HOST]
    }

    suspend fun setSelectedUserId(userId: String?) {
        dataStore.edit { preferences ->
            if (userId == null) {
                preferences.remove(SELECTED_USER_ID)
            } else {
                preferences[SELECTED_USER_ID] = userId
            }
        }
    }

    suspend fun clearSelectedUserId() {
        setSelectedUserId(null)
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        dataStore.edit { preferences ->
            preferences[ONBOARDING_COMPLETED] = completed
        }
    }

    suspend fun setRemoteCameraHost(host: String?) {
        dataStore.edit { preferences ->
            if (host == null) {
                preferences.remove(REMOTE_CAMERA_HOST)
            } else {
                preferences[REMOTE_CAMERA_HOST] = host
            }
        }
    }

    private companion object {
        val SELECTED_USER_ID = stringPreferencesKey("selected_user_id")
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val REMOTE_CAMERA_HOST = stringPreferencesKey("remote_camera_host")
    }
}
