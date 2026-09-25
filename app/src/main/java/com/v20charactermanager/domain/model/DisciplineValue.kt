package com.v20charactermanager.domain.model

import com.v20charactermanager.domain.definition.DisciplineId
import kotlinx.serialization.Serializable

@Serializable
data class DisciplineValue(
    val id: DisciplineId,
    val value: Int = 1
) {
    init {
        // Up to 10: the highest generation max trait (elders, XP-trained).
        require(value in 1..10) { "Discipline value must be between 1 and 10" }
    }
}
