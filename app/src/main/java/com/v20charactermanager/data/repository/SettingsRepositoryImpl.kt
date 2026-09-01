package com.v20charactermanager.data.repository

import com.v20charactermanager.data.datastore.SettingsDataStore
import com.v20charactermanager.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow

class SettingsRepositoryImpl(
    private val settingsDataStore: SettingsDataStore
) : SettingsRepository {

    override val language: Flow<String>
        get() = settingsDataStore.language

    override val theme: Flow<String>
        get() = settingsDataStore.theme

    override suspend fun setLanguage(language: String) {
        settingsDataStore.setLanguage(language)
    }

    override suspend fun setTheme(theme: String) {
        settingsDataStore.setTheme(theme)
    }
}
