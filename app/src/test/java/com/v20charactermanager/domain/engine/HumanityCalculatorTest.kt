package com.v20charactermanager.domain.engine

import com.v20charactermanager.domain.definition.*
import com.v20charactermanager.domain.model.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class HumanityCalculatorTest {

    private lateinit var character: Character

    @Before
    fun setUp() {
        character = Character(
            id = "test-1",
            identity = CharacterIdentity(
                name = "Test Character",
                clan = ClanId.BRUAH,
                generation = 13
            )
        )
    }

    @Test
    fun `humanity equals conscience plus self-control`() {
        val humanity = HumanityCalculator.calculate(character)
        assertEquals(2, humanity) // Base: 1 + 1
    }

    @Test
    fun `humanity increases with virtues`() {
        val updated = character
            .setVirtueValue(VirtueId.CONSCIENCE, 3)
            .setVirtueValue(VirtueId.SELF_CONTROL, 4)
        val humanity = HumanityCalculator.calculate(updated)
        assertEquals(7, humanity)
    }

    @Test
    fun `humanity minimum is 2`() {
        val humanity = HumanityCalculator.calculate(character)
        assertTrue(humanity >= 2)
    }

    @Test
    fun `humanity maximum is 10`() {
        val updated = character
            .setVirtueValue(VirtueId.CONSCIENCE, 5)
            .setVirtueValue(VirtueId.SELF_CONTROL, 5)
        val humanity = HumanityCalculator.calculate(updated)
        assertEquals(10, humanity)
    }
}
