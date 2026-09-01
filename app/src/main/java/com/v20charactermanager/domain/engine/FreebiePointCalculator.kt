package com.v20charactermanager.domain.engine

import com.v20charactermanager.domain.definition.*
import com.v20charactermanager.domain.model.Character

class FreebiePointCalculator(
    private val freebieCost: RuleSet.FreebieCost = RuleSet.defaultFreebieCost
) {
    data class FreebieReport(
        val initialPoints: Int,
        val usedPoints: Int,
        val remainingPoints: Int,
        val nextAttributeCost: Int,
        val nextAbilityCost: Int,
        val nextDisciplineCost: Int,
        val nextBackgroundCost: Int,
        val nextVirtueCost: Int,
        val nextHumanityCost: Int,
        val nextWillpowerCost: Int
    )

    fun calculate(character: Character): FreebieReport {
        val used = calculateUsedPoints(character)
        val initial = RuleSet.FREEBIE_POINTS
        return FreebieReport(
            initialPoints = initial,
            usedPoints = used,
            remainingPoints = initial - used,
            nextAttributeCost = freebieCost.attributeCost,
            nextAbilityCost = freebieCost.abilityCost,
            nextDisciplineCost = freebieCost.disciplineCost,
            nextBackgroundCost = freebieCost.backgroundCost,
            nextVirtueCost = freebieCost.virtueCost,
            nextHumanityCost = freebieCost.humanityCost,
            nextWillpowerCost = freebieCost.willpowerCost
        )
    }

    fun calculateUsedPoints(character: Character): Int {
        var total = 0

        // Attribute points above base (1)
        total += character.attributes.sumOf { attr ->
            if (attr.value > RuleSet.ATTRIBUTE_BASE) {
                (attr.value - RuleSet.ATTRIBUTE_BASE) * freebieCost.attributeCost
            } else 0
        }

        // Ability points above base (0)
        total += character.abilities.sumOf { abil ->
            if (abil.value > RuleSet.ABILITY_BASE) {
                (abil.value - RuleSet.ABILITY_BASE) * freebieCost.abilityCost
            } else 0
        }

        // Discipline points
        total += character.disciplines.sumOf { disc ->
            disc.value * freebieCost.disciplineCost
        }

        // Background points
        total += character.backgrounds.sumOf { bg ->
            bg.value * freebieCost.backgroundCost
        }

        // Virtue points above base (1)
        total += character.virtues.sumOf { virt ->
            if (virt.value > RuleSet.VIRTUE_BASE) {
                (virt.value - RuleSet.VIRTUE_BASE) * freebieCost.virtueCost
            } else 0
        }

        return total
    }

    fun canAfford(character: Character, category: String, value: Int = 1): Boolean {
        val report = calculate(character)
        val cost = when (category) {
            "attribute" -> freebieCost.attributeCost * value
            "ability" -> freebieCost.abilityCost * value
            "discipline" -> freebieCost.disciplineCost * value
            "background" -> freebieCost.backgroundCost * value
            "virtue" -> freebieCost.virtueCost * value
            "humanity" -> freebieCost.humanityCost * value
            "willpower" -> freebieCost.willpowerCost * value
            else -> return false
        }
        return report.remainingPoints >= cost
    }

    fun getRemainingAfterPurchase(character: Character, category: String, value: Int = 1): Int {
        val report = calculate(character)
        val cost = when (category) {
            "attribute" -> freebieCost.attributeCost * value
            "ability" -> freebieCost.abilityCost * value
            "discipline" -> freebieCost.disciplineCost * value
            "background" -> freebieCost.backgroundCost * value
            "virtue" -> freebieCost.virtueCost * value
            "humanity" -> freebieCost.humanityCost * value
            "willpower" -> freebieCost.willpowerCost * value
            else -> return report.remainingPoints
        }
        return report.remainingPoints - cost
    }
}
