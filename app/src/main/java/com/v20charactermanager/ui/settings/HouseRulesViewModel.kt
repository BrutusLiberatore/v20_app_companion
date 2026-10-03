package com.v20charactermanager.ui.settings

import com.v20charactermanager.R
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.v20charactermanager.data.repository.HouseRuleRepositoryImpl
import com.v20charactermanager.domain.engine.applyToEngines
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
    private val repository: HouseRuleRepositoryImpl,
    private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(HouseRulesUiState())
    val uiState: StateFlow<HouseRulesUiState> = _uiState.asStateFlow()

    fun loadRules(chronicleId: String) {
        viewModelScope.launch {
            val rules = repository.getHouseRules(chronicleId)
            _uiState.update { it.copy(rules = rules, isLoaded = true) }
            persistLastChronicle(chronicleId)
            rules.applyToEngines()
        }
    }

    fun updateRules(rules: HouseRules) {
        _uiState.update { it.copy(rules = rules) }
    }

    fun save() {
        viewModelScope.launch {
            val rules = _uiState.value.rules
            repository.saveHouseRules(rules)
            persistLastChronicle(rules.chronicleId)
            rules.applyToEngines()
            _uiState.update { it.copy(message = context.getString(R.string.msg_house_rules_saved)) }
        }
    }

    fun resetToDefaults() {
        val chronicleId = _uiState.value.rules.chronicleId
        val defaults = HouseRules.defaults(chronicleId)
        _uiState.update { it.copy(rules = defaults) }
        defaults.applyToEngines()
    }

    private fun persistLastChronicle(chronicleId: String) {
        if (chronicleId.isBlank()) return
        context.getSharedPreferences(PREFS_NAME, android.content.Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_LAST_CHRONICLE, chronicleId)
            .apply()
    }

    fun clearMessage() {
        _uiState.update { it.copy(message = null) }
    }

    companion object {
        const val PREFS_NAME = "house_rules"
        const val KEY_LAST_CHRONICLE = "last_chronicle"
    }
}

class HouseRulesViewModelFactory(
    private val repository: HouseRuleRepositoryImpl,
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HouseRulesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HouseRulesViewModel(repository, context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
