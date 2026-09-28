package com.v20charactermanager.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class HouseRules(
    val chronicleId: String = "",
    val attributePrimary: Int = 7,
    val attributeSecondary: Int = 5,
    val attributeTertiary: Int = 3,
    val abilityPrimary: Int = 13,
    val abilitySecondary: Int = 9,
    val abilityTertiary: Int = 5,
    val disciplineInitial: Int = 3,
    val backgroundInitial: Int = 5,
    val virtueInitial: Int = 7,
    val freebiePoints: Int = 15,
    val freebieAttributeCost: Int = 5,
    val freebieAbilityCost: Int = 2,
    val freebieDisciplineCost: Int = 7,
    val freebieBackgroundCost: Int = 1,
    val freebieVirtueCost: Int = 2,
    val freebieHumanityCost: Int = 2,
    val freebieWillpowerCost: Int = 1,
    val startingBlood: Int = 10,
    val startingWillpower: Int = 3,
    val difficultyDefault: Int = 6,
    val explodingTensAvailable: Boolean = true,
    val explodingTensDefault: Boolean = false,
    val explodingTensRecursive: Boolean = false,
    val xpAttributeCostPerDot: Int = 4,
    val xpNewAttributeCost: Int = 5,
    val xpAbilityCostPerDot: Int = 2,
    val xpNewAbilityCost: Int = 3,
    val xpBackgroundCostPerDot: Int = 1,
    val xpVirtueCostPerDot: Int = 2,
    val xpHumanityCostPerDot: Int = 2,
    val xpWillpowerCostPerDot: Int = 1,
    val xpDisciplineInClanPerLevel: Int = 5,
    val xpNewDisciplineInClan: Int = 10,
    val xpDisciplineOutOfClanPerLevel: Int = 7,
    val xpNewDisciplineOutOfClan: Int = 10,
    val xpDisciplineCaitiffPerLevel: Int = 6,
    val xpNewDisciplineCaitiff: Int = 10,
    val botchRule: String = "STANDARD",
    val excludedClans: List<String> = emptyList(),
    val customDisciplines: List<String> = emptyList()
) {
    companion object {
        fun defaults(chronicleId: String) = HouseRules(chronicleId = chronicleId)
    }
}
