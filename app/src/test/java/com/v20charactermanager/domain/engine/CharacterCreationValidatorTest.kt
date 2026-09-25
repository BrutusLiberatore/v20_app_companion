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

    /** Builds a character that satisfies every creation rule of the manual. */
    private fun completeCharacter(): Character {
        var c = character
            .setAttributeValue(AttributeId.STRENGTH, 4)
            .setAttributeValue(AttributeId.DEXTERITY, 4)
            .setAttributeValue(AttributeId.STAMINA, 2)
            .setAttributeValue(AttributeId.CHARISMA, 3)
            .setAttributeValue(AttributeId.MANIPULATION, 2)
            .setAttributeValue(AttributeId.APPEARANCE, 3)
            .setAttributeValue(AttributeId.PERCEPTION, 2)
            .setAttributeValue(AttributeId.INTELLIGENCE, 2)
            .setAttributeValue(AttributeId.WITS, 2)

        val targets = mapOf(
            AbilityCategory.TALENTS to 13,
            AbilityCategory.SKILLS to 9,
            AbilityCategory.KNOWLEDGES to 5
        )
        targets.forEach { (category, target) ->
            var placed = 0
            val ids = AbilityId.entries.filter { it.category == category }
            var index = 0
            while (placed < target) {
                val id = ids[index % ids.size]
                val current = c.getAbilityValue(id)
                if (current < 3) {
                    c = c.setAbilityValue(id, current + 1)
                    placed++
                }
                index++
                check(index < 500) { "Unable to allocate ability points" }
            }
        }

        c = c.addDiscipline(DisciplineId.PRESENCE, 3)
        c = c.addBackground(BackgroundId.entries.first(), 5)
        c = c.setVirtueValue(VirtueId.CONSCIENCE, 3)
            .setVirtueValue(VirtueId.SELF_CONTROL, 2)
            .setVirtueValue(VirtueId.COURAGE, 2)
        c = c.copy(willpower = WillpowerState(permanent = 2, current = 2))
        return c
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
        val updated = character.copy(identity = character.identity.copy(generation = 16))
        val result = validator.validateIdentity(updated)
        assertFalse(result.isValid)
        assertTrue(result.errors.any { it.contains("Generation") })
    }

    @Test
    fun `thin-blooded generation 14-15 passes identity validation`() {
        listOf(14, 15).forEach { generation ->
            val updated = character.copy(identity = character.identity.copy(generation = generation))
            val result = validator.validateIdentity(updated)
            assertTrue("Generation $generation should be valid", result.isValid)
        }
    }

    @Test
    fun `missing clan required choice fails identity validation`() {
        val updated = character.copy(
            identity = character.identity.copy(clan = ClanId.MALKAVIAN, clanChoices = emptyMap())
        )
        val result = validator.validateIdentity(updated)
        assertFalse(result.isValid)
        assertTrue(result.errors.any { it.contains("Missing required choice") })
    }

    @Test
    fun `clan required choice satisfied passes identity validation`() {
        val updated = character.copy(
            identity = character.identity.copy(
                clan = ClanId.MALKAVIAN,
                clanChoices = mapOf("malkavian_derangement" to "Paranoia")
            )
        )
        val result = validator.validateIdentity(updated)
        assertTrue(result.isValid)
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
        assertTrue(result.warnings.isEmpty())
    }

    @Test
    fun `attribute point mismatch is a warning, not an error`() {
        val updated = character.setAttributeValue(AttributeId.WITS, 3)
        val result = validator.validateAttributes(updated)
        assertTrue(result.isValid)
        assertTrue(result.warnings.any { it.contains("Total attribute points") })
    }

    @Test
    fun `ability above 3 is a warning, not an error`() {
        val updated = character.setAbilityValue(AbilityId.ATHLETICS, 4)
        val result = validator.validateAbilities(updated)
        assertTrue(result.isValid)
        assertTrue(result.warnings.any { it.contains("exceeds max creation value") })
    }

    @Test
    fun `non-clan discipline fails advantages validation`() {
        val updated = completeCharacter().addDiscipline(DisciplineId.OBFUSCATE, 1)
        val result = validator.validateAdvantages(updated)
        assertFalse(result.isValid)
        assertTrue(result.errors.any { it.contains("not a clan discipline") })
    }

    @Test
    fun `discipline point mismatch is a warning, not an error`() {
        val updated = completeCharacter().removeDiscipline(DisciplineId.PRESENCE)
        val result = validator.validateAdvantages(updated)
        assertTrue(result.isValid)
        assertTrue(result.warnings.any { it.contains("Discipline points") })
    }

    @Test
    fun `merits within 7 points pass advantages validation`() {
        val updated = completeCharacter()
            .addMerit(MeritValue(id = "merit.empathy", name = "Empathy", cost = 3, description = ""))
            .addMerit(MeritValue(id = "merit.iron_will", name = "Iron Will", cost = 4, description = ""))
        val result = validator.validateAdvantages(updated)
        assertTrue(result.errors.isEmpty())
    }

    @Test
    fun `merits beyond 7 points fail advantages validation`() {
        val updated = completeCharacter()
            .addMerit(MeritValue(id = "merit.a", name = "A", cost = 4, description = ""))
            .addMerit(MeritValue(id = "merit.b", name = "B", cost = 4, description = ""))
        val result = validator.validateAdvantages(updated)
        assertFalse(result.isValid)
        assertTrue(result.errors.any { it.contains("Merits exceed") })
    }

    @Test
    fun `flaws beyond 7 points fail advantages validation`() {
        val updated = completeCharacter()
            .addFlaw(FlawValue(id = "flaw.a", name = "A", value = 4, description = ""))
            .addFlaw(FlawValue(id = "flaw.b", name = "B", value = 4, description = ""))
        val result = validator.validateAdvantages(updated)
        assertFalse(result.isValid)
        assertTrue(result.errors.any { it.contains("Flaws exceed") })
    }

    @Test
    fun `freebie overspend is a warning, not an error`() {
        var updated = completeCharacter()
        AttributeId.entries.forEach { updated = updated.setAttributeValue(it, 3) }
        val result = validator.validateFinalization(updated)
        assertTrue(result.isValid)
        assertTrue(result.warnings.any { it.contains("Freebie points exceeded") })
    }

    @Test
    fun `complete character passes all steps without warnings`() {
        val c = completeCharacter()
        (1..5).forEach { step ->
            val result = validator.validateStep(c, step)
            assertTrue("Step $step errors: ${result.errors}", result.errors.isEmpty())
            assertTrue("Step $step warnings: ${result.warnings}", result.warnings.isEmpty())
        }
    }

    @Test
    fun `unknown step passes validation`() {
        val result = validator.validateStep(character, 99)
        assertTrue(result.isValid)
    }
}
