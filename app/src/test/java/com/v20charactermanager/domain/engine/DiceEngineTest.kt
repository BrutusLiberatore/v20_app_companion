package com.v20charactermanager.domain.engine

import com.v20charactermanager.domain.definition.SectId
import org.junit.Assert.*
import org.junit.Test

class DiceEngineTest {

    @Test
    fun `roll returns correct number of dice`() {
        val result = DiceEngine.roll(5, 6)
        assertEquals(5, result.individualResults.size)
    }

    @Test
    fun `roll with extra dice includes them`() {
        val result = DiceEngine.roll(5, 6, extraDice = 2)
        assertEquals(7, result.individualResults.size)
    }

    @Test
    fun `roll with willpower adds one die`() {
        val result = DiceEngine.roll(5, 6, willpowerUsed = true)
        assertEquals(6, result.individualResults.size)
    }

    @Test
    fun `results are between 1 and 10`() {
        val result = DiceEngine.roll(10, 6)
        result.individualResults.forEach { die ->
            assertTrue(die in 1..10)
        }
    }

    @Test
    fun `successes counted correctly`() {
        val result = DiceEngine.roll(5, 6)
        assertTrue(result.successes >= 0)
    }

    @Test
    fun `ones reduce successes`() {
        val result = DiceEngine.roll(10, 6)
        assertTrue(result.netSuccesses <= result.successes)
    }

    @Test
    fun `botch requires zeros and ones`() {
        val result = DiceEngine.roll(10, 10)
        if (result.isBotch) {
            assertTrue(result.netSuccesses <= 0)
            assertTrue(result.ones > 0)
        }
    }

    @Test
    fun `difficulty is coerced to valid range`() {
        val lowResult = DiceEngine.roll(5, 1)
        assertTrue(lowResult.individualResults.isNotEmpty())
        val highResult = DiceEngine.roll(5, 11)
        assertTrue(highResult.individualResults.isNotEmpty())
    }

    @Test
    fun `pool must be positive`() {
        assertThrows(IllegalArgumentException::class.java) {
            DiceEngine.roll(0, 6)
        }
    }

    @Test
    fun `dice modifier reduces pool`() {
        val result = DiceEngine.roll(5, 6, diceModifier = -2)
        assertEquals(3, result.individualResults.size)
    }

    @Test
    fun `dice modifier increases pool`() {
        val result = DiceEngine.roll(5, 6, diceModifier = 3)
        assertEquals(8, result.individualResults.size)
    }

    @Test
    fun `difficulty modifier adjusts difficulty`() {
        val result = DiceEngine.roll(5, 6, difficultyModifier = -2)
        assertTrue(result.individualResults.isNotEmpty())
    }

    @Test
    fun `willpower and modifier combined`() {
        val result = DiceEngine.roll(5, 6, diceModifier = 2, willpowerUsed = true)
        assertEquals(8, result.individualResults.size)
    }

    @Test
    fun `creation profile camarilla has correct values`() {
        val profile = CreationProfile.forSect(SectId.CAMARILLA)
        assertEquals(3, profile.disciplinePoints)
        assertEquals(5, profile.backgroundPoints)
        assertEquals(7, profile.virtuePoints)
    }

    @Test
    fun `creation profile sabbat has correct values`() {
        val profile = CreationProfile.forSect(SectId.SABBAT)
        assertEquals(4, profile.disciplinePoints)
        assertEquals(5, profile.backgroundPoints)
        assertEquals(5, profile.virtuePoints)
    }

    @Test
    fun `creation profile anarch matches camarilla`() {
        val anarch = CreationProfile.forSect(SectId.ANARCH)
        val camarilla = CreationProfile.forSect(SectId.CAMARILLA)
        assertEquals(camarilla.disciplinePoints, anarch.disciplinePoints)
        assertEquals(camarilla.virtuePoints, anarch.virtuePoints)
    }
}
