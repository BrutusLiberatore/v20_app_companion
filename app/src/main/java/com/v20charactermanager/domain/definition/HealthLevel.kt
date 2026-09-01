package com.v20charactermanager.domain.definition

enum class HealthLevel(
    val index: Int,
    val nameIt: String,
    val nameEn: String,
    val penalty: Int
) {
    HURT(0, "Contuso", "Hurt", 0),
    GRAZED(1, "Graffiato", "Grazed", -1),
    INJURED(2, "Leso", "Injured", -1),
    WOUNDED(3, "Ferito", "Wounded", -2),
    MAULED(4, "Straziato", "Mauled", -2),
    CRIPPLED(5, "Menomato", "Crippled", -5),
    INCAPACITATED(6, "Incapacitato", "Incapacitated", 0);

    companion object {
        fun fromIndex(index: Int): HealthLevel? =
            entries.find { it.index == index }
    }
}

enum class DamageType {
    NONE,
    BASHING,
    LETHAL,
    AGGRAVATED
}
