package com.v20charactermanager.domain.model

import com.v20charactermanager.domain.definition.AbilityId
import kotlinx.serialization.Serializable

@Serializable
data class AbilityValue(
    val id: AbilityId,
    val value: Int = 0,
    val specialty: String? = null
) {
    init {
        require(value in 0..5) { "Ability value must be between 0 and 5" }
    }
}
