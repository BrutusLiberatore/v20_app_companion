package com.v20charactermanager.domain.engine

import com.v20charactermanager.domain.definition.VirtueId
import com.v20charactermanager.domain.model.Character

object WillpowerCalculator {

    fun calculatePermanent(character: Character): Int {
        return character.virtues.find { it.id == VirtueId.COURAGE }?.value ?: 1
    }

    fun canSpendWillpower(character: Character, amount: Int = 1): Boolean {
        return character.willpower.current >= amount
    }

    fun getWillpowerRollDifficulty(difficulty: Int): Int {
        return difficulty.coerceIn(2, 10)
    }

    fun canResistFrenzy(character: Character): Boolean {
        return character.willpower.current > 0
    }

    fun canResistDominate(character: Character): Boolean {
        return character.willpower.current > 0
    }
}
