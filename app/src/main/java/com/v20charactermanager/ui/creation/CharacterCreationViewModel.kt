package com.v20charactermanager.ui.creation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.v20charactermanager.domain.definition.*
import com.v20charactermanager.domain.engine.CharacterCreationValidator
import com.v20charactermanager.domain.engine.FreebiePointCalculator
import com.v20charactermanager.domain.engine.GenerationRules
import com.v20charactermanager.domain.model.BloodPoolState
import com.v20charactermanager.domain.model.Character
import com.v20charactermanager.domain.model.FlawValue
import com.v20charactermanager.domain.model.MeritValue
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
    val pendingWarnings: List<String>? = null,
    val pendingSave: Boolean = false,
    val freebieReport: FreebiePointCalculator.FreebieReport? = null,
    val isSaving: Boolean = false,
    val saved: Boolean = false,
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

    fun addMerit(merit: MeritValue) {
        val current = _uiState.value.character
        _uiState.value = _uiState.value.copy(character = current.addMerit(merit))
        updateFreebieReport()
        saveDraft()
    }

    fun removeMerit(meritId: String) {
        val current = _uiState.value.character
        _uiState.value = _uiState.value.copy(character = current.removeMerit(meritId))
        updateFreebieReport()
        saveDraft()
    }

    fun addFlaw(flaw: FlawValue) {
        val current = _uiState.value.character
        _uiState.value = _uiState.value.copy(character = current.addFlaw(flaw))
        updateFreebieReport()
        saveDraft()
    }

    fun removeFlaw(flawId: String) {
        val current = _uiState.value.character
        _uiState.value = _uiState.value.copy(character = current.removeFlaw(flawId))
        updateFreebieReport()
        saveDraft()
    }

    private fun updateFreebieReport() {
        val report = freebieCalculator.calculate(_uiState.value.character)
        _uiState.value = _uiState.value.copy(freebieReport = report)
    }

    fun nextStep() {
        val state = _uiState.value
        try {
            val result = validator.validateStep(state.character, state.currentStep)
            when {
                result.errors.isNotEmpty() -> {
                    _uiState.value = state.copy(validationResult = result, pendingWarnings = null, pendingSave = false)
                }
                result.warnings.isNotEmpty() -> {
                    _uiState.value = state.copy(validationResult = null, pendingWarnings = result.warnings)
                }
                else -> advanceStep(state)
            }
        } catch (e: Exception) {
            // On any validation crash, advance anyway (non-blocking)
            advanceStep(state)
        }
    }

    private fun advanceStep(state: CreationUiState) {
        _uiState.value = state.copy(
            currentStep = state.currentStep + 1,
            validationResult = null,
            pendingWarnings = null,
            pendingSave = false
        )
        saveDraft()
    }

    fun confirmWarnings() {
        val state = _uiState.value
        if (state.pendingSave) {
            performSave(state.character)
        } else {
            advanceStep(state)
        }
    }

    fun dismissWarnings() {
        _uiState.value = _uiState.value.copy(pendingWarnings = null, pendingSave = false)
    }

    fun previousStep() {
        val state = _uiState.value
        if (state.currentStep > 1) {
            _uiState.value = state.copy(
                currentStep = state.currentStep - 1,
                validationResult = null,
                pendingWarnings = null,
                pendingSave = false
            )
            saveDraft()
        }
    }

    fun goToStep(step: Int) {
        if (step in 1..5) {
            _uiState.value = _uiState.value.copy(
                currentStep = step,
                validationResult = null,
                pendingWarnings = null,
                pendingSave = false,
                saved = false
            )
            saveDraft()
        }
    }

    /**
     * Derived values the manual computes automatically: Willpower permanent = Courage,
     * Humanity = Conscience + Self-Control, blood pool max = generation table.
     */
    private fun withDerivedValues(character: Character): Character {
        val courage = character.getVirtueValue(VirtueId.COURAGE)
        val bloodMax = GenerationRules.getBloodPoolMax(character.identity.generation)
        return character.copy(
            willpower = character.willpower.copy(
                permanent = courage,
                current = character.willpower.current.coerceIn(0, courage)
            ),
            moralPath = character.moralPath.copy(
                conscienceValue = character.getVirtueValue(VirtueId.CONSCIENCE),
                selfControlValue = character.getVirtueValue(VirtueId.SELF_CONTROL),
                courageValue = courage
            ),
            bloodPool = BloodPoolState(maximum = bloodMax, current = bloodMax)
        )
    }

    /**
     * Final save: runs full validation over all 5 steps. Hard errors block the save,
     * warnings require player confirmation.
     */
    fun saveCharacter() {
        val state = _uiState.value
        val base = try {
            withDerivedValues(state.character)
        } catch (e: Exception) {
            state.character
        }
        try {
            val results = (1..5).map { validator.validateStep(base, it) }
            val errors = results.flatMap { it.errors }.distinct()
            val warnings = results.flatMap { it.warnings }.distinct()
            when {
                errors.isNotEmpty() -> {
                    _uiState.value = state.copy(
                        character = base,
                        validationResult = CharacterCreationValidator.ValidationResult(errors = errors),
                        pendingWarnings = null,
                        pendingSave = false
                    )
                }
                warnings.isNotEmpty() -> {
                    _uiState.value = state.copy(
                        character = base,
                        validationResult = null,
                        pendingWarnings = warnings,
                        pendingSave = true
                    )
                }
                else -> performSave(base)
            }
        } catch (e: Exception) {
            performSave(base)
        }
    }

    private fun performSave(character: Character) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, pendingWarnings = null, pendingSave = false, validationResult = null)
            try {
                val toSave = character.copy(
                    isComplete = true,
                    creationStep = _uiState.value.currentStep,
                    updatedAt = System.currentTimeMillis()
                )
                characterRepository.insertCharacter(toSave)
                _uiState.value = _uiState.value.copy(
                    character = toSave,
                    isSaving = false,
                    saved = true
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
