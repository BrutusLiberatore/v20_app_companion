package com.v20charactermanager.domain.engine

import com.v20charactermanager.domain.definition.*
import com.v20charactermanager.domain.model.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class CharacterCreationValidatorTest {

    private lateinit var validator: CharacterCreationValidator
    private lateinit var character: Character

    @Before
    fun setUp() {
        validator = CharacterCreationValidator()
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
    fun `valid identity passes validation`() {
        val result = validator.validateIdentity(character)
        assertTrue(result.isValid)
    }

    @Test
    fun `empty name fails identity validation`() {
        val updated = character.copy(identity = character.identity.copy(name = ""))
        val result = validator.validateIdentity(updated)
        assertFalse(result.isValid)
        assertTrue(result.errors.any { it.contains("Name is required") })
    }

    @Test
    fun `invalid generation fails identity validation`() {
        val updated = character.copy(identity = character.identity.copy(generation = 15))
        val result = validator.validateIdentity(updated)
        assertFalse(result.isValid)
        assertTrue(result.errors.any { it.contains("Generation") })
    }

    @Test
    fun `correct attribute distribution passes`() {
        // Physical: 7 points above base 1, Social: 5, Mental: 3
        val updated = character
            .setAttributeValue(AttributeId.STRENGTH, 4)       // +3
            .setAttributeValue(AttributeId.DEXTERITY, 4)      // +3
            .setAttributeValue(AttributeId.STAMINA, 2)        // +1 = 7 total
            .setAttributeValue(AttributeId.CHARISMA, 3)       // +2
            .setAttributeValue(AttributeId.MANIPULATION, 2)   // +1
            .setAttributeValue(AttributeId.APPEARANCE, 3)     // +2 = 5 total
            .setAttributeValue(AttributeId.PERCEPTION, 2)     // +1
            .setAttributeValue(AttributeId.INTELLIGENCE, 2)   // +1
            .setAttributeValue(AttributeId.WITS, 2)           // +1 = 3 total
        val result = validator.validateAttributes(updated)
        assertTrue(result.isValid)
    }

    @Test
    fun `ability above 3 fails creation validation`() {
        val updated = character.setAbilityValue(AbilityId.ATHLETICS, 4)
        val result = validator.validateAbilities(updated)
        assertFalse(result.isValid)
        assertTrue(result.errors.any { it.contains("exceeds max creation value") })
    }
}
