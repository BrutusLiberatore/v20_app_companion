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
    fun `creation gives 0 freebie points and standard allocation costs nothing`() {
        val report = calculator.calculate(character)
        assertEquals(0, report.initialPoints)
        assertEquals(0, report.usedPoints)
        assertEquals(0, report.remainingPoints)
    }

    @Test
    fun `attribute dots within creation total are free`() {
        val updated = character
            .setAttributeValue(AttributeId.STRENGTH, 4)
            .setAttributeValue(AttributeId.DEXTERITY, 4)
            .setAttributeValue(AttributeId.STAMINA, 2)
        val report = calculator.calculate(updated)
        assertEquals(0, report.usedPoints)
    }

    @Test
    fun `attribute dots beyond creation total cost 5 each`() {
        var updated = character
        AttributeId.entries.forEach { updated = updated.setAttributeValue(it, 3) }
        // 9 attributes x 2 dots above base = 18, creation allows 15 -> 3 extra dots
        val report = calculator.calculate(updated)
        assertEquals(15, report.usedPoints)
        assertEquals(-15, report.remainingPoints)
    }

    @Test
    fun `ability dots beyond creation total cost 2 each`() {
        var updated = character
        AbilityId.entries.take(28).forEach { updated = updated.setAbilityValue(it, 1) }
        // 28 points, creation allows 27 -> 1 extra dot
        val report = calculator.calculate(updated)
        assertEquals(2, report.usedPoints)
    }

    @Test
    fun `discipline dots beyond creation total cost 7 each`() {
        val updated = character.addDiscipline(DisciplineId.POTENCE, 4)
        // 4 dots, creation includes 3 -> 1 extra dot
        val report = calculator.calculate(updated)
        assertEquals(7, report.usedPoints)
    }

    @Test
    fun `background dots beyond creation total cost 1 each`() {
        val updated = character
            .addBackground(BackgroundId.entries.first(), 5)
            .addBackground(BackgroundId.entries[1], 1)
        // 6 dots, creation includes 5 -> 1 extra dot
        val report = calculator.calculate(updated)
        assertEquals(1, report.usedPoints)
    }

    @Test
    fun `virtue dots beyond creation total cost 2 each`() {
        val updated = character
            .setVirtueValue(VirtueId.CONSCIENCE, 3)
            .setVirtueValue(VirtueId.SELF_CONTROL, 2)
            .setVirtueValue(VirtueId.COURAGE, 3)
        // 8 dots, creation includes 7 -> 1 extra dot
        val report = calculator.calculate(updated)
        assertEquals(2, report.usedPoints)
    }

    @Test
    fun `merits cost freebie points`() {
        val updated = character.addMerit(
            MeritValue(id = "merit.ambidextrous", name = "Ambidextrous", cost = 2, description = "")
        )
        val report = calculator.calculate(updated)
        assertEquals(2, report.usedPoints)
        assertEquals(-2, report.remainingPoints)
    }

    @Test
    fun `flaws refund freebie points`() {
        val updated = character.addFlaw(
            FlawValue(id = "flaw.addiction", name = "Addiction", value = 2, description = "")
        )
        val report = calculator.calculate(updated)
        assertEquals(-2, report.usedPoints)
        assertEquals(2, report.remainingPoints)
    }

    @Test
    fun `merits paid by flaws balance to zero`() {
        val updated = character
            .addMerit(MeritValue(id = "merit.empathy", name = "Empathy", cost = 3, description = ""))
            .addFlaw(FlawValue(id = "flaw.lame", name = "Lame", value = 3, description = ""))
        val report = calculator.calculate(updated)
        assertEquals(0, report.usedPoints)
        assertEquals(0, report.remainingPoints)
    }

    @Test
    fun `can afford within remaining freebie points`() {
        val updated = character.addFlaw(
            FlawValue(id = "flaw.addiction", name = "Addiction", value = 2, description = "")
        )
        assertTrue(calculator.canAfford(updated, "background", 1))
        assertFalse(calculator.canAfford(updated, "discipline", 1))
    }

    @Test
    fun `default freebie costs are correct`() {
        val cost = RuleSet.defaultFreebieCost
        assertEquals(5, cost.attributeCost)
        assertEquals(2, cost.abilityCost)
        assertEquals(7, cost.disciplineCost)
        assertEquals(1, cost.backgroundCost)
        assertEquals(2, cost.virtueCost)
        assertEquals(2, cost.humanityCost)
        assertEquals(1, cost.willpowerCost)
    }
}
