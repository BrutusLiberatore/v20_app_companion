package com.v20charactermanager.ui.creation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.v20charactermanager.domain.definition.*
import com.v20charactermanager.domain.engine.CharacterCreationValidator
import com.v20charactermanager.domain.engine.FreebiePointCalculator
import com.v20charactermanager.domain.model.Character
import com.v20charactermanager.domain.repository.CharacterRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

data class CreationUiState(
    val currentStep: Int = 1,
    val character: Character = Character(id = UUID.randomUUID().toString()),
    val validationResult: CharacterCreationValidator.ValidationResult? = null,
    val freebieReport: FreebiePointCalculator.FreebieReport? = null,
    val isSaving: Boolean = false,
    val error: String? = null
)

class CharacterCreationViewModel(
    private val characterRepository: CharacterRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreationUiState())
    val uiState: StateFlow<CreationUiState> = _uiState.asStateFlow()

    private val validator = CharacterCreationValidator()
    private val freebieCalculator = FreebiePointCalculator()

    init {
        saveDraft()
    }

    private fun saveDraft() {
        val state = _uiState.value
        val draft = state.character.copy(
            creationStep = state.currentStep,
            isComplete = false,
            updatedAt = System.currentTimeMillis()
        )
        viewModelScope.launch {
            try {
                characterRepository.insertCharacter(draft)
            } catch (_: Exception) { }
        }
    }

    fun updateIdentity(identity: com.v20charactermanager.domain.model.CharacterIdentity) {
        val current = _uiState.value.character
        _uiState.value = _uiState.value.copy(
            character = current.copy(identity = identity)
        )
        saveDraft()
    }

    fun updateAttribute(attributeId: AttributeId, value: Int) {
        val current = _uiState.value.character
        _uiState.value = _uiState.value.copy(
            character = current.setAttributeValue(attributeId, value)
        )
        updateFreebieReport()
        saveDraft()
    }

    fun updateAbility(abilityId: AbilityId, value: Int) {
        val current = _uiState.value.character
        _uiState.value = _uiState.value.copy(
            character = current.setAbilityValue(abilityId, value)
        )
        updateFreebieReport()
        saveDraft()
    }

    fun addDiscipline(disciplineId: DisciplineId, value: Int = 1) {
        val current = _uiState.value.character
        _uiState.value = _uiState.value.copy(
            character = current.addDiscipline(disciplineId, value)
        )
        updateFreebieReport()
        saveDraft()
    }

    fun updateDiscipline(disciplineId: DisciplineId, value: Int) {
        val current = _uiState.value.character
        val updated = current.disciplines.map {
            if (it.id == disciplineId) it.copy(value = value) else it
        }
        _uiState.value = _uiState.value.copy(
            character = current.copy(disciplines = updated)
        )
        updateFreebieReport()
        saveDraft()
    }

    fun removeDiscipline(disciplineId: DisciplineId) {
        val current = _uiState.value.character
        _uiState.value = _uiState.value.copy(
            character = current.removeDiscipline(disciplineId)
        )
        updateFreebieReport()
        saveDraft()
    }

    fun addBackground(backgroundId: BackgroundId, value: Int = 1) {
        val current = _uiState.value.character
        _uiState.value = _uiState.value.copy(
            character = current.addBackground(backgroundId, value)
        )
        updateFreebieReport()
        saveDraft()
    }

    fun updateBackground(backgroundId: BackgroundId, value: Int) {
        val current = _uiState.value.character
        val updated = current.backgrounds.map {
            if (it.id == backgroundId) it.copy(value = value) else it
        }
        _uiState.value = _uiState.value.copy(
            character = current.copy(backgrounds = updated)
        )
        updateFreebieReport()
        saveDraft()
    }

    fun removeBackground(backgroundId: BackgroundId) {
        val current = _uiState.value.character
        _uiState.value = _uiState.value.copy(
            character = current.removeBackground(backgroundId)
        )
        updateFreebieReport()
        saveDraft()
    }

    fun updateVirtue(virtueId: VirtueId, value: Int) {
        val current = _uiState.value.character
        _uiState.value = _uiState.value.copy(
            character = current.setVirtueValue(virtueId, value)
        )
        updateFreebieReport()
        saveDraft()
    }

    private fun updateFreebieReport() {
        val report = freebieCalculator.calculate(_uiState.value.character)
        _uiState.value = _uiState.value.copy(freebieReport = report)
    }

    fun nextStep() {
        val state = _uiState.value
        val result = validator.validateStep(state.character, state.currentStep)
        if (result.isValid) {
            _uiState.value = state.copy(
                currentStep = state.currentStep + 1,
                validationResult = null
            )
            saveDraft()
        } else {
            _uiState.value = state.copy(validationResult = result)
        }
    }

    fun previousStep() {
        val state = _uiState.value
        if (state.currentStep > 1) {
            _uiState.value = state.copy(
                currentStep = state.currentStep - 1,
                validationResult = null
            )
            saveDraft()
        }
    }

    fun goToStep(step: Int) {
        if (step in 1..5) {
            _uiState.value = _uiState.value.copy(
                currentStep = step,
                validationResult = null
            )
            saveDraft()
        }
    }

    fun saveCharacter() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)
            try {
                val character = _uiState.value.character.copy(
                    isComplete = true,
                    creationStep = _uiState.value.currentStep,
                    updatedAt = System.currentTimeMillis()
                )
                characterRepository.insertCharacter(character)
                _uiState.value = _uiState.value.copy(
                    character = character,
                    isSaving = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    error = e.message
                )
            }
        }
    }

    fun deleteDraft() {
        viewModelScope.launch {
            try {
                characterRepository.deleteCharacter(_uiState.value.character.id)
            } catch (_: Exception) { }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

class CharacterCreationViewModelFactory(
    private val characterRepository: CharacterRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CharacterCreationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CharacterCreationViewModel(characterRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
