package com.v20charactermanager.domain.engine

import com.v20charactermanager.domain.definition.*
import com.v20charactermanager.domain.model.Character

object RuleEngine {

    fun calculateDerivedValues(character: Character): DerivedValues {
        val humanity = HumanityCalculator.calculate(character)
        val willpowerPermanent = WillpowerCalculator.calculatePermanent(character)
        val bloodPoolMax = GenerationRules.getBloodPoolMax(character.identity.generation)
        val bloodPerTurn = GenerationRules.getBloodPerTurn(character.identity.generation)
        val maxTrait = GenerationRules.getMaxTrait(character.identity.generation)

        return DerivedValues(
            humanity = humanity,
            willpowerPermanent = willpowerPermanent,
            bloodPoolMax = bloodPoolMax,
            bloodPerTurn = bloodPerTurn,
            maxTrait = maxTrait,
            healthPenalty = character.health.totalPenalty()
        )
    }

    data class DerivedValues(
        val humanity: Int,
        val willpowerPermanent: Int,
        val bloodPoolMax: Int,
        val bloodPerTurn: Int,
        val maxTrait: Int,
        val healthPenalty: Int
    )

    fun validateAttributeForGeneration(character: Character, attribute: AttributeId, value: Int): Boolean {
        val max = GenerationRules.getMaxTrait(character.identity.generation)
        return value in 1..max
    }

    fun validateAbilityForCreation(character: Character, ability: AbilityId, value: Int): Boolean {
        return value in 0..RuleSet.ABILITY_MAX_CREATION
    }

    fun validateDisciplineForGeneration(character: Character, discipline: DisciplineId, value: Int): Boolean {
        val max = GenerationRules.getMaxDisciplineLevel(character.identity.generation)
        return value in 1..max
    }

    fun validateBackgroundForGeneration(character: Character, background: BackgroundId, value: Int): Boolean {
        val max = GenerationRules.getMaxBackgroundLevel(character.identity.generation)
        return value in 1..max
    }

    fun getSoakDifficulty(): Int = RuleSet.DIFFICULTY_STANDARD

    fun calculateSoak(character: Character): Int {
        return character.getAttributeValue(AttributeId.STAMINA)
    }

    fun calculateInitiative(character: Character): Int {
        val dexterity = character.getAttributeValue(AttributeId.DEXTERITY)
        val wits = character.getAttributeValue(AttributeId.WITS)
        return dexterity + wits
    }
}
