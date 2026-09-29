package com.v20charactermanager.domain.model

import kotlinx.serialization.Serializable

/**
 * A combat participant with their initiative rating for the current round.
 * V20 initiative: 1d10 + Dexterity + Wits (highest acts first).
 */
@Serializable
data class Combatant(
    val id: String,
    val name: String,
    val initiative: Int,
    val characterId: String? = null,
    val playerId: String? = null
)

/**
 * Combat tracker state. Pure data: order is kept sorted by initiative
 * (descending, ties keep insertion order).
 */
@Serializable
data class CombatState(
    val active: Boolean = false,
    val combatants: List<Combatant> = emptyList(),
    val currentIndex: Int = 0,
    val round: Int = 1,
    /** V20 RAW: initiative is re-rolled every round. When false the order is frozen. */
    val reRollEachRound: Boolean = true
)

object CombatEngine {

    fun start(combatants: List<Combatant> = emptyList(), reRollEachRound: Boolean = true): CombatState =
        CombatState(
            active = true,
            combatants = sortByInitiative(combatants),
            currentIndex = 0,
            round = 1,
            reRollEachRound = reRollEachRound
        )

    fun upsert(state: CombatState, combatant: Combatant): CombatState {
        val currentId = state.combatants.getOrNull(state.currentIndex)?.id
        val merged = state.combatants.filterNot { it.id == combatant.id } + combatant
        val sorted = sortByInitiative(merged)
        val newCurrentIndex = currentId
            ?.let { id -> sorted.indexOfFirst { it.id == id } }
            ?.takeIf { it >= 0 } ?: 0
        return state.copy(combatants = sorted, currentIndex = newCurrentIndex)
    }

    fun remove(state: CombatState, id: String): CombatState {
        val currentId = state.combatants.getOrNull(state.currentIndex)?.id
        val remaining = state.combatants.filterNot { it.id == id }
        if (remaining.size == state.combatants.size) return state
        if (remaining.isEmpty()) return state.copy(combatants = emptyList(), currentIndex = 0)
        val newCurrentIndex = if (currentId == id) {
            // The current combatant was removed: the slot now points at the next one
            state.currentIndex.coerceAtMost(remaining.size - 1)
        } else {
            currentId?.let { cid -> remaining.indexOfFirst { it.id == cid } } ?: 0
        }.coerceAtLeast(0)
        return state.copy(combatants = remaining, currentIndex = newCurrentIndex)
    }

    /** Advance to the next combatant; wrapping past the last one starts a new round. */
    fun advance(state: CombatState): CombatState {
        if (!state.active || state.combatants.isEmpty()) return state
        val next = state.currentIndex + 1
        return if (next >= state.combatants.size) {
            state.copy(currentIndex = 0, round = state.round + 1)
        } else {
            state.copy(currentIndex = next)
        }
    }

    fun setReroll(state: CombatState, enabled: Boolean): CombatState =
        state.copy(reRollEachRound = enabled)

    fun end(): CombatState = CombatState()

    private fun sortByInitiative(list: List<Combatant>): List<Combatant> =
        list.sortedByDescending { it.initiative }
}
