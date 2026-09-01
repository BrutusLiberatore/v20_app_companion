package com.v20charactermanager.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.v20charactermanager.domain.model.Character
import com.v20charactermanager.domain.repository.CharacterRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val characters: List<Character> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

class HomeViewModel(
    private val characterRepository: CharacterRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadCharacters()
    }

    private fun loadCharacters() {
        viewModelScope.launch {
            characterRepository.getAllCharacters().collect { characters ->
                _uiState.value = _uiState.value.copy(
                    characters = characters.filter { it.isComplete },
                    isLoading = false
                )
            }
        }
    }

    fun deleteCharacter(id: String) {
        viewModelScope.launch {
            characterRepository.deleteCharacter(id)
        }
    }

    fun saveCharacter(character: Character) {
        viewModelScope.launch {
            characterRepository.insertCharacter(character)
        }
    }

    fun duplicateCharacter(id: String) {
        viewModelScope.launch {
            characterRepository.duplicateCharacter(id)
        }
    }
}

class HomeViewModelFactory(
    private val characterRepository: CharacterRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(characterRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
