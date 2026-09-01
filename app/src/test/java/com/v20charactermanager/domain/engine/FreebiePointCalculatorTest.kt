package com.v20charactermanager.domain.engine

import com.v20charactermanager.domain.definition.*
import com.v20charactermanager.domain.model.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class FreebiePointCalculatorTest {

    private lateinit var calculator: FreebiePointCalculator
    private lateinit var character: Character

    @Before
    fun setUp() {
        calculator = FreebiePointCalculator()
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
    fun `initial character has 0 used points`() {
        val report = calculator.calculate(character)
        assertEquals(0, report.usedPoints)
        assertEquals(15, report.remainingPoints)
    }

    @Test
    fun `adding attribute points costs 5 per dot`() {
        val updated = character.setAttributeValue(AttributeId.STRENGTH, 2)
        val report = calculator.calculate(updated)
        assertEquals(5, report.usedPoints)
        assertEquals(10, report.remainingPoints)
    }

    @Test
    fun `adding ability points costs 2 per dot`() {
        val updated = character.setAbilityValue(AbilityId.ATHLETICS, 1)
        val report = calculator.calculate(updated)
        assertEquals(2, report.usedPoints)
        assertEquals(13, report.remainingPoints)
    }

    @Test
    fun `can afford attribute increase`() {
        assertTrue(calculator.canAfford(character, "attribute", 1))
    }

    @Test
    fun `cannot afford too many points`() {
        var char = character
        repeat(3) { char = char.setAttributeValue(AttributeId.STRENGTH, it + 2) }
        val report = calculator.calculate(char)
        assertFalse(calculator.canAfford(char, "discipline", 1))
    }

    @Test
    fun `discipline costs 7 per dot`() {
        val updated = character.addDiscipline(DisciplineId.POTENCE, 1)
        val report = calculator.calculate(updated)
        assertEquals(7, report.usedPoints)
        assertEquals(8, report.remainingPoints)
    }

    @Test
    fun `background costs 1 per dot`() {
        val updated = character.addBackground(BackgroundId.RESOURCES, 1)
        val report = calculator.calculate(updated)
        assertEquals(1, report.usedPoints)
        assertEquals(14, report.remainingPoints)
    }

    @Test
    fun `virtue above base costs 2 per dot`() {
        val updated = character.setVirtueValue(VirtueId.COURAGE, 2)
        val report = calculator.calculate(updated)
        assertEquals(2, report.usedPoints)
        assertEquals(13, report.remainingPoints)
    }

    @Test
    fun `default freebie costs are correct`() {
        val cost = RuleSet.defaultFreebieCost
        assertEquals(5, cost.attributeCost)
        assertEquals(2, cost.abilityCost)
        assertEquals(7, cost.disciplineCost)
        assertEquals(1, cost.backgroundCost)
        assertEquals(2, cost.virtueCost)
        assertEquals(1, cost.humanityCost)
        assertEquals(1, cost.willpowerCost)
    }
}
