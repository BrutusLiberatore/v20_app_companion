package com.v20charactermanager.domain.engine

import com.v20charactermanager.domain.definition.RuleSet
import com.v20charactermanager.domain.definition.SectId

data class CreationProfile(
    val disciplinePoints: Int,
    val backgroundPoints: Int = RuleSet.BACKGROUND_INITIAL,
    val virtuePoints: Int,
    val freebiePoints: Int = RuleSet.FREEBIE_POINTS,
    val attributePrimary: Int = RuleSet.ATTRIBUTE_PRIMARY,
    val attributeSecondary: Int = RuleSet.ATTRIBUTE_SECONDARY,
    val attributeTertiary: Int = RuleSet.ATTRIBUTE_TERTIARY,
    val abilityPrimary: Int = RuleSet.ABILITY_PRIMARY,
    val abilitySecondary: Int = RuleSet.ABILITY_SECONDARY,
    val abilityTertiary: Int = RuleSet.ABILITY_TERTIARY
) {
    val totalAttributePoints: Int
        get() = attributePrimary + attributeSecondary + attributeTertiary

    val totalAbilityPoints: Int
        get() = abilityPrimary + abilitySecondary + abilityTertiary

    companion object {
        fun forSect(sect: SectId): CreationProfile {
            return when (sect) {
                SectId.SABBAT -> CreationProfile(
                    disciplinePoints = 4,
                    backgroundPoints = RuleSet.BACKGROUND_INITIAL,
                    virtuePoints = 5
                )
                SectId.CAMARILLA,
                SectId.ANARCH,
                SectId.INDEPENDENT -> CreationProfile(
                    disciplinePoints = 3,
                    backgroundPoints = RuleSet.BACKGROUND_INITIAL,
                    virtuePoints = 7
                )
            }
        }
    }
}
