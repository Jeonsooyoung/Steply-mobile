package com.steply.app.data.repository

import com.steply.app.data.local.SettingsDataStore
import kotlinx.coroutines.flow.Flow

class SettingsRepository(
    private val settingsDataStore: SettingsDataStore,
) {
    val onboardingCompleted: Flow<Boolean> = settingsDataStore.onboardingCompleted
    val selectedUserId: Flow<String?> = settingsDataStore.selectedUserId
    val remoteCameraHost: Flow<String?> = settingsDataStore.remoteCameraHost

    suspend fun setSelectedUserId(userId: String?) {
        settingsDataStore.setSelectedUserId(userId)
    }

    suspend fun clearSelectedUserId() {
        settingsDataStore.clearSelectedUserId()
    }

    suspend fun selectUserProfile(profileId: String?) {
        setSelectedUserId(profileId)
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        settingsDataStore.setOnboardingCompleted(completed)
    }

    suspend fun setRemoteCameraHost(host: String?) {
        settingsDataStore.setRemoteCameraHost(host)
    }
}
