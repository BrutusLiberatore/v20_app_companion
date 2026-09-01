package com.v20charactermanager.domain.model

import kotlinx.serialization.Serializable

enum class SceneStatus {
    PLANNED, READY, ACTIVE, COMPLETED, SKIPPED
}

@Serializable
data class ChronicleScene(
    val id: String,
    val chronicleId: String,
    val storyId: String? = null,
    val sessionId: String? = null,
    val title: String,
    val locationId: String? = null,
    val participantIds: List<String> = emptyList(),
    val hook: String? = null,
    val objective: String? = null,
    val conflict: String? = null,
    val mood: String? = null,
    val description: String? = null,
    val clueIds: List<String> = emptyList(),
    val secretIds: List<String> = emptyList(),
    val possibleComplications: List<String> = emptyList(),
    val mediaAssetIds: List<String> = emptyList(),
    val outcome: String? = null,
    val status: SceneStatus = SceneStatus.PLANNED,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)