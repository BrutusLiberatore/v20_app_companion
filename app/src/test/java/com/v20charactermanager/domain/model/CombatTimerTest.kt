package com.v20charactermanager.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CombatTimerTest {

    private fun combatant(id: String, initiative: Int): Combatant =
        Combatant(id = id, name = id, initiative = initiative)

    @Test
    fun `start with previous config keeps timer settings and starts countdown`() {
        val config = CombatState(timerSeconds = 30, timerAutoAdvance = false)
        val state = CombatEngine.start(
            combatants = listOf(combatant("a", 10)),
            previousConfig = config,
            now = 1_000_000L
        )
        assertEquals(30, state.timerSeconds)
        assertFalse(state.timerAutoAdvance)
        assertEquals(1_000_000L + 30_000L, state.timerEndsAt)
        assertEquals(0L, state.timerPausedRemainingMs)
    }

    @Test
    fun `start without config has timer off`() {
        val state = CombatEngine.start()
        assertEquals(0, state.timerSeconds)
        assertEquals(0L, state.timerEndsAt)
        assertTrue(state.timerAutoAdvance)
    }

    @Test
    fun `advance restarts the turn timer and wrapping round too`() {
        var state = CombatEngine.start(
            combatants = listOf(combatant("a", 10), combatant("b", 5)),
            previousConfig = CombatState(timerSeconds = 15),
            now = 0L
        )
        assertEquals(15_000L, state.timerEndsAt)
        state = CombatEngine.advance(state, now = 5_000L)
        assertEquals(5_000L + 15_000L, state.timerEndsAt)
        assertEquals(1, state.currentIndex)
        // Wrap to a new round: timer restarts again
        state = CombatEngine.advance(state, now = 20_000L)
        assertEquals(20_000L + 15_000L, state.timerEndsAt)
        assertEquals(2, state.round)
    }

    @Test
    fun `advance with timer off clears any stale deadline`() {
        val state = CombatEngine.start(combatants = listOf(combatant("a", 10)), now = 0L)
        assertEquals(0L, state.timerEndsAt)
        val advanced = CombatEngine.advance(state, now = 1_000L)
        assertEquals(0L, advanced.timerEndsAt)
    }

    @Test
    fun `setTimer restarts running timer and stops with zero`() {
        var state = CombatEngine.start(
            combatants = listOf(combatant("a", 10)),
            previousConfig = CombatState(timerSeconds = 60),
            now = 0L
        )
        assertEquals(60_000L, state.timerEndsAt)
        // Change duration while running: countdown restarts from the new full duration
        state = CombatEngine.setTimer(state, 30, now = 10_000L)
        assertEquals(30, state.timerSeconds)
        assertEquals(10_000L + 30_000L, state.timerEndsAt)
        // Turn the timer off: deadline is cleared
        state = CombatEngine.setTimer(state, 0, now = 20_000L)
        assertEquals(0, state.timerSeconds)
        assertEquals(0L, state.timerEndsAt)
        assertEquals(0L, state.timerPausedRemainingMs)
    }

    @Test
    fun `pause and resume keep the remaining time`() {
        var state = CombatEngine.start(
            combatants = listOf(combatant("a", 10)),
            previousConfig = CombatState(timerSeconds = 30),
            now = 0L
        )
        state = CombatEngine.pauseTimer(state, now = 12_000L)
        assertEquals(0L, state.timerEndsAt)
        assertEquals(18_000L, state.timerPausedRemainingMs)
        assertEquals(18_000L, CombatEngine.timerRemainingMs(state, now = 99_000L))
        // Pausing again is a no-op
        assertEquals(state, CombatEngine.pauseTimer(state, now = 13_000L))

        state = CombatEngine.resumeTimer(state, now = 50_000L)
        assertEquals(50_000L + 18_000L, state.timerEndsAt)
        assertEquals(0L, state.timerPausedRemainingMs)
        // Resuming with nothing paused is a no-op
        assertEquals(state, CombatEngine.resumeTimer(state, now = 60_000L))
    }

    @Test
    fun `remaining and expiry helpers follow the clock`() {
        val state = CombatEngine.start(
            combatants = listOf(combatant("a", 10)),
            previousConfig = CombatState(timerSeconds = 30),
            now = 1_000L
        )
        assertEquals(30_000L, CombatEngine.timerRemainingMs(state, now = 1_000L))
        assertEquals(21_000L, CombatEngine.timerRemainingMs(state, now = 10_000L))
        assertEquals(0L, CombatEngine.timerRemainingMs(state, now = 45_000L))
        assertFalse(CombatEngine.isTimerExpired(state, now = 30_999L))
        assertTrue(CombatEngine.isTimerExpired(state, now = 31_000L))
    }

    @Test
    fun `end keeps the timer configuration for the next fight`() {
        val state = CombatEngine.start(
            combatants = listOf(combatant("a", 10)),
            previousConfig = CombatState(timerSeconds = 45, timerAutoAdvance = false),
            now = 0L
        )
        val ended = CombatEngine.end(state)
        assertFalse(ended.active)
        assertEquals(45, ended.timerSeconds)
        assertFalse(ended.timerAutoAdvance)
        assertEquals(0L, ended.timerEndsAt)
        // Plain end() keeps the defaults
        val fresh = CombatEngine.end()
        assertEquals(0, fresh.timerSeconds)
        assertTrue(fresh.timerAutoAdvance)
    }

    @Test
    fun `auto advance toggle is stored`() {
        var state = CombatEngine.start(previousConfig = CombatState(timerSeconds = 15))
        assertTrue(state.timerAutoAdvance)
        state = CombatEngine.setTimerAutoAdvance(state, false)
        assertFalse(state.timerAutoAdvance)
        state = CombatEngine.setTimerAutoAdvance(state, true)
        assertTrue(state.timerAutoAdvance)
    }
}
