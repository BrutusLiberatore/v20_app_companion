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
        val pool = buildFromAttributes(character, AttributeId.INTELLIGENCE, ability)
        return DiceEngine.roll(
            pool = pool.totalPool,
            difficulty = difficulty,
            willpowerUsed = willpowerUsed
        )
    }
}
