package com.v20charactermanager.domain.engine

import com.v20charactermanager.domain.definition.DamageType
import com.v20charactermanager.domain.model.Character

object HumanityCalculator {

    fun calculate(character: Character): Int {
        val virtues = character.virtues
        val conscience = virtues.find { it.id == com.v20charactermanager.domain.definition.VirtueId.CONSCIENCE }?.value ?: 1
        val selfControl = virtues.find { it.id == com.v20charactermanager.domain.definition.VirtueId.SELF_CONTROL }?.value ?: 1
        return conscience + selfControl
    }

    fun getHumanityLossThreshold(currentHumanity: Int): Int {
        return when {
            currentHumanity <= 3 -> 2
            currentHumanity <= 6 -> 1
            else -> 1
        }
    }

    fun shouldRollDegeneration(character: Character, severity: Int): Boolean {
        val humanity = calculate(character)
        return humanity > 0 && severity > 0
    }
}
