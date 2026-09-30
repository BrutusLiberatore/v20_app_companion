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
    val reRollEachRound: Boolean = true,
    /** Turn timer: seconds per turn (0 = timer off). */
    val timerSeconds: Int = 0,
    /** Auto-advance to the next turn when the timer expires. */
    val timerAutoAdvance: Boolean = true,
    /** Epoch ms (emitter clock) when the current turn's timer expires; 0 = not running. */
    val timerEndsAt: Long = 0,
    /** Remaining ms when the timer is paused; > 0 means paused. */
    val timerPausedRemainingMs: Long = 0,
    /** Emitter clock reading stamped at publish time, so receivers can rebase [timerEndsAt] on their own clock. */
    val timerSyncNow: Long = 0
)

object CombatEngine {

    fun start(
        combatants: List<Combatant> = emptyList(),
        reRollEachRound: Boolean = true,
        previousConfig: CombatState? = null,
        now: Long = 0
    ): CombatState {
        val base = CombatState(
            active = true,
            combatants = sortByInitiative(combatants),
            currentIndex = 0,
            round = 1,
            reRollEachRound = reRollEachRound,
            timerSeconds = previousConfig?.timerSeconds ?: 0,
            timerAutoAdvance = previousConfig?.timerAutoAdvance ?: true
        )
        return startTurnTimer(base, now)
    }

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

    /** Advance to the next combatant; wrapping past the last one starts a new round.
     *  A running turn timer is restarted for the new turn ([now] = current epoch ms). */
    fun advance(state: CombatState, now: Long = 0): CombatState {
        if (!state.active || state.combatants.isEmpty()) return state
        val next = state.currentIndex + 1
        val advanced = if (next >= state.combatants.size) {
            state.copy(currentIndex = 0, round = state.round + 1)
        } else {
            state.copy(currentIndex = next)
        }
        return startTurnTimer(advanced, now)
    }

    fun setReroll(state: CombatState, enabled: Boolean): CombatState =
        state.copy(reRollEachRound = enabled)

    // --- TURN TIMER ---

    /** Sets the per-turn duration in seconds (0 = off). Restarts a running timer,
     *  updates the paused remaining, and starts the timer if combat is active. */
    fun setTimer(state: CombatState, seconds: Int, now: Long = 0): CombatState {
        val s = seconds.coerceAtLeast(0)
        val fullMs = s * 1000L
        return state.copy(
            timerSeconds = s,
            timerEndsAt = when {
                s == 0 -> 0
                state.timerEndsAt > 0 -> now + fullMs
                state.active && state.timerPausedRemainingMs <= 0 && state.timerSeconds <= 0 -> now + fullMs
                else -> 0
            },
            timerPausedRemainingMs = when {
                s == 0 -> 0
                state.timerPausedRemainingMs > 0 -> fullMs
                else -> 0
            }
        )
    }

    fun setTimerAutoAdvance(state: CombatState, enabled: Boolean): CombatState =
        state.copy(timerAutoAdvance = enabled)

    /** Starts (or restarts) the turn timer for the current turn. */
    fun startTurnTimer(state: CombatState, now: Long): CombatState =
        if (state.active && state.timerSeconds > 0) {
            state.copy(timerEndsAt = now + state.timerSeconds * 1000L, timerPausedRemainingMs = 0)
        } else {
            state.copy(timerEndsAt = 0, timerPausedRemainingMs = 0)
        }

    fun pauseTimer(state: CombatState, now: Long): CombatState {
        if (state.timerEndsAt <= 0) return state
        return state.copy(
            timerPausedRemainingMs = (state.timerEndsAt - now).coerceAtLeast(0),
            timerEndsAt = 0
        )
    }

    fun resumeTimer(state: CombatState, now: Long): CombatState {
        val remaining = state.timerPausedRemainingMs
        if (remaining <= 0 || !state.active || state.timerSeconds <= 0) return state
        return state.copy(timerEndsAt = now + remaining, timerPausedRemainingMs = 0)
    }

    fun stopTimer(state: CombatState): CombatState =
        state.copy(timerEndsAt = 0, timerPausedRemainingMs = 0)

    /** Remaining ms for the countdown (running, paused, or 0 when idle/off). */
    fun timerRemainingMs(state: CombatState, now: Long): Long = when {
        state.timerEndsAt > 0 -> (state.timerEndsAt - now).coerceAtLeast(0)
        state.timerPausedRemainingMs > 0 -> state.timerPausedRemainingMs
        else -> 0
    }

    fun isTimerExpired(state: CombatState, now: Long): Boolean =
        state.timerEndsAt > 0 && now >= state.timerEndsAt

    /** Ends combat; the timer configuration (duration + auto-advance) is kept for the next fight. */
    fun end(previous: CombatState? = null): CombatState =
        CombatState(
            timerSeconds = previous?.timerSeconds ?: 0,
            timerAutoAdvance = previous?.timerAutoAdvance ?: true
        )

    private fun sortByInitiative(list: List<Combatant>): List<Combatant> =
        list.sortedByDescending { it.initiative }
}
