package com.v20charactermanager.domain.model

import com.v20charactermanager.domain.definition.DisciplineId
import kotlinx.serialization.Serializable

@Serializable
data class DisciplineValue(
    val id: DisciplineId,
    val value: Int = 1
) {
    init {
        require(value in 1..5) { "Discipline value must be between 1 and 5" }
    }
}
