package com.v20charactermanager.domain.model

import kotlinx.serialization.Serializable

enum class LocationStatus {
    ACTIVE, INACTIVE, DESTROYED, UNKNOWN
}

@Serializable
data class ChronicleLocation(
    val id: String,
    val chronicleId: String,
    val name: String,
    val typeId: String = "Generic Location",
    val description: String = "",
    val districtOrArea: String = "",
    val controllerEntityId: String? = null,
    val factionId: String? = null,
    val linkedNpcIds: List<String> = emptyList(),
    val linkedPlotIds: List<String> = emptyList(),
    val mediaAssetIds: List<String> = emptyList(),
    val narratorNotes: String = "",
    val imagePath: String? = null,
    val status: LocationStatus = LocationStatus.ACTIVE,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
