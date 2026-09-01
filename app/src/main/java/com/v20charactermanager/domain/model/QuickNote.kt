package com.v20charactermanager.domain.model

import kotlinx.serialization.Serializable

enum class NoteScope {
    CHRONICLE, SESSION, SCENE, ENTITY, QUICK
}

@Serializable
data class QuickNote(
    val id: String,
    val chronicleId: String,
    val scopeType: NoteScope = NoteScope.QUICK,
    val scopeId: String? = null,
    val text: String,
    val visibility: Visibility = Visibility.GM_ONLY,
    val createdAt: Long = System.currentTimeMillis(),
    val modifiedAt: Long = System.currentTimeMillis()
)
