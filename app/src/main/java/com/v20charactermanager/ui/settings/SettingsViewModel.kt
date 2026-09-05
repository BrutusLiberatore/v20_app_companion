package com.v20charactermanager.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.v20charactermanager.domain.model.Chronicle
import com.v20charactermanager.domain.repository.ChronicleRepository
import com.v20charactermanager.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SettingsUiState(
    val language: String = "en",
    val chronicles: List<Chronicle> = emptyList(),
    val isLoading: Boolean = true
)

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val chronicleRepository: ChronicleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadLanguage()
        loadChronicles()
    }

    private fun loadLanguage() {
        viewModelScope.launch {
            settingsRepository.language.collect { language ->
                _uiState.value = _uiState.value.copy(
                    language = language,
                    isLoading = false
                )
            }
        }
    }

    private fun loadChronicles() {
        viewModelScope.launch {
            chronicleRepository.getAllChronicles().collect { chronicles ->
                _uiState.value = _uiState.value.copy(chronicles = chronicles)
            }
        }
    }

    fun setLanguage(language: String) {
        viewModelScope.launch {
            settingsRepository.setLanguage(language)
        }
    }
}

class SettingsViewModelFactory(
    private val settingsRepository: SettingsRepository,
    private val chronicleRepository: ChronicleRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(settingsRepository, chronicleRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
