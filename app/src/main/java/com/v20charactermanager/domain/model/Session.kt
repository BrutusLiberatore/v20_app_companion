package com.v20charactermanager.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Session(
    val id: String,
    val chronicleId: String,
    val number: Int,
    val title: String = "",
    val date: Long = System.currentTimeMillis(),
    val status: SessionStatus = SessionStatus.PLANNED,
    val realStartDateTime: Long? = null,
    val realEndDateTime: Long? = null,
    val inGameDate: String? = null,
    val activeSceneId: String? = null,
    val plannedSceneIds: List<String> = emptyList(),
    val participantCharacterIds: List<String> = emptyList(),
    val preparationNotes: String = "",
    val liveNotes: String = "",
    val recap: String = "",
    val xpAwarded: Int = 0,
    val unresolvedThreadIds: List<String> = emptyList(),
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
