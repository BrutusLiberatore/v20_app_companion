package com.v20charactermanager.domain.model

import com.v20charactermanager.domain.definition.DamageType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CharacterStatusTest {

    private fun character(
        blood: Int = 10,
        bloodMax: Int = 10,
        willpower: Int = 5,
        willpowerPermanent: Int = 5
    ): Character = Character(id = "test-char").copy(
        bloodPool = BloodPoolState(maximum = bloodMax, current = blood),
        willpower = WillpowerState(permanent = willpowerPermanent, current = willpower)
    )

    @Test
    fun `blood update is applied and clamped to range`() {
        val char = character(blood = 10, bloodMax = 10)
        val spent = char.applyStatUpdate("blood", 4, null)
        assertNotNull(spent)
        assertEquals(4, spent!!.bloodPool.current)
        assertEquals(0, char.applyStatUpdate("blood", -3, null)!!.bloodPool.current)
        assertEquals(10, char.applyStatUpdate("blood", 99, null)!!.bloodPool.current)
    }

    @Test
    fun `willpower update is applied and clamped to range`() {
        val char = character(willpower = 5, willpowerPermanent = 5)
        assertEquals(2, char.applyStatUpdate("willpower", 2, null)!!.willpower.current)
        assertEquals(0, char.applyStatUpdate("willpower", -1, null)!!.willpower.current)
        assertEquals(5, char.applyStatUpdate("willpower", 50, null)!!.willpower.current)
    }

    @Test
    fun `health damage sets the level type`() {
        val char = character()
        val updated = char.applyStatUpdate("health", null, "3:LETHAL")
        assertNotNull(updated)
        assertEquals(DamageType.LETHAL, updated!!.health.levels[3])
        assertEquals(DamageType.AGGRAVATED, char.applyStatUpdate("health", null, "6:AGGRAVATED")!!.health.levels[6])
        assertEquals(DamageType.BASHING, char.applyStatUpdate("health", null, "0:BASHING")!!.health.levels[0])
    }

    @Test
    fun `health heal clears the level`() {
        val injured = character().copy(
            health = character().health.withDamage(2, DamageType.LETHAL)
        )
        val healed = injured.applyStatUpdate("health", null, "2:HEAL")
        assertNotNull(healed)
        assertEquals(DamageType.NONE, healed!!.health.levels[2])
        // Healing an untouched level is a harmless no-op
        assertEquals(DamageType.NONE, injured.applyStatUpdate("health", null, "0:HEAL")!!.health.levels[0])
    }

    @Test
    fun `invalid health messages are rejected`() {
        val char = character()
        assertNull(char.applyStatUpdate("health", null, null))
        assertNull(char.applyStatUpdate("health", null, "abc:LETHAL"))
        assertNull(char.applyStatUpdate("health", null, "9:LETHAL"))
        assertNull(char.applyStatUpdate("health", null, "-1:LETHAL"))
        assertNull(char.applyStatUpdate("health", null, "2:BOGUS"))
        assertNull(char.applyStatUpdate("health", null, "2"))
    }

    @Test
    fun `unknown fields and missing values are rejected`() {
        val char = character()
        assertNull(char.applyStatUpdate("experience", 5, null))
        assertNull(char.applyStatUpdate("blood", null, null))
        assertNull(char.applyStatUpdate("willpower", null, "3"))
    }

    @Test
    fun `updates do not mutate other stats`() {
        val char = character(blood = 7, willpower = 3)
        val updated = char.applyStatUpdate("blood", 5, null)!!
        assertEquals(3, updated.willpower.current)
        assertEquals(char.health, updated.health)
        assertEquals(char.bloodPool.maximum, updated.bloodPool.maximum)
    }
}
