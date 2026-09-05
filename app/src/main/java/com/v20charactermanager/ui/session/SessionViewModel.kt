package com.v20charactermanager.ui.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.v20charactermanager.domain.definition.DamageType
import com.v20charactermanager.domain.model.Character
import com.v20charactermanager.domain.repository.CharacterRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SessionUiState(
    val character: Character? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)

class SessionViewModel(
    private val characterRepository: CharacterRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SessionUiState())
    val uiState: StateFlow<SessionUiState> = _uiState.asStateFlow()

    private var characterId: String? = null
    private var collectJob: Job? = null

    fun loadCharacter(id: String) {
        characterId = id
        collectJob?.cancel()
        collectJob = viewModelScope.launch {
            characterRepository.getCharacterById(id).collect { character ->
                _uiState.value = _uiState.value.copy(
                    character = character,
                    isLoading = false
                )
            }
        }
    }

    fun spendBlood(amount: Int = 1) {
        val character = _uiState.value.character ?: return
        val newCurrent = (character.bloodPool.current - amount).coerceAtLeast(0)
        val updated = character.copy(
            bloodPool = character.bloodPool.copy(current = newCurrent)
        )
        updateCharacterInternal(updated)
    }

    fun refillBlood(amount: Int = 1) {
        val character = _uiState.value.character ?: return
        val newCurrent = (character.bloodPool.current + amount).coerceAtMost(character.bloodPool.maximum)
        val updated = character.copy(
            bloodPool = character.bloodPool.copy(current = newCurrent)
        )
        updateCharacterInternal(updated)
    }

    fun spendWillpower(amount: Int = 1) {
        val character = _uiState.value.character ?: return
        val newCurrent = (character.willpower.current - amount).coerceAtLeast(0)
        val updated = character.copy(
            willpower = character.willpower.copy(current = newCurrent)
        )
        updateCharacterInternal(updated)
    }

    fun recoverWillpower(amount: Int = 1) {
        val character = _uiState.value.character ?: return
        val newCurrent = (character.willpower.current + amount).coerceAtMost(character.willpower.permanent)
        val updated = character.copy(
            willpower = character.willpower.copy(current = newCurrent)
        )
        updateCharacterInternal(updated)
    }

    fun applyDamage(index: Int, type: DamageType) {
        val character = _uiState.value.character ?: return
        val updated = character.copy(
            health = character.health.withDamage(index, type)
        )
        updateCharacterInternal(updated)
    }

    fun healDamage(index: Int) {
        val character = _uiState.value.character ?: return
        val updated = character.copy(
            health = character.health.heal(index)
        )
        updateCharacterInternal(updated)
    }

    fun applyHealthDelta(delta: Int) {
        val character = _uiState.value.character ?: return
        if (delta > 0) {
            val damagedIndex = character.health.levels.indexOfFirst { it == DamageType.NONE }
            if (damagedIndex >= 0) {
                val updated = character.copy(
                    health = character.health.withDamage(damagedIndex, DamageType.BASHING)
                )
                updateCharacterInternal(updated)
            }
        } else if (delta < 0) {
            val damagedIndex = character.health.levels.indexOfLast { it != DamageType.NONE }
            if (damagedIndex >= 0) {
                val updated = character.copy(
                    health = character.health.heal(damagedIndex)
                )
                updateCharacterInternal(updated)
            }
        }
    }

    fun earnExperience(amount: Int) {
        val character = _uiState.value.character ?: return
        val updated = character.copy(
            experience = character.experience.earn(amount)
        )
        updateCharacterInternal(updated)
    }

    fun spendExperience(amount: Int) {
        val character = _uiState.value.character ?: return
        val updated = character.copy(
            experience = character.experience.spend(amount)
        )
        updateCharacterInternal(updated)
    }

    fun updateCharacter(character: Character) {
        _uiState.value = _uiState.value.copy(character = character)
        updateCharacterInternal(character)
    }

    private fun updateCharacterInternal(character: Character) {
        viewModelScope.launch {
            characterRepository.updateCharacter(character)
        }
    }
}

class SessionViewModelFactory(
    private val characterRepository: CharacterRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SessionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SessionViewModel(characterRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
