package com.v20charactermanager.ui.creation

import com.v20charactermanager.domain.definition.AttributeId
import com.v20charactermanager.domain.definition.RuleSet
import com.v20charactermanager.domain.model.AbilityValue
import com.v20charactermanager.domain.model.AttributeValue
import org.junit.Assert.assertEquals
import org.junit.Test

class CreationDotRulesTest {

    @Test
    fun `single dot sets exactly one point on an ability`() {
        assertEquals(1, CreationDotRules.nextValue(current = 0, dot = 1, minValue = 0))
    }

    @Test
    fun `filled dot decreases the ability by one`() {
        assertEquals(0, CreationDotRules.nextValue(current = 1, dot = 1, minValue = 0))
        assertEquals(2, CreationDotRules.nextValue(current = 3, dot = 3, minValue = 0))
    }

    @Test
    fun `tapping a different dot sets the absolute value`() {
        assertEquals(3, CreationDotRules.nextValue(current = 1, dot = 3, minValue = 0))
        assertEquals(2, CreationDotRules.nextValue(current = 0, dot = 2, minValue = 0))
    }

    @Test
    fun `attribute never goes below its minimum`() {
        assertEquals(1, CreationDotRules.nextValue(current = 1, dot = 1, minValue = 1))
        assertEquals(2, CreationDotRules.nextValue(current = 3, dot = 3, minValue = 1))
    }

    @Test
    fun `appearance can return to zero`() {
        assertEquals(0, CreationDotRules.nextValue(current = 1, dot = 1, minValue = 0))
    }

    @Test
    fun `abilities are limited to the creation maximum of three dots`() {
        assertEquals(3, CreationDotRules.ABILITY_MAX_DOTS)
        assertEquals(3, RuleSet.ABILITY_MAX_CREATION)
    }

    @Test
    fun `attribute points spent sum values above base`() {
        val attributes = listOf(
            AttributeValue(AttributeId.STRENGTH, 4),
            AttributeValue(AttributeId.DEXTERITY, 3),
            AttributeValue(AttributeId.APPEARANCE, 2)
        )
        // base 1 is free on every attribute: (4-1) + (3-1) + (2-1) = 6
        assertEquals(6, CreationDotRules.attributePointsSpent(attributes, nosferatu = false))
    }

    @Test
    fun `nosferatu appearance has no base point`() {
        val attributes = listOf(
            AttributeValue(AttributeId.STRENGTH, 4),
            AttributeValue(AttributeId.APPEARANCE, 0)
        )
        assertEquals(3, CreationDotRules.attributePointsSpent(attributes, nosferatu = true))
    }

    @Test
    fun `ability points spent sum the ratings`() {
        val abilities = listOf(
            AbilityValue(com.v20charactermanager.domain.definition.AbilityId.ATHLETICS, 3),
            AbilityValue(com.v20charactermanager.domain.definition.AbilityId.STEALTH, 1)
        )
        assertEquals(4, CreationDotRules.abilityPointsSpent(abilities))
    }
}
