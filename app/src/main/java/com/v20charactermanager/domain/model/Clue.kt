package com.v20charactermanager.domain.model

import kotlinx.serialization.Serializable

enum class ClueStatus {
    UNKNOWN, DISCOVERED, SHARED, ARCHIVED
}

@Serializable
data class Clue(
    val id: String,
    val chronicleId: String,
    val title: String,
    val content: String? = null,
    val mediaAssetId: String? = null,
    val linkedSecretIds: List<String> = emptyList(),
    val status: ClueStatus = ClueStatus.UNKNOWN,
    val discoveredAtEventId: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)