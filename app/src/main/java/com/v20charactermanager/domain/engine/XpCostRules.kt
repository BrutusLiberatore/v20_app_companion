package com.v20charactermanager.domain.engine

import com.v20charactermanager.domain.definition.ClanId
import com.v20charactermanager.domain.definition.SectId

data class DisciplineXpCost(
    val newDisciplineCost: Int = 7,
    val increaseMultiplier: Int = 5
)

data class XpCostRules(
    val caitiffDisciplineCost: DisciplineXpCost = DisciplineXpCost(
        newDisciplineCost = 10,
        increaseMultiplier = 6
    ),
    val standardDisciplineCost: DisciplineXpCost = DisciplineXpCost(
        newDisciplineCost = 7,
        increaseMultiplier = 5
    ),
    val attributeCost: Int = 5,
    val abilityCost: Int = 2,
    val backgroundCost: Int = 1,
    val virtueCost: Int = 2,
    val humanityCost: Int = 1,
    val willpowerCost: Int = 1,
    val newAbilityCost: Int = 3,
    val newAttributeCost: Int = 5
) {
    fun getDisciplineCost(clan: ClanId): DisciplineXpCost {
        return if (clan == ClanId.CAITIFF) caitiffDisciplineCost else standardDisciplineCost
    }

    fun getNewDisciplineCost(clan: ClanId): Int =
        getDisciplineCost(clan).newDisciplineCost

    fun getDisciplineIncreaseCost(clan: ClanId, currentLevel: Int): Int =
        getDisciplineCost(clan).increaseMultiplier * currentLevel
}

object XpCostCalculator {
    private val rules = XpCostRules()

    fun calculateDisciplineCost(clan: ClanId, currentLevel: Int, isNew: Boolean): Int {
        return if (isNew) {
            rules.getNewDisciplineCost(clan)
        } else {
            rules.getDisciplineIncreaseCost(clan, currentLevel)
        }
    }

    fun calculateAttributeCost(currentValue: Int): Int {
        return rules.attributeCost * (currentValue + 1)
    }

    fun calculateAbilityCost(currentValue: Int, isNew: Boolean): Int {
        return if (isNew) rules.newAbilityCost else rules.abilityCost * (currentValue + 1)
    }

    fun calculateBackgroundCost(currentValue: Int): Int {
        return rules.backgroundCost * (currentValue + 1)
    }

    fun calculateVirtueCost(currentValue: Int): Int {
        return rules.virtueCost * (currentValue + 1)
    }
}
