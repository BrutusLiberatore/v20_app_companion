package com.v20charactermanager.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DiceRevealTest {

    @Test
    fun `botch is always critical`() {
        assertTrue(DiceReveal.isCritical(isBotch = true, netSuccesses = 0, pool = 7))
        assertTrue(DiceReveal.isCritical(isBotch = true, netSuccesses = null, pool = 0))
    }

    @Test
    fun `total success is critical`() {
        assertTrue(DiceReveal.isCritical(isBotch = false, netSuccesses = 5, pool = 5))
        assertTrue(DiceReveal.isCritical(isBotch = false, netSuccesses = 7, pool = 5))
    }

    @Test
    fun `ordinary success or failure is not critical`() {
        assertFalse(DiceReveal.isCritical(isBotch = false, netSuccesses = 3, pool = 5))
        assertFalse(DiceReveal.isCritical(isBotch = false, netSuccesses = 0, pool = 5))
    }

    @Test
    fun `rolls without data are never critical`() {
        assertFalse(DiceReveal.isCritical(isBotch = null, netSuccesses = null, pool = 7))
        assertFalse(DiceReveal.isCritical(null, null, 0))
    }

    @Test
    fun `empty pool never yields total success`() {
        assertFalse(DiceReveal.isCritical(isBotch = false, netSuccesses = 0, pool = 0))
    }

    @Test
    fun `mode triggers according to policy`() {
        assertFalse(DiceRevealMode.OFF.triggersOn(isCritical = true))
        assertFalse(DiceRevealMode.OFF.triggersOn(isCritical = false))
        assertTrue(DiceRevealMode.CRITICAL.triggersOn(isCritical = true))
        assertFalse(DiceRevealMode.CRITICAL.triggersOn(isCritical = false))
        assertTrue(DiceRevealMode.EVERY.triggersOn(isCritical = true))
        assertTrue(DiceRevealMode.EVERY.triggersOn(isCritical = false))
    }

    @Test
    fun `mode round-trips through preference values`() {
        for (mode in DiceRevealMode.entries) {
            assertEquals(mode, DiceRevealMode.fromPrefValue(DiceRevealMode.toPrefValue(mode)))
        }
        assertEquals(DiceRevealMode.EVERY, DiceRevealMode.fromPrefValue(DiceRevealMode.PREF_VALUE_EVERY))
        assertEquals(DiceRevealMode.OFF, DiceRevealMode.fromPrefValue(DiceRevealMode.PREF_VALUE_OFF))
        assertEquals(DiceRevealMode.CRITICAL, DiceRevealMode.fromPrefValue(DiceRevealMode.PREF_VALUE_CRITICAL))
        assertEquals(DiceRevealMode.CRITICAL, DiceRevealMode.fromPrefValue(null))
        assertEquals(DiceRevealMode.CRITICAL, DiceRevealMode.fromPrefValue("garbage"))
    }

    @Test
    fun `critical detection from a DiceRoll message`() {
        val base = LiveRoomMessage.DiceRoll(
            characterId = "c1",
            playerName = "Ana",
            pool = "5",
            result = "5 successes"
        )
        assertTrue(DiceReveal.isCritical(base.copy(isBotch = true, netSuccesses = 0)))
        assertTrue(DiceReveal.isCritical(base.copy(isBotch = false, netSuccesses = 5)))
        assertFalse(DiceReveal.isCritical(base.copy(isBotch = false, netSuccesses = 2)))
        assertFalse(DiceReveal.isCritical(base))
        assertFalse(
            DiceReveal.isCritical(base.copy(pool = "abc", isBotch = false, netSuccesses = 5))
        )
    }
}
