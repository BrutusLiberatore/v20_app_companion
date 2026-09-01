package com.v20charactermanager.ui.sheet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.v20charactermanager.domain.definition.*
import com.v20charactermanager.domain.model.*
import com.v20charactermanager.domain.repository.CharacterRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class EditCharacterUiState(
    val character: Character? = null,
    val originalCharacter: Character? = null,
    val isEditing: Boolean = false,
    val isSaving: Boolean = false,
    val hasChanges: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
)

class EditCharacterViewModel(
    private val characterRepository: CharacterRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditCharacterUiState())
    val uiState: StateFlow<EditCharacterUiState> = _uiState.asStateFlow()

    private var characterId: String? = null

    fun loadCharacter(id: String) {
        characterId = id
        viewModelScope.launch {
            characterRepository.getCharacterById(id).collect { character ->
                if (character != null) {
                    val currentState = _uiState.value
                    if (currentState.originalCharacter == null) {
                        _uiState.value = currentState.copy(
                            character = character,
                            originalCharacter = character
                        )
                    } else {
                        _uiState.value = currentState.copy(character = character)
                    }
                }
            }
        }
    }

    fun startEditing() {
        _uiState.value = _uiState.value.copy(isEditing = true)
    }

    fun cancelEditing() {
        val original = _uiState.value.originalCharacter ?: return
        _uiState.value = _uiState.value.copy(
            character = original,
            isEditing = false,
            hasChanges = false,
            error = null
        )
    }

    fun updateAttribute(attributeId: AttributeId, value: Int) {
        val current = _uiState.value.character ?: return
        _uiState.value = _uiState.value.copy(
            character = current.setAttributeValue(attributeId, value),
            hasChanges = true
        )
    }

    fun updateAbility(abilityId: AbilityId, value: Int) {
        val current = _uiState.value.character ?: return
        _uiState.value = _uiState.value.copy(
            character = current.setAbilityValue(abilityId, value),
            hasChanges = true
        )
    }

    fun updateDisciplineValue(disciplineId: DisciplineId, value: Int) {
        val current = _uiState.value.character ?: return
        val updated = current.disciplines.map {
            if (it.id == disciplineId) it.copy(value = value) else it
        }
        _uiState.value = _uiState.value.copy(
            character = current.copy(disciplines = updated),
            hasChanges = true
        )
    }

    fun addDiscipline(disciplineId: DisciplineId, value: Int = 1) {
        val current = _uiState.value.character ?: return
        _uiState.value = _uiState.value.copy(
            character = current.addDiscipline(disciplineId, value),
            hasChanges = true
        )
    }

    fun removeDiscipline(disciplineId: DisciplineId) {
        val current = _uiState.value.character ?: return
        _uiState.value = _uiState.value.copy(
            character = current.removeDiscipline(disciplineId),
            hasChanges = true
        )
    }

    fun updateBackgroundValue(backgroundId: BackgroundId, value: Int) {
        val current = _uiState.value.character ?: return
        val updated = current.backgrounds.map {
            if (it.id == backgroundId) it.copy(value = value) else it
        }
        _uiState.value = _uiState.value.copy(
            character = current.copy(backgrounds = updated),
            hasChanges = true
        )
    }

    fun addBackground(backgroundId: BackgroundId, value: Int = 1) {
        val current = _uiState.value.character ?: return
        _uiState.value = _uiState.value.copy(
            character = current.addBackground(backgroundId, value),
            hasChanges = true
        )
    }

    fun removeBackground(backgroundId: BackgroundId) {
        val current = _uiState.value.character ?: return
        _uiState.value = _uiState.value.copy(
            character = current.removeBackground(backgroundId),
            hasChanges = true
        )
    }

    fun updateVirtue(virtueId: VirtueId, value: Int) {
        val current = _uiState.value.character ?: return
        _uiState.value = _uiState.value.copy(
            character = current.setVirtueValue(virtueId, value),
            hasChanges = true
        )
    }

    fun updateIdentity(identity: CharacterIdentity) {
        val current = _uiState.value.character ?: return
        _uiState.value = _uiState.value.copy(
            character = current.copy(identity = identity),
            hasChanges = true
        )
    }

    fun updateNotes(notes: String) {
        val current = _uiState.value.character ?: return
        val updated = current.copy(notes = notes, updatedAt = System.currentTimeMillis())
        _uiState.value = _uiState.value.copy(
            character = updated,
            hasChanges = true
        )
        viewModelScope.launch {
            characterRepository.updateCharacter(updated)
        }
    }

    fun updatePortrait(portraitUri: String?) {
        val current = _uiState.value.character ?: return
        val updated = current.copy(portraitUri = portraitUri, updatedAt = System.currentTimeMillis())
        _uiState.value = _uiState.value.copy(
            character = updated,
            hasChanges = true
        )
        viewModelScope.launch {
            characterRepository.updateCharacter(updated)
        }
    }

    fun addMerit(merit: MeritValue) {
        val current = _uiState.value.character ?: return
        _uiState.value = _uiState.value.copy(
            character = current.addMerit(merit),
            hasChanges = true
        )
    }

    fun removeMerit(meritId: String) {
        val current = _uiState.value.character ?: return
        _uiState.value = _uiState.value.copy(
            character = current.removeMerit(meritId),
            hasChanges = true
        )
    }

    fun addFlaw(flaw: FlawValue) {
        val current = _uiState.value.character ?: return
        _uiState.value = _uiState.value.copy(
            character = current.addFlaw(flaw),
            hasChanges = true
        )
    }

    fun removeFlaw(flawId: String) {
        val current = _uiState.value.character ?: return
        _uiState.value = _uiState.value.copy(
            character = current.removeFlaw(flawId),
            hasChanges = true
        )
    }

    fun cloneMerit(merit: MeritValue) {
        val current = _uiState.value.character ?: return
        val cloned = merit.copy(
            id = java.util.UUID.randomUUID().toString(),
            name = "${merit.name} (Copy)"
        )
        _uiState.value = _uiState.value.copy(
            character = current.addMerit(cloned),
            hasChanges = true
        )
    }

    fun cloneFlaw(flaw: FlawValue) {
        val current = _uiState.value.character ?: return
        val cloned = flaw.copy(
            id = java.util.UUID.randomUUID().toString(),
            name = "${flaw.name} (Copy)"
        )
        _uiState.value = _uiState.value.copy(
            character = current.addFlaw(cloned),
            hasChanges = true
        )
    }

    fun addEquipment(item: EquipmentItem) {
        val current = _uiState.value.character ?: return
        _uiState.value = _uiState.value.copy(
            character = current.addEquipment(item),
            hasChanges = true
        )
    }

    fun removeEquipment(itemId: String) {
        val current = _uiState.value.character ?: return
        _uiState.value = _uiState.value.copy(
            character = current.removeEquipment(itemId),
            hasChanges = true
        )
    }

    fun updateEquipment(item: EquipmentItem) {
        val current = _uiState.value.character ?: return
        _uiState.value = _uiState.value.copy(
            character = current.updateEquipment(item),
            hasChanges = true
        )
    }

    fun cloneEquipment(item: EquipmentItem) {
        val current = _uiState.value.character ?: return
        val cloned = item.copy(
            id = java.util.UUID.randomUUID().toString(),
            name = "${item.name} (Copy)"
        )
        _uiState.value = _uiState.value.copy(
            character = current.addEquipment(cloned),
            hasChanges = true
        )
    }

    fun save() {
        val current = _uiState.value.character ?: return
        _uiState.value = _uiState.value.copy(isSaving = true, error = null)
        viewModelScope.launch {
            try {
                val updatedCharacter = current.copy(
                    updatedAt = System.currentTimeMillis()
                )
                characterRepository.updateCharacter(updatedCharacter)
                _uiState.value = _uiState.value.copy(
                    character = updatedCharacter,
                    originalCharacter = updatedCharacter,
                    isEditing = false,
                    isSaving = false,
                    hasChanges = false,
                    successMessage = "Character saved successfully"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    error = "Save failed: ${e.message}"
                )
            }
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(error = null, successMessage = null)
    }
}

class EditCharacterViewModelFactory(
    private val characterRepository: CharacterRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditCharacterViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EditCharacterViewModel(characterRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
