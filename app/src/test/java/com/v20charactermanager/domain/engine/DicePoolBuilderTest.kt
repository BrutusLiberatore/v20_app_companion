package com.v20charactermanager.domain.engine

import com.v20charactermanager.domain.definition.AttributeId
import com.v20charactermanager.domain.definition.AbilityId
import com.v20charactermanager.domain.model.Character
import org.junit.Assert.*
import org.junit.Test

class DicePoolBuilderTest {

    private fun testCharacter(
        strength: Int = 1, dexterity: Int = 1, stamina: Int = 1,
        charisma: Int = 1, manipulation: Int = 1, appearance: Int = 1,
        perception: Int = 1, intelligence: Int = 1, wits: Int = 1,
        athletics: Int = 0, melee: Int = 0, firearms: Int = 0,
        stealth: Int = 0, investigation: Int = 0, academics: Int = 0, empathy: Int = 0,
        intimidate: Int = 0, streetwise: Int = 0, subterfuge: Int = 0,
        leadership: Int = 0, expression: Int = 0
    ): Character {
        var c = Character(id = "test")
        c = c.setAttributeValue(AttributeId.STRENGTH, strength)
        c = c.setAttributeValue(AttributeId.DEXTERITY, dexterity)
        c = c.setAttributeValue(AttributeId.STAMINA, stamina)
        c = c.setAttributeValue(AttributeId.CHARISMA, charisma)
        c = c.setAttributeValue(AttributeId.MANIPULATION, manipulation)
        c = c.setAttributeValue(AttributeId.APPEARANCE, appearance)
        c = c.setAttributeValue(AttributeId.PERCEPTION, perception)
        c = c.setAttributeValue(AttributeId.INTELLIGENCE, intelligence)
        c = c.setAttributeValue(AttributeId.WITS, wits)
        c = c.setAbilityValue(AbilityId.ATHLETICS, athletics)
        c = c.setAbilityValue(AbilityId.MELEE, melee)
        c = c.setAbilityValue(AbilityId.FIREARMS, firearms)
        c = c.setAbilityValue(AbilityId.STEALTH, stealth)
        c = c.setAbilityValue(AbilityId.INVESTIGATION, investigation)
        c = c.setAbilityValue(AbilityId.ACADEMICS, academics)
        c = c.setAbilityValue(AbilityId.EMPATHY, empathy)
        c = c.setAbilityValue(AbilityId.INTIMIDATE, intimidate)
        c = c.setAbilityValue(AbilityId.STREETWISE, streetwise)
        c = c.setAbilityValue(AbilityId.SUBTERFUGE, subterfuge)
        c = c.setAbilityValue(AbilityId.LEADERSHIP, leadership)
        c = c.setAbilityValue(AbilityId.EXPRESSION, expression)
        return c
    }

    @Test
    fun `rollAbility with Athletics uses DEXTERITY`() {
        val character = testCharacter(dexterity = 4, athletics = 3)
        val pool = DicePoolBuilder.buildFromAttributes(character, DicePoolBuilder.defaultAttributeForAbility(AbilityId.ATHLETICS), AbilityId.ATHLETICS)
        assertEquals(AttributeId.DEXTERITY, pool.attribute)
        assertEquals(4, pool.attributeValue)
        assertEquals(3, pool.abilityValue)
        assertEquals(7, pool.totalPool)
    }

    @Test
    fun `rollAbility with Investigation uses PERCEPTION`() {
        val character = testCharacter(perception = 3, intelligence = 5, investigation = 2)
        val pool = DicePoolBuilder.buildFromAttributes(character, DicePoolBuilder.defaultAttributeForAbility(AbilityId.INVESTIGATION), AbilityId.INVESTIGATION)
        assertEquals(AttributeId.PERCEPTION, pool.attribute)
        assertEquals(3, pool.attributeValue)
        assertEquals(2, pool.abilityValue)
        assertEquals(5, pool.totalPool)
    }

    @Test
    fun `rollAbility with Academics uses INTELLIGENCE`() {
        val character = testCharacter(intelligence = 5, academics = 3)
        val pool = DicePoolBuilder.buildFromAttributes(character, DicePoolBuilder.defaultAttributeForAbility(AbilityId.ACADEMICS), AbilityId.ACADEMICS)
        assertEquals(AttributeId.INTELLIGENCE, pool.attribute)
        assertEquals(5, pool.attributeValue)
        assertEquals(3, pool.abilityValue)
        assertEquals(8, pool.totalPool)
    }

    @Test
    fun `rollAbility with Melee uses STRENGTH`() {
        val character = testCharacter(strength = 4, melee = 3)
        val pool = DicePoolBuilder.buildFromAttributes(character, DicePoolBuilder.defaultAttributeForAbility(AbilityId.MELEE), AbilityId.MELEE)
        assertEquals(AttributeId.STRENGTH, pool.attribute)
        assertEquals(4, pool.attributeValue)
        assertEquals(3, pool.abilityValue)
        assertEquals(7, pool.totalPool)
    }

    @Test
    fun `rollAbility with Empathy uses WITS`() {
        val character = testCharacter(wits = 3, empathy = 2)
        val pool = DicePoolBuilder.buildFromAttributes(character, DicePoolBuilder.defaultAttributeForAbility(AbilityId.EMPATHY), AbilityId.EMPATHY)
        assertEquals(AttributeId.WITS, pool.attribute)
        assertEquals(3, pool.attributeValue)
        assertEquals(2, pool.abilityValue)
        assertEquals(5, pool.totalPool)
    }

    @Test
    fun `rollAbility with Intimidate uses MANIPULATION`() {
        val character = testCharacter(manipulation = 4, intimidate = 2)
        val pool = DicePoolBuilder.buildFromAttributes(character, DicePoolBuilder.defaultAttributeForAbility(AbilityId.INTIMIDATE), AbilityId.INTIMIDATE)
        assertEquals(AttributeId.MANIPULATION, pool.attribute)
        assertEquals(4, pool.attributeValue)
        assertEquals(2, pool.abilityValue)
        assertEquals(6, pool.totalPool)
    }

    @Test
    fun `rollAbility with Leadership uses CHARISMA`() {
        val character = testCharacter(charisma = 5, leadership = 3)
        val pool = DicePoolBuilder.buildFromAttributes(character, DicePoolBuilder.defaultAttributeForAbility(AbilityId.LEADERSHIP), AbilityId.LEADERSHIP)
        assertEquals(AttributeId.CHARISMA, pool.attribute)
        assertEquals(5, pool.attributeValue)
        assertEquals(3, pool.abilityValue)
        assertEquals(8, pool.totalPool)
    }

    @Test
    fun `rollAbility with Stealth uses DEXTERITY`() {
        val character = testCharacter(dexterity = 4, stealth = 2)
        val pool = DicePoolBuilder.buildFromAttributes(character, DicePoolBuilder.defaultAttributeForAbility(AbilityId.STEALTH), AbilityId.STEALTH)
        assertEquals(AttributeId.DEXTERITY, pool.attribute)
        assertEquals(4, pool.attributeValue)
        assertEquals(2, pool.abilityValue)
        assertEquals(6, pool.totalPool)
    }

    @Test
    fun `rollAbility with Streetwise uses MANIPULATION`() {
        val character = testCharacter(manipulation = 3, streetwise = 2)
        val pool = DicePoolBuilder.buildFromAttributes(character, DicePoolBuilder.defaultAttributeForAbility(AbilityId.STREETWISE), AbilityId.STREETWISE)
        assertEquals(AttributeId.MANIPULATION, pool.attribute)
        assertEquals(3, pool.attributeValue)
        assertEquals(2, pool.abilityValue)
        assertEquals(5, pool.totalPool)
    }

    @Test
    fun `defaultAttributeForAbility returns correct attributes for all abilities`() {
        // Mental / Intelligence
        assertEquals(AttributeId.INTELLIGENCE, DicePoolBuilder.defaultAttributeForAbility(AbilityId.ACADEMICS))
        assertEquals(AttributeId.INTELLIGENCE, DicePoolBuilder.defaultAttributeForAbility(AbilityId.COMPUTER))
        assertEquals(AttributeId.INTELLIGENCE, DicePoolBuilder.defaultAttributeForAbility(AbilityId.FINANCE))
        assertEquals(AttributeId.INTELLIGENCE, DicePoolBuilder.defaultAttributeForAbility(AbilityId.LAW))
        assertEquals(AttributeId.INTELLIGENCE, DicePoolBuilder.defaultAttributeForAbility(AbilityId.MEDICINE))
        assertEquals(AttributeId.INTELLIGENCE, DicePoolBuilder.defaultAttributeForAbility(AbilityId.OCCULT))
        assertEquals(AttributeId.INTELLIGENCE, DicePoolBuilder.defaultAttributeForAbility(AbilityId.POLITICS))
        assertEquals(AttributeId.INTELLIGENCE, DicePoolBuilder.defaultAttributeForAbility(AbilityId.SCIENCE))

        // Mental / Perception
        assertEquals(AttributeId.PERCEPTION, DicePoolBuilder.defaultAttributeForAbility(AbilityId.INVESTIGATION))
        assertEquals(AttributeId.PERCEPTION, DicePoolBuilder.defaultAttributeForAbility(AbilityId.ALERTNESS))

        // Mental / Wits
        assertEquals(AttributeId.WITS, DicePoolBuilder.defaultAttributeForAbility(AbilityId.EMPATHY))
        assertEquals(AttributeId.WITS, DicePoolBuilder.defaultAttributeForAbility(AbilityId.INTUITION))

        // Physical / Strength
        assertEquals(AttributeId.STRENGTH, DicePoolBuilder.defaultAttributeForAbility(AbilityId.MELEE))

        // Physical / Dexterity
        assertEquals(AttributeId.DEXTERITY, DicePoolBuilder.defaultAttributeForAbility(AbilityId.ATHLETICS))
        assertEquals(AttributeId.DEXTERITY, DicePoolBuilder.defaultAttributeForAbility(AbilityId.CRAFTS))
        assertEquals(AttributeId.DEXTERITY, DicePoolBuilder.defaultAttributeForAbility(AbilityId.DRIVE))
        assertEquals(AttributeId.DEXTERITY, DicePoolBuilder.defaultAttributeForAbility(AbilityId.FIREARMS))
        assertEquals(AttributeId.DEXTERITY, DicePoolBuilder.defaultAttributeForAbility(AbilityId.LARCENY))
        assertEquals(AttributeId.DEXTERITY, DicePoolBuilder.defaultAttributeForAbility(AbilityId.SECURITY))
        assertEquals(AttributeId.DEXTERITY, DicePoolBuilder.defaultAttributeForAbility(AbilityId.STEALTH))

        // Physical / Stamina
        assertEquals(AttributeId.STAMINA, DicePoolBuilder.defaultAttributeForAbility(AbilityId.ANIMAL_KEN))
        assertEquals(AttributeId.STAMINA, DicePoolBuilder.defaultAttributeForAbility(AbilityId.SURVIVAL))

        // Social / Charisma
        assertEquals(AttributeId.CHARISMA, DicePoolBuilder.defaultAttributeForAbility(AbilityId.EXPRESSION))
        assertEquals(AttributeId.CHARISMA, DicePoolBuilder.defaultAttributeForAbility(AbilityId.LEADERSHIP))
        assertEquals(AttributeId.CHARISMA, DicePoolBuilder.defaultAttributeForAbility(AbilityId.PERFORMANCE))

        // Social / Manipulation
        assertEquals(AttributeId.MANIPULATION, DicePoolBuilder.defaultAttributeForAbility(AbilityId.INTIMIDATE))
        assertEquals(AttributeId.MANIPULATION, DicePoolBuilder.defaultAttributeForAbility(AbilityId.STREETWISE))
        assertEquals(AttributeId.MANIPULATION, DicePoolBuilder.defaultAttributeForAbility(AbilityId.SUBTERFUGE))
    }
}
