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
    val freebieHumanityCost: Int = 1,
    val freebieWillpowerCost: Int = 1,
    val startingBlood: Int = 10,
    val startingWillpower: Int = 3,
    val difficultyDefault: Int = 6,
    val xpCostAttribute: Map<Int, Int> = mapOf(2 to 2, 3 to 4, 4 to 6, 5 to 8),
    val xpCostAbility: Map<Int, Int> = mapOf(1 to 2, 2 to 4, 3 to 6, 4 to 10, 5 to 15),
    val xpCostDiscipline: Map<Int, Int> = mapOf(1 to 5, 2 to 10, 3 to 15, 4 to 20, 5 to 25),
    val botchRule: String = "STANDARD",
    val excludedClans: List<String> = emptyList(),
    val customDisciplines: List<String> = emptyList()
) {
    companion object {
        fun defaults(chronicleId: String) = HouseRules(chronicleId = chronicleId)
    }
}
