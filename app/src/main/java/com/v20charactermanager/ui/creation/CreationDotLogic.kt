package com.v20charactermanager.ui.creation

import com.v20charactermanager.domain.definition.AttributeId
import com.v20charactermanager.domain.definition.RuleSet
import com.v20charactermanager.domain.model.AbilityValue
import com.v20charactermanager.domain.model.AttributeValue

/**
 * Regole dei pallini usati nella creazione del personaggio.
 * Un pallino corrisponde a UN punto: il pallino N imposta il valore N.
 */
object CreationDotRules {
    const val ATTRIBUTE_MAX_DOTS = RuleSet.ATTRIBUTE_MAX
    const val ABILITY_MAX_DOTS = RuleSet.ABILITY_MAX_CREATION
    const val TRAIT_MAX_DOTS = RuleSet.VIRTUE_MAX

    const val ATTRIBUTE_MIN_VALUE = RuleSet.ATTRIBUTE_BASE
    const val ABILITY_MIN_VALUE = RuleSet.ABILITY_BASE
    const val TRAIT_MIN_VALUE = RuleSet.VIRTUE_BASE

    /**
     * Valore risultante dal tap sul pallino [dot].
     * - tap su un pallino diverso da quello attuale -> imposta il valore assoluto
     * - tap sul pallino gia' pieno -> scala il valore di 1 (fino al minimo)
     */
    fun nextValue(current: Int, dot: Int, minValue: Int): Int = when {
        dot == current && current > minValue -> current - 1
        dot == current -> current
        else -> dot
    }

    fun attributePointsSpent(attributes: List<AttributeValue>, nosferatu: Boolean): Int =
        attributes.sumOf { attribute ->
            val base = if (nosferatu && attribute.id == AttributeId.APPEARANCE) {
                0
            } else {
                RuleSet.ATTRIBUTE_BASE
            }
            attribute.value - base
        }

    fun abilityPointsSpent(abilities: List<AbilityValue>): Int =
        abilities.sumOf { it.value - RuleSet.ABILITY_BASE }
}
