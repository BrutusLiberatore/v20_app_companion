package com.v20charactermanager.domain.engine

import com.v20charactermanager.domain.definition.ClanId
import org.junit.Assert.assertEquals
import org.junit.Test

class XpCostCalculatorTest {

    @Test
    fun `attribute cost increases by cost per dot`() {
        assertEquals(10, XpCostCalculator.calculateAttributeCost(1))
        assertEquals(15, XpCostCalculator.calculateAttributeCost(2))
        assertEquals(20, XpCostCalculator.calculateAttributeCost(3))
        assertEquals(25, XpCostCalculator.calculateAttributeCost(4))
    }

    @Test
    fun `existing ability cost increases by cost per dot`() {
        assertEquals(4, XpCostCalculator.calculateAbilityCost(1, isNew = false))
        assertEquals(6, XpCostCalculator.calculateAbilityCost(2, isNew = false))
        assertEquals(8, XpCostCalculator.calculateAbilityCost(3, isNew = false))
        assertEquals(10, XpCostCalculator.calculateAbilityCost(4, isNew = false))
    }

    @Test
    fun `new ability cost is flat rate`() {
        assertEquals(3, XpCostCalculator.calculateAbilityCost(0, isNew = true))
    }

    @Test
    fun `background cost increases by cost per dot`() {
        assertEquals(2, XpCostCalculator.calculateBackgroundCost(1))
        assertEquals(3, XpCostCalculator.calculateBackgroundCost(2))
        assertEquals(4, XpCostCalculator.calculateBackgroundCost(3))
        assertEquals(5, XpCostCalculator.calculateBackgroundCost(4))
    }

    @Test
    fun `virtue cost increases by cost per dot`() {
        assertEquals(4, XpCostCalculator.calculateVirtueCost(1))
        assertEquals(6, XpCostCalculator.calculateVirtueCost(2))
        assertEquals(8, XpCostCalculator.calculateVirtueCost(3))
        assertEquals(10, XpCostCalculator.calculateVirtueCost(4))
    }

    @Test
    fun `standard discipline increase cost`() {
        assertEquals(5, XpCostCalculator.calculateDisciplineCost(ClanId.VENTRUE, 1, isNew = false))
        assertEquals(10, XpCostCalculator.calculateDisciplineCost(ClanId.VENTRUE, 2, isNew = false))
        assertEquals(15, XpCostCalculator.calculateDisciplineCost(ClanId.VENTRUE, 3, isNew = false))
        assertEquals(20, XpCostCalculator.calculateDisciplineCost(ClanId.VENTRUE, 4, isNew = false))
    }

    @Test
    fun `standard new discipline cost`() {
        assertEquals(7, XpCostCalculator.calculateDisciplineCost(ClanId.VENTRUE, 0, isNew = true))
    }

    @Test
    fun `caitiff discipline cost is higher`() {
        assertEquals(6, XpCostCalculator.calculateDisciplineCost(ClanId.CAITIFF, 1, isNew = false))
        assertEquals(12, XpCostCalculator.calculateDisciplineCost(ClanId.CAITIFF, 2, isNew = false))
        assertEquals(10, XpCostCalculator.calculateDisciplineCost(ClanId.CAITIFF, 0, isNew = true))
    }

    @Test
    fun `discipline cost scales linearly with level`() {
        for (level in 1..4) {
            val cost = XpCostCalculator.calculateDisciplineCost(ClanId.VENTRUE, level, isNew = false)
            assertEquals(5 * level, cost)
        }
    }
}
