package com.v20charactermanager.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.v20charactermanager.data.repository.HouseRuleRepositoryImpl
import com.v20charactermanager.domain.model.HouseRules
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HouseRulesUiState(
    val rules: HouseRules = HouseRules(""),
    val isLoaded: Boolean = false,
    val message: String? = null
)

class HouseRulesViewModel(
    private val repository: HouseRuleRepositoryImpl
) : ViewModel() {

    private val _uiState = MutableStateFlow(HouseRulesUiState())
    val uiState: StateFlow<HouseRulesUiState> = _uiState.asStateFlow()

    fun loadRules(chronicleId: String) {
        viewModelScope.launch {
            val rules = repository.getHouseRules(chronicleId)
            _uiState.update { it.copy(rules = rules, isLoaded = true) }
        }
    }

    fun updateRules(rules: HouseRules) {
        _uiState.update { it.copy(rules = rules) }
    }

    fun save() {
        viewModelScope.launch {
            repository.saveHouseRules(_uiState.value.rules)
            _uiState.update { it.copy(message = "House Rules saved") }
        }
    }

    fun resetToDefaults() {
        val chronicleId = _uiState.value.rules.chronicleId
        _uiState.update { it.copy(rules = HouseRules.defaults(chronicleId)) }
    }

    fun clearMessage() {
        _uiState.update { it.copy(message = null) }
    }
}

class HouseRulesViewModelFactory(
    private val repository: HouseRuleRepositoryImpl
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HouseRulesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HouseRulesViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
