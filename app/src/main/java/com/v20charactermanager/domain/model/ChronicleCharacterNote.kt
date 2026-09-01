package com.v20charactermanager.domain.model

import kotlinx.serialization.Serializable

enum class NoteVisibility {
    PRIVATE_STORYTELLER
}

@Serializable
data class ChronicleCharacterNote(
    val id: String,
    val chronicleId: String,
    val characterId: String,
    val text: String = "",
    val visibility: NoteVisibility = NoteVisibility.PRIVATE_STORYTELLER,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
