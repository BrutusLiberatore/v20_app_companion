package com.v20charactermanager.domain.model

import com.v20charactermanager.domain.definition.BackgroundId
import kotlinx.serialization.Serializable

@Serializable
data class BackgroundValue(
    val id: BackgroundId,
    val value: Int = 1,
    val notes: String? = null
) {
    init {
        require(value in 1..5) { "Background value must be between 1 and 5" }
    }
}
