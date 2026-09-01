package com.v20charactermanager.domain.engine

import com.v20charactermanager.domain.definition.GenerationDefinition
import org.junit.Assert.*
import org.junit.Test

class GenerationRulesTest {

    @Test
    fun `13th generation has blood pool 10`() {
        assertEquals(10, GenerationRules.getBloodPoolMax(13))
    }

    @Test
    fun `10th generation has blood pool 13`() {
        assertEquals(13, GenerationRules.getBloodPoolMax(10))
    }

    @Test
    fun `13th generation has blood per turn 1`() {
        assertEquals(1, GenerationRules.getBloodPerTurn(13))
    }

    @Test
    fun `4th generation has blood per turn 10`() {
        assertEquals(10, GenerationRules.getBloodPerTurn(4))
    }

    @Test
    fun `13th generation has max trait 5`() {
        assertEquals(5, GenerationRules.getMaxTrait(13))
    }

    @Test
    fun `7th generation has max trait 6`() {
        assertEquals(6, GenerationRules.getMaxTrait(7))
    }

    @Test
    fun `valid generations are 3-13`() {
        assertTrue(GenerationRules.isValidGeneration(3))
        assertTrue(GenerationRules.isValidGeneration(13))
        assertFalse(GenerationRules.isValidGeneration(2))
        assertFalse(GenerationRules.isValidGeneration(14))
    }

    @Test
    fun `3rd generation has undefined blood pool`() {
        // DOCX shows ??? for 3rd generation - implementation returns default
        val bloodPool = GenerationRules.getBloodPoolMax(3)
        assertTrue(bloodPool >= 1) // Returns a valid default
    }

    @Test
    fun `5th generation has blood pool 40`() {
        assertEquals(40, GenerationRules.getBloodPoolMax(5))
    }
}
