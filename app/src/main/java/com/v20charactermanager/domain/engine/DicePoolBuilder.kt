package com.v20charactermanager.domain.engine

import com.v20charactermanager.domain.definition.AttributeId
import com.v20charactermanager.domain.definition.AbilityId
import com.v20charactermanager.domain.definition.RuleSet
import com.v20charactermanager.domain.model.Character

object DicePoolBuilder {

    data class DicePool(
        val attribute: AttributeId,
        val attributeValue: Int,
        val ability: AbilityId?,
        val abilityValue: Int,
        val totalPool: Int,
        val specialtyBonus: Int = 0
    )

    fun defaultAttributeForAbility(ability: AbilityId): AttributeId = when (ability) {
        // Talents
        AbilityId.ATHLETICS -> AttributeId.DEXTERITY
        AbilityId.ALERTNESS -> AttributeId.PERCEPTION
        AbilityId.EMPATHY -> AttributeId.WITS
        AbilityId.EXPRESSION -> AttributeId.CHARISMA
        AbilityId.INTIMIDATE -> AttributeId.MANIPULATION
        AbilityId.INTUITION -> AttributeId.WITS
        AbilityId.LEADERSHIP -> AttributeId.CHARISMA
        AbilityId.STREETWISE -> AttributeId.MANIPULATION
        AbilityId.SUBTERFUGE -> AttributeId.MANIPULATION

        // Skills
        AbilityId.ANIMAL_KEN -> AttributeId.STAMINA
        AbilityId.CRAFTS -> AttributeId.DEXTERITY
        AbilityId.DRIVE -> AttributeId.DEXTERITY
        AbilityId.FIREARMS -> AttributeId.DEXTERITY
        AbilityId.LARCENY -> AttributeId.DEXTERITY
        AbilityId.MELEE -> AttributeId.STRENGTH
        AbilityId.PERFORMANCE -> AttributeId.CHARISMA
        AbilityId.SECURITY -> AttributeId.DEXTERITY
        AbilityId.STEALTH -> AttributeId.DEXTERITY
        AbilityId.SURVIVAL -> AttributeId.STAMINA

        // Knowledges
        AbilityId.ACADEMICS -> AttributeId.INTELLIGENCE
        AbilityId.COMPUTER -> AttributeId.INTELLIGENCE
        AbilityId.FINANCE -> AttributeId.INTELLIGENCE
        AbilityId.INVESTIGATION -> AttributeId.PERCEPTION
        AbilityId.LAW -> AttributeId.INTELLIGENCE
        AbilityId.MEDICINE -> AttributeId.INTELLIGENCE
        AbilityId.OCCULT -> AttributeId.INTELLIGENCE
        AbilityId.POLITICS -> AttributeId.INTELLIGENCE
        AbilityId.SCIENCE -> AttributeId.INTELLIGENCE
    }

    fun buildFromAttributes(
        character: Character,
        attribute: AttributeId,
        ability: AbilityId? = null
    ): DicePool {
        val attributeValue = character.getAttributeValue(attribute)
        val abilityValue = if (ability != null) character.getAbilityValue(ability) else 0
        val totalPool = attributeValue + abilityValue

        return DicePool(
            attribute = attribute,
            attributeValue = attributeValue,
            ability = ability,
            abilityValue = abilityValue,
            totalPool = totalPool
        )
    }

    fun roll(
        character: Character,
        attribute: AttributeId,
        ability: AbilityId? = null,
        difficulty: Int = RuleSet.DIFFICULTY_STANDARD,
        extraDice: Int = 0,
        willpowerUsed: Boolean = false
    ): DiceResult {
        val pool = buildFromAttributes(character, attribute, ability)
        return DiceEngine.roll(
            pool = pool.totalPool,
            difficulty = difficulty,
            extraDice = extraDice,
            willpowerUsed = willpowerUsed
        )
    }

    fun rollAttribute(
        character: Character,
        attribute: AttributeId,
        difficulty: Int = RuleSet.DIFFICULTY_STANDARD,
        willpowerUsed: Boolean = false
    ): DiceResult {
        val pool = buildFromAttributes(character, attribute)
        return DiceEngine.roll(
            pool = pool.totalPool,
            difficulty = difficulty,
            willpowerUsed = willpowerUsed
        )
    }

    fun rollAbility(
        character: Character,
        ability: AbilityId,
        difficulty: Int = RuleSet.DIFFICULTY_STANDARD,
        willpowerUsed: Boolean = false
    ): DiceResult {
        val attribute = defaultAttributeForAbility(ability)
        val pool = buildFromAttributes(character, attribute, ability)
        return DiceEngine.roll(
            pool = pool.totalPool,
            difficulty = difficulty,
            willpowerUsed = willpowerUsed
        )
    }
}
