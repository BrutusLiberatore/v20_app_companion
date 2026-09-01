package com.v20charactermanager.domain.model

import kotlinx.serialization.Serializable

enum class EquipmentCategory {
    WEAPON,
    ARMOR,
    CLOTHING,
    VEHICLE,
    TOOL,
    ELECTRONICS,
    DOCUMENT,
    MISCELLANEOUS
}

@Serializable
data class EquipmentItem(
    val id: String,
    val name: String,
    val description: String = "",
    val quantity: Int = 1,
    val category: EquipmentCategory = EquipmentCategory.MISCELLANEOUS,
    val damage: String = "",
    val size: Int = 0,
    val weight: Double = 0.0,
    val cost: String = "",
    val notes: String = ""
)
