package com.v20charactermanager.domain.model

import com.v20charactermanager.domain.definition.VirtueId
import kotlinx.serialization.Serializable

@Serializable
data class VirtueValue(
    val id: VirtueId,
    val value: Int = 1
) {
    init {
        require(value in 1..5) { "Virtue value must be between 1 and 5" }
    }
}
