package com.steply.app

import android.content.Context
import com.steply.app.data.local.SettingsDataStore
import com.steply.app.data.local.database.SteplyDatabase
import com.steply.app.data.repository.MovementHistoryRepository
import com.steply.app.data.repository.SettingsRepository
import com.steply.app.data.repository.UserProfileRepository

class AppContainer(context: Context) {
    private val database = SteplyDatabase.getInstance(context)
    private val settingsDataStore = SettingsDataStore(context.applicationContext)

    val settingsRepository = SettingsRepository(settingsDataStore)
    val userProfileRepository = UserProfileRepository(database.userProfileDao())
    val movementHistoryRepository = MovementHistoryRepository(database.movementHistoryDao())
}
