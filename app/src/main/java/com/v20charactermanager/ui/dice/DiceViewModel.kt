package com.v20charactermanager.ui.dice

import androidx.lifecycle.ViewModel
import com.v20charactermanager.domain.engine.DiceEngine
import com.v20charactermanager.domain.engine.DiceResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class DiceUiState(
    val pool: Int = 5,
    val difficulty: Int = 6,
    val extraDice: Int = 0,
    val diceModifier: Int = 0,
    val difficultyModifier: Int = 0,
    val useWillpower: Boolean = false,
    val explodingTens: Boolean = false,
    val modifierReason: String = "",
    val result: DiceResult? = null
)

class DiceViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(DiceUiState())
    val uiState: StateFlow<DiceUiState> = _uiState.asStateFlow()

    fun updatePool(value: Int) {
        if (value > 0) _uiState.value = _uiState.value.copy(pool = value)
    }

    fun updateDifficulty(value: Int) {
        if (value in 2..10) _uiState.value = _uiState.value.copy(difficulty = value)
    }

    fun updateExtraDice(value: Int) {
        if (value >= 0) _uiState.value = _uiState.value.copy(extraDice = value)
    }

    fun updateDiceModifier(value: Int) {
        _uiState.value = _uiState.value.copy(diceModifier = value)
    }

    fun updateDifficultyModifier(value: Int) {
        _uiState.value = _uiState.value.copy(difficultyModifier = value)
    }

    fun updateUseWillpower(value: Boolean) {
        _uiState.value = _uiState.value.copy(useWillpower = value)
    }

    fun updateExplodingTens(value: Boolean) {
        _uiState.value = _uiState.value.copy(explodingTens = value)
    }

    fun updateModifierReason(value: String) {
        _uiState.value = _uiState.value.copy(modifierReason = value)
    }

    fun roll() {
        val state = _uiState.value
        val diceResult = DiceEngine.roll(
            pool = state.pool,
            difficulty = state.difficulty,
            diceModifier = state.diceModifier,
            difficultyModifier = state.difficultyModifier,
            extraDice = state.extraDice,
            willpowerUsed = state.useWillpower,
            explodingTens = state.explodingTens
        )
        _uiState.value = state.copy(result = diceResult)
    }
}
