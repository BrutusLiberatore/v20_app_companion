package com.v20charactermanager.domain.definition

enum class AttributeCategory {
    PHYSICAL,
    SOCIAL,
    MENTAL
}

enum class AttributeId(
    val id: String,
    val category: AttributeCategory,
    val nameIt: String,
    val nameEn: String
) {
    STRENGTH("attribute.strength", AttributeCategory.PHYSICAL, "Forza", "Strength"),
    DEXTERITY("attribute.dexterity", AttributeCategory.PHYSICAL, "Destrezza", "Dexterity"),
    STAMINA("attribute.stamina", AttributeCategory.PHYSICAL, "Costituzione", "Stamina"),
    CHARISMA("attribute.charisma", AttributeCategory.SOCIAL, "Carisma", "Charisma"),
    MANIPULATION("attribute.manipulation", AttributeCategory.SOCIAL, "Persuasione", "Manipulation"),
    APPEARANCE("attribute.appearance", AttributeCategory.SOCIAL, "Aspetto", "Appearance"),
    PERCEPTION("attribute.perception", AttributeCategory.MENTAL, "Percezione", "Perception"),
    INTELLIGENCE("attribute.intelligence", AttributeCategory.MENTAL, "Intelligenza", "Intelligence"),
    WITS("attribute.wits", AttributeCategory.MENTAL, "Prontezza", "Wits");

    companion object {
        fun byCategory(category: AttributeCategory): List<AttributeId> =
            entries.filter { it.category == category }

        fun fromId(id: String): AttributeId? =
            entries.find { it.id == id }
    }
}
