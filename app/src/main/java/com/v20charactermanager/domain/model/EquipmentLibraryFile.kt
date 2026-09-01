package com.v20charactermanager.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class EquipmentLibraryFile(
    val formatId: String = "v20-equipment-library",
    val schemaVersion: Int = 1,
    val name: String = "",
    val description: String = "",
    val items: List<EquipmentItem> = emptyList()
)
