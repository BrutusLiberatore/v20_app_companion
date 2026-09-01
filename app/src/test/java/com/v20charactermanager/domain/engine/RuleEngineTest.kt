package com.v20charactermanager.domain.engine

import com.v20charactermanager.domain.definition.*
import com.v20charactermanager.domain.model.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class RuleEngineTest {

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
    fun `derived values calculated correctly`() {
        val derived = RuleEngine.calculateDerivedValues(character)
        assertEquals(2, derived.humanity) // 1+1
        assertEquals(1, derived.willpowerPermanent) // courage
        assertEquals(10, derived.bloodPoolMax) // 13th gen
        assertEquals(1, derived.bloodPerTurn) // 13th gen
        assertEquals(5, derived.maxTrait) // 13th gen
    }

    @Test
    fun `attribute within generation limit is valid`() {
        assertTrue(RuleEngine.validateAttributeForGeneration(character, AttributeId.STRENGTH, 5))
    }

    @Test
    fun `attribute above generation limit is invalid`() {
        assertFalse(RuleEngine.validateAttributeForGeneration(character, AttributeId.STRENGTH, 6))
    }

    @Test
    fun `ability within creation limit is valid`() {
        assertTrue(RuleEngine.validateAbilityForCreation(character, AbilityId.ATHLETICS, 3))
    }

    @Test
    fun `ability above creation limit is invalid`() {
        assertFalse(RuleEngine.validateAbilityForCreation(character, AbilityId.ATHLETICS, 4))
    }

    @Test
    fun `calculate soak from stamina`() {
        val updated = character.setAttributeValue(AttributeId.STAMINA, 3)
        assertEquals(3, RuleEngine.calculateSoak(updated))
    }

    @Test
    fun `calculate initiative from dexterity and wits`() {
        val updated = character
            .setAttributeValue(AttributeId.DEXTERITY, 3)
            .setAttributeValue(AttributeId.WITS, 2)
        assertEquals(5, RuleEngine.calculateInitiative(updated))
    }
}
