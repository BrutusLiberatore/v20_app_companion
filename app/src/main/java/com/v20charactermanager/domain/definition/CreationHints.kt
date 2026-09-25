package com.v20charactermanager.domain.definition

import kotlinx.serialization.Serializable

@Serializable
data class CreationHints(
    val recommendedAttributes: List<AttributeCategory> = emptyList(),
    val recommendedAbilityCategories: List<AbilityCategory> = emptyList(),
    val noteIt: String = "",
    val noteEn: String = ""
) {
    val hasHints: Boolean get() = recommendedAttributes.isNotEmpty() || recommendedAbilityCategories.isNotEmpty()
}
