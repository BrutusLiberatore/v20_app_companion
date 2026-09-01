package com.v20charactermanager.domain.definition

object RuleSet {
    // Creation point distributions
    const val ATTRIBUTE_PRIMARY = 7
    const val ATTRIBUTE_SECONDARY = 5
    const val ATTRIBUTE_TERTIARY = 3

    const val ABILITY_PRIMARY = 13
    const val ABILITY_SECONDARY = 9
    const val ABILITY_TERTIARY = 5

    const val DISCIPLINE_INITIAL = 3
    const val BACKGROUND_INITIAL = 5
    const val VIRTUE_INITIAL = 7

    const val FREEBIE_POINTS = 15

    // Base values
    const val ATTRIBUTE_BASE = 1
    const val ABILITY_BASE = 0
    const val VIRTUE_BASE = 1

    // Max values during creation
    const val ABILITY_MAX_CREATION = 3
    const val ATTRIBUTE_MAX = 5
    const val ABILITY_MAX = 5
    const val DISCIPLINE_MAX = 5
    const val BACKGROUND_MAX = 5
    const val VIRTUE_MAX = 5

    // Freebie costs (configurable)
    data class FreebieCost(
        val attributeCost: Int = 5,
        val abilityCost: Int = 2,
        val disciplineCost: Int = 7,
        val backgroundCost: Int = 1,
        val virtueCost: Int = 2,
        val humanityCost: Int = 1,
        val willpowerCost: Int = 1
    )

    val defaultFreebieCost = FreebieCost()

    // Health levels
    val healthLevels = listOf(
        HealthLevel.HURT,
        HealthLevel.GRAZED,
        HealthLevel.INJURED,
        HealthLevel.WOUNDED,
        HealthLevel.MAULED,
        HealthLevel.CRIPPLED,
        HealthLevel.INCAPACITATED
    )

    // Difficulty scale
    const val DIFFICULTY_BANAL = 2
    const val DIFFICULTY_EASY = 4
    const val DIFFICULTY_STANDARD = 6
    const val DIFFICULTY_HARD = 8
    const val DIFFICULTY_NEAR_IMPOSSIBLE = 10
}
