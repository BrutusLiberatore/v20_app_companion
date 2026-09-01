package com.v20charactermanager.domain.engine

import com.v20charactermanager.domain.definition.*
import com.v20charactermanager.domain.model.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class WillpowerCalculatorTest {

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
    fun `permanent willpower equals courage`() {
        val willpower = WillpowerCalculator.calculatePermanent(character)
        assertEquals(1, willpower) // Base courage = 1
    }

    @Test
    fun `willpower increases with courage`() {
        val updated = character.setVirtueValue(VirtueId.COURAGE, 5)
        val willpower = WillpowerCalculator.calculatePermanent(updated)
        assertEquals(5, willpower)
    }

    @Test
    fun `can spend willpower when available`() {
        assertTrue(WillpowerCalculator.canSpendWillpower(character, 1))
    }

    @Test
    fun `cannot spend willpower when empty`() {
        var char = character
        char = char.copy(willpower = char.willpower.copy(current = 0))
        assertFalse(WillpowerCalculator.canSpendWillpower(char, 1))
    }

    @Test
    fun `can resist frenzy with willpower`() {
        assertTrue(WillpowerCalculator.canResistFrenzy(character))
    }

    @Test
    fun `cannot resist frenzy without willpower`() {
        var char = character
        char = char.copy(willpower = char.willpower.copy(current = 0))
        assertFalse(WillpowerCalculator.canResistFrenzy(char))
    }
}
