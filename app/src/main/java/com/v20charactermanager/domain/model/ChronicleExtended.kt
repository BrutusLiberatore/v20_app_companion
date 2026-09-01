package com.v20charactermanager.domain.model

import kotlinx.serialization.Serializable

enum class ChronicleStatus {
    DRAFT, ACTIVE, PAUSED, COMPLETED, ARCHIVED
}

@Serializable
data class ChronicleConcept(
    val premise: String = "",
    val location: String = "",
    val timePeriod: String = "",
    val primarySectId: String = "",
    val themes: List<String> = emptyList(),
    val mood: List<String> = emptyList(),
    val startingSituation: String = "",
    val expectedScale: String = ""
)

@Serializable
data class ChronicleExtended(
    val id: String,
    val name: String,
    val description: String = "",
    val storytellerName: String = "",
    val userRole: ChronicleUserRole = ChronicleUserRole.PLAYER,
    val status: ChronicleStatus = ChronicleStatus.DRAFT,
    val concept: ChronicleConcept = ChronicleConcept(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
