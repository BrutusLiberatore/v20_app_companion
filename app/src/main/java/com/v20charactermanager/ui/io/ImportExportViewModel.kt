package com.v20charactermanager.ui.io

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.v20charactermanager.domain.engine.CharacterExporter
import com.v20charactermanager.domain.engine.CharacterImporter
import com.v20charactermanager.domain.engine.EquipmentLibraryEngine
import com.v20charactermanager.domain.model.Character
import com.v20charactermanager.domain.model.EquipmentItem
import com.v20charactermanager.domain.repository.CharacterRepository
import com.v20charactermanager.ui.components.V20ErrorType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

sealed class IoOperationState {
    data object Idle : IoOperationState()
    data object Loading : IoOperationState()
    data class Success(val message: String) : IoOperationState()
    data class Error(
        val message: String,
        val errorType: V20ErrorType = V20ErrorType.UNKNOWN_ERROR,
        val details: String? = null
    ) : IoOperationState()
    data class DuplicateDetected(
        val existingCharacterId: String,
        val existingCharacterName: String,
        val importedCharacter: Character,
        val pendingUri: Uri? = null
    ) : IoOperationState()
    data class EquipmentLibraryImported(
        val items: List<com.v20charactermanager.domain.model.EquipmentItem>,
        val libraryName: String
    ) : IoOperationState()
}

data class ImportExportUiState(
    val operationState: IoOperationState = IoOperationState.Idle,
    val characters: List<Character> = emptyList()
)

class ImportExportViewModel(
    private val characterRepository: CharacterRepository,
    private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(ImportExportUiState())
    val uiState: StateFlow<ImportExportUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            characterRepository.getAllCharacters().collect { characters ->
                _uiState.value = _uiState.value.copy(characters = characters)
            }
        }
    }

    fun resetState() {
        _uiState.value = _uiState.value.copy(operationState = IoOperationState.Idle)
    }

    fun importFromUri(uri: Uri) {
        _uiState.value = _uiState.value.copy(operationState = IoOperationState.Loading)
        viewModelScope.launch {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                    ?: run {
                        _uiState.value = _uiState.value.copy(
                            operationState = IoOperationState.Error(
                                "Cannot read file",
                                V20ErrorType.DOCUMENT_IMPORT_FAILED,
                                "ContentResolver returned null stream for URI: $uri"
                            )
                        )
                        return@launch
                    }
                val jsonString = inputStream.bufferedReader().use { it.readText() }
                inputStream.close()

                val result = CharacterImporter.import(jsonString)
                if (!result.success) {
                    _uiState.value = _uiState.value.copy(
                        operationState = IoOperationState.Error(
                            result.error ?: "Import failed",
                            V20ErrorType.IMPORT_FORMAT_ERROR,
                            result.error
                        )
                    )
                    return@launch
                }

                val importedCharacter = result.character!!

                val duplicate = _uiState.value.characters.find {
                    it.identity.name == importedCharacter.identity.name &&
                        it.identity.clan == importedCharacter.identity.clan &&
                        it.identity.generation == importedCharacter.identity.generation
                }

                if (duplicate != null) {
                    _uiState.value = _uiState.value.copy(
                        operationState = IoOperationState.DuplicateDetected(
                            existingCharacterId = duplicate.id,
                            existingCharacterName = duplicate.identity.name,
                            importedCharacter = importedCharacter,
                            pendingUri = uri
                        )
                    )
                } else {
                    saveImportedCharacter(importedCharacter)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    operationState = IoOperationState.Error(
                        "Import failed: ${e.message}",
                        V20ErrorType.IMPORT_FORMAT_ERROR,
                        e.message
                    )
                )
            }
        }
    }

    fun saveImportedAsCopy() {
        val state = _uiState.value.operationState
        if (state !is IoOperationState.DuplicateDetected) return

        val newCharacter = state.importedCharacter.copy(
            id = UUID.randomUUID().toString(),
            importMetadata = com.v20charactermanager.domain.model.ImportMetadata(
                importedAt = System.currentTimeMillis(),
                sourceCharacterId = state.importedCharacter.id
            )
        )
        saveImportedCharacter(newCharacter)
    }

    fun replaceExisting() {
        val state = _uiState.value.operationState
        if (state !is IoOperationState.DuplicateDetected) return

        val newCharacter = state.importedCharacter.copy(
            id = state.existingCharacterId,
            importMetadata = com.v20charactermanager.domain.model.ImportMetadata(
                importedAt = System.currentTimeMillis(),
                sourceCharacterId = state.importedCharacter.id
            )
        )
        viewModelScope.launch {
            characterRepository.updateCharacter(newCharacter)
            _uiState.value = _uiState.value.copy(
                operationState = IoOperationState.Success("Character replaced successfully")
            )
        }
    }

    private fun saveImportedCharacter(character: Character) {
        viewModelScope.launch {
            try {
                characterRepository.insertCharacter(character)
                _uiState.value = _uiState.value.copy(
                    operationState = IoOperationState.Success(
                        "Imported: ${character.identity.name.ifEmpty { "Unnamed" }}"
                    )
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    operationState = IoOperationState.Error(
                        "Save failed: ${e.message}",
                        V20ErrorType.DATABASE_ERROR,
                        e.message
                    )
                )
            }
        }
    }

    fun exportCharacter(character: Character, uri: Uri) {
        _uiState.value = _uiState.value.copy(operationState = IoOperationState.Loading)
        viewModelScope.launch {
            try {
                val jsonString = CharacterExporter.export(character)
                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    outputStream.bufferedWriter().use { it.write(jsonString) }
                } ?: run {
                    _uiState.value = _uiState.value.copy(
                        operationState = IoOperationState.Error(
                            "Cannot write to file",
                            V20ErrorType.EXPORT_FAILED,
                            "ContentResolver returned null output stream for URI: $uri"
                        )
                    )
                    return@launch
                }
                _uiState.value = _uiState.value.copy(
                    operationState = IoOperationState.Success(
                        "Exported: ${character.identity.name.ifEmpty { "Unnamed" }}"
                    )
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    operationState = IoOperationState.Error(
                        "Export failed: ${e.message}",
                        V20ErrorType.EXPORT_FAILED,
                        e.message
                    )
                )
            }
        }
    }

    fun createShareIntent(character: Character): Intent {
        val jsonString = CharacterExporter.export(character)
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/json"
            putExtra(Intent.EXTRA_STREAM, jsonString)
            putExtra(Intent.EXTRA_SUBJECT, "V20 Character: ${character.identity.name.ifEmpty { "Unnamed" }}")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        return Intent.createChooser(shareIntent, "Share V20 Character")
    }

    fun importEquipmentLibrary(uri: Uri) {
        _uiState.value = _uiState.value.copy(operationState = IoOperationState.Loading)
        viewModelScope.launch {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                    ?: run {
                        _uiState.value = _uiState.value.copy(
                            operationState = IoOperationState.Error(
                                "Cannot read file",
                                V20ErrorType.DOCUMENT_IMPORT_FAILED,
                                "ContentResolver returned null stream for URI: $uri"
                            )
                        )
                        return@launch
                    }
                val jsonString = inputStream.bufferedReader().use { it.readText() }
                inputStream.close()

                val result = EquipmentLibraryEngine.import(jsonString)
                if (!result.success) {
                    _uiState.value = _uiState.value.copy(
                        operationState = IoOperationState.Error(
                            result.error ?: "Import failed",
                            V20ErrorType.IMPORT_FORMAT_ERROR,
                            result.error
                        )
                    )
                    return@launch
                }

                _pendingEquipmentItems = result.items
                _uiState.value = _uiState.value.copy(
                    operationState = IoOperationState.EquipmentLibraryImported(
                        items = result.items,
                        libraryName = result.name.ifEmpty { "Equipment Library" }
                    )
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    operationState = IoOperationState.Error(
                        "Import failed: ${e.message}",
                        V20ErrorType.IMPORT_FORMAT_ERROR,
                        e.message
                    )
                )
            }
        }
    }

    fun importEquipmentToCharacter(characterId: String) {
        val state = _uiState.value.operationState
        if (state !is IoOperationState.EquipmentLibraryImported) return
        val items = _pendingEquipmentItems ?: return

        viewModelScope.launch {
            try {
                val character = characterRepository.getCharacterByIdOnce(characterId)
                if (character != null) {
                    val updated = character.copy(
                        equipment = character.equipment + items,
                        updatedAt = System.currentTimeMillis()
                    )
                    characterRepository.updateCharacter(updated)
                    _pendingEquipmentItems = null
                    _uiState.value = _uiState.value.copy(
                        operationState = IoOperationState.Success(
                            "Imported ${items.size} equipment items to ${character.identity.name.ifEmpty { "Unnamed" }}"
                        )
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        operationState = IoOperationState.Error(
                            "Character not found",
                            V20ErrorType.CHARACTER_NOT_FOUND,
                            "ID: $characterId"
                        )
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    operationState = IoOperationState.Error(
                        "Import failed: ${e.message}",
                        V20ErrorType.DATABASE_ERROR,
                        e.message
                    )
                )
            }
        }
    }

    fun exportEquipmentLibrary(
        items: List<EquipmentItem>,
        name: String,
        uri: Uri
    ) {
        _uiState.value = _uiState.value.copy(operationState = IoOperationState.Loading)
        viewModelScope.launch {
            try {
                val result = EquipmentLibraryEngine.export(items, name)
                if (!result.success || result.jsonString == null) {
                    _uiState.value = _uiState.value.copy(
                        operationState = IoOperationState.Error(
                            result.error ?: "Export failed",
                            V20ErrorType.EXPORT_FAILED,
                            result.error
                        )
                    )
                    return@launch
                }
                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    outputStream.bufferedWriter().use { it.write(result.jsonString) }
                } ?: run {
                    _uiState.value = _uiState.value.copy(
                        operationState = IoOperationState.Error(
                            "Cannot write to file",
                            V20ErrorType.EXPORT_FAILED,
                            "ContentResolver returned null output stream for URI: $uri"
                        )
                    )
                    return@launch
                }
                _uiState.value = _uiState.value.copy(
                    operationState = IoOperationState.Success("Exported ${items.size} equipment items")
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    operationState = IoOperationState.Error(
                        "Export failed: ${e.message}",
                        V20ErrorType.EXPORT_FAILED,
                        e.message
                    )
                )
            }
        }
    }

    private var _pendingEquipmentItems: List<EquipmentItem>? = null
}

class ImportExportViewModelFactory(
    private val characterRepository: CharacterRepository,
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ImportExportViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ImportExportViewModel(characterRepository, context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
