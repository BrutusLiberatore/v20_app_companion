package com.v20charactermanager.domain.model

import kotlinx.serialization.Serializable

enum class SessionPhase {
    PREPARATION, LIVE, CLOSING
}

enum class SessionStatus {
    PLANNED, ACTIVE, COMPLETED, ARCHIVED
}

@Serializable
data class ChronicleSessionExtended(
    val id: String,
    val chronicleId: String,
    val number: Int? = null,
    val title: String? = null,
    val realDate: Long? = null,
    val inGameDate: String? = null,
    val plannedSceneIds: List<String> = emptyList(),
    val activeSceneId: String? = null,
    val preparationNotes: String? = null,
    val liveNotes: String? = null,
    val recap: String? = null,
    val xpAwarded: Int? = null,
    val importantEventIds: List<String> = emptyList(),
    val unresolvedThreadIds: List<String> = emptyList(),
    val rollLogRefs: List<String> = emptyList(),
    val status: SessionStatus = SessionStatus.PLANNED,
    val phase: SessionPhase = SessionPhase.PREPARATION,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)