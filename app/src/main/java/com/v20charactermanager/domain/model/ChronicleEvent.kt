package com.v20charactermanager.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ChronicleEvent(
    val id: String,
    val chronicleId: String,
    val sessionId: String? = null,
    val sceneId: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val inGameTime: String? = null,
    val typeId: String = "GENERAL",
    val title: String,
    val description: String? = null,
    val involvedEntityIds: List<String> = emptyList(),
    val consequenceNotes: List<String> = emptyList(),
    val visibility: Visibility = Visibility.GM_ONLY,
    val imagePath: String? = null,
    val mediaAssetIds: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
)