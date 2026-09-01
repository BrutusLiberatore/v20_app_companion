package com.v20charactermanager.domain.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val language: Flow<String>
    val theme: Flow<String>

    suspend fun setLanguage(language: String)
    suspend fun setTheme(theme: String)
}
