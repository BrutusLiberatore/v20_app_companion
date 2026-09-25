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
        val initial = RuleSet.FREEBIE_CREATION_POINTS
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

    /**
     * Creation allocations (7/5/3 attributes, 13/9/5 abilities, clan discipline dots,
     * 5 backgrounds, 7 virtue points) are included in the creation steps and are free.
     * Only spending beyond those totals counts, plus Merits (paid) and Flaws (refunded),
     * as per the V20 manual.
     */
    fun calculateUsedPoints(character: Character): Int {
        var total = 0
        val profile = CreationProfile.forSect(character.identity.sect)
        val isNosferatu = character.identity.clan == ClanId.NOSFERATU

        val expectedAttributes = RuleSet.ATTRIBUTE_PRIMARY + RuleSet.ATTRIBUTE_SECONDARY + RuleSet.ATTRIBUTE_TERTIARY
        val attributePoints = character.attributes.sumOf { attr ->
            val base = if (isNosferatu && attr.id == AttributeId.APPEARANCE) 0 else RuleSet.ATTRIBUTE_BASE
            (attr.value - base).coerceAtLeast(0)
        }
        total += (attributePoints - expectedAttributes).coerceAtLeast(0) * freebieCost.attributeCost

        val expectedAbilities = RuleSet.ABILITY_PRIMARY + RuleSet.ABILITY_SECONDARY + RuleSet.ABILITY_TERTIARY
        val abilityPoints = character.abilities.sumOf { (it.value - RuleSet.ABILITY_BASE).coerceAtLeast(0) }
        total += (abilityPoints - expectedAbilities).coerceAtLeast(0) * freebieCost.abilityCost

        total += (character.disciplines.sumOf { it.value } - profile.disciplinePoints)
            .coerceAtLeast(0) * freebieCost.disciplineCost

        total += (character.backgrounds.sumOf { it.value } - RuleSet.BACKGROUND_INITIAL)
            .coerceAtLeast(0) * freebieCost.backgroundCost

        val virtuePoints = character.virtues.sumOf { it.value }
        total += (virtuePoints - profile.virtuePoints).coerceAtLeast(0) * freebieCost.virtueCost

        total += character.merits.sumOf { it.cost }
        total -= character.flaws.sumOf { it.value }

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
