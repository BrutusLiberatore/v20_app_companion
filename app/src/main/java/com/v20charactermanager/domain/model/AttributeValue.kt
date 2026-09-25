package com.v20charactermanager.domain.model

import com.v20charactermanager.domain.definition.AttributeId
import kotlinx.serialization.Serializable

@Serializable
data class AttributeValue(
    val id: AttributeId,
    val value: Int = 1,
    val specialty: String? = null
) {
    init {
        // Nosferatu have Appearance 0 (manual); every other attribute starts at 1.
        // Upper bound 10 = the highest generation max trait (elders, XP-trained).
        val min = if (id == AttributeId.APPEARANCE) 0 else 1
        require(value in min..10) { "Attribute value must be between $min and 10" }
    }
}
