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
        require(value in 1..5) { "Attribute value must be between 1 and 5" }
    }
}
