package com.v20charactermanager.domain.model

import com.v20charactermanager.domain.definition.DamageType
import com.v20charactermanager.domain.definition.HealthLevel
import kotlinx.serialization.Serializable

@Serializable
data class HealthState(
    val levels: List<DamageType> = listOf(
        DamageType.NONE,
        DamageType.NONE,
        DamageType.NONE,
        DamageType.NONE,
        DamageType.NONE,
        DamageType.NONE,
        DamageType.NONE
    )
) {
    init {
        require(levels.size == 7) { "Health must have exactly 7 levels" }
    }

    val totalDamage: Int
        get() = levels.count { it != DamageType.NONE }

    val bashingDamage: Int
        get() = levels.count { it == DamageType.BASHING }

    val lethalDamage: Int
        get() = levels.count { it == DamageType.LETHAL }

    val aggravatedDamage: Int
        get() = levels.count { it == DamageType.AGGRAVATED }

    val isAlive: Boolean
        get() = levels.last() == DamageType.NONE || levels.last() == DamageType.BASHING

    fun withDamage(index: Int, type: DamageType): HealthState {
        require(index in 0..6) { "Health index must be between 0 and 6" }
        return copy(levels = levels.toMutableList().apply { set(index, type) })
    }

    fun heal(index: Int): HealthState {
        require(index in 0..6) { "Health index must be between 0 and 6" }
        return copy(levels = levels.toMutableList().apply { set(index, DamageType.NONE) })
    }

    fun getPenalty(healthLevel: HealthLevel): Int {
        return if (levels[healthLevel.index] != DamageType.NONE) healthLevel.penalty else 0
    }

    fun totalPenalty(): Int {
        var penalty = 0
        HealthLevel.entries.forEach { level ->
            if (levels[level.index] != DamageType.NONE) {
                penalty += level.penalty
            }
        }
        return penalty
    }
}
