package com.v20charactermanager.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CombatEngineTest {

    private fun combatant(id: String, initiative: Int, name: String = "Combatant $id") =
        Combatant(id = id, name = name, initiative = initiative)

    @Test
    fun `start sorts combatants by initiative descending`() {
        val state = CombatEngine.start(
            listOf(combatant("a", 7), combatant("b", 19), combatant("c", 12))
        )
        assertTrue(state.active)
        assertEquals(listOf("b", "c", "a"), state.combatants.map { it.id })
        assertEquals("b", state.combatants[state.currentIndex].id)
        assertEquals(1, state.round)
    }

    @Test
    fun `start keeps insertion order for tied initiatives`() {
        val state = CombatEngine.start(
            listOf(combatant("first", 15), combatant("second", 15), combatant("third", 15))
        )
        assertEquals(listOf("first", "second", "third"), state.combatants.map { it.id })
    }

    @Test
    fun `start keeps re-roll flag from argument`() {
        assertTrue(CombatEngine.start(reRollEachRound = true).reRollEachRound)
        assertFalse(CombatEngine.start(reRollEachRound = false).reRollEachRound)
    }

    @Test
    fun `start with empty list yields active empty tracker`() {
        val state = CombatEngine.start()
        assertTrue(state.active)
        assertTrue(state.combatants.isEmpty())
        assertEquals(0, state.currentIndex)
    }

    @Test
    fun `upsert adds a new combatant in initiative order`() {
        val state = CombatEngine.start(listOf(combatant("a", 10), combatant("b", 8)))
        val updated = CombatEngine.upsert(state, combatant("c", 20))
        assertEquals(listOf("c", "a", "b"), updated.combatants.map { it.id })
    }

    @Test
    fun `upsert updates existing combatant without duplicating`() {
        val state = CombatEngine.start(listOf(combatant("a", 10), combatant("b", 8)))
        val updated = CombatEngine.upsert(state, combatant("b", 14))
        assertEquals(2, updated.combatants.size)
        assertEquals(14, updated.combatants.first { it.id == "b" }.initiative)
    }

    @Test
    fun `upsert re-sorts but keeps the current turn on the same combatant`() {
        val state = CombatEngine.start(
            listOf(combatant("a", 10), combatant("b", 8), combatant("c", 6))
        )
        assertEquals("a", state.combatants[state.currentIndex].id)
        val updated = CombatEngine.upsert(state, combatant("b", 20))
        assertEquals(listOf("b", "a", "c"), updated.combatants.map { it.id })
        assertEquals("a", updated.combatants[updated.currentIndex].id)
    }

    @Test
    fun `remove adjusts the current index when the current combatant is removed`() {
        val state = CombatEngine.start(
            listOf(combatant("a", 10), combatant("b", 8), combatant("c", 6))
        )
        val updated = CombatEngine.remove(state, "a")
        assertEquals(listOf("b", "c"), updated.combatants.map { it.id })
        assertEquals("b", updated.combatants[updated.currentIndex].id)
    }

    @Test
    fun `remove keeps the current turn when another combatant is removed`() {
        val state = CombatEngine.start(
            listOf(combatant("a", 10), combatant("b", 8), combatant("c", 6))
        )
        val withSecondCurrent = state.copy(currentIndex = 1)
        val updated = CombatEngine.remove(withSecondCurrent, "a")
        assertEquals("b", updated.combatants[updated.currentIndex].id)
    }

    @Test
    fun `remove of unknown id is a no-op`() {
        val state = CombatEngine.start(listOf(combatant("a", 10)))
        assertEquals(state, CombatEngine.remove(state, "missing"))
    }

    @Test
    fun `advance moves to the next combatant`() {
        val state = CombatEngine.start(
            listOf(combatant("a", 10), combatant("b", 8), combatant("c", 6))
        )
        val next = CombatEngine.advance(state)
        assertEquals("b", next.combatants[next.currentIndex].id)
        assertEquals(1, next.round)
    }

    @Test
    fun `advance past the last combatant starts a new round`() {
        val state = CombatEngine.start(
            listOf(combatant("a", 10), combatant("b", 8), combatant("c", 6))
        )
        val second = CombatEngine.advance(CombatEngine.advance(state))
        val thirdRound = CombatEngine.advance(second)
        assertEquals(0, thirdRound.currentIndex)
        assertEquals(2, thirdRound.round)
    }

    @Test
    fun `advance is a no-op when inactive or empty`() {
        val inactive = CombatState(active = false, combatants = listOf(combatant("a", 10)))
        assertEquals(inactive, CombatEngine.advance(inactive))
        val activeEmpty = CombatEngine.start()
        assertEquals(activeEmpty, CombatEngine.advance(activeEmpty))
    }

    @Test
    fun `setReroll toggles the re-roll flag only`() {
        val state = CombatEngine.start(listOf(combatant("a", 10)), reRollEachRound = true)
        val frozen = CombatEngine.setReroll(state, false)
        assertFalse(frozen.reRollEachRound)
        assertEquals(state.combatants, frozen.combatants)
        assertTrue(CombatEngine.setReroll(frozen, true).reRollEachRound)
    }

    @Test
    fun `end resets the whole tracker`() {
        val state = CombatEngine.start(listOf(combatant("a", 10)), reRollEachRound = false)
        val ended = CombatEngine.end()
        assertFalse(ended.active)
        assertTrue(ended.combatants.isEmpty())
        assertEquals(1, ended.round)
        assertTrue(ended.reRollEachRound)
        assertEquals(CombatState(), ended)
        assertTrue(state.active)
    }
}
