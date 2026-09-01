package com.v20charactermanager.domain.model

import kotlinx.serialization.Serializable

enum class SessionEventType {
    SESSION_STARTED,
    SESSION_ENDED,
    SCENE_STARTED,
    SCENE_ENDED,
    SCENE_CHANGED,
    CHARACTER_BLOOD_CHANGED,
    CHARACTER_WILLPOWER_CHANGED,
    CHARACTER_HEALTH_CHANGED,
    NPC_ADDED_TO_SCENE,
    NPC_REMOVED_FROM_SCENE,
    CLUE_REVEALED,
    MEDIA_PRESENTED,
    PLOT_STATUS_CHANGED,
    ROLL_PERFORMED,
    NOTE_CREATED,
    MANUAL_EVENT
}

@Serializable
data class SessionEvent(
    val id: String,
    val chronicleId: String,
    val sessionId: String? = null,
    val sceneId: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val type: SessionEventType,
    val title: String,
    val description: String? = null,
    val entityRefs: List<String> = emptyList(),
    val visibility: Visibility = Visibility.GM_ONLY,
    val metadata: String? = null,
    val origin: String = "MANUAL",
    val createdAt: Long = System.currentTimeMillis()
)
