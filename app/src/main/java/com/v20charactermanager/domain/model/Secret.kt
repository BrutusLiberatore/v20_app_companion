package com.v20charactermanager.domain.model

import kotlinx.serialization.Serializable

enum class SecretStatus {
    HIDDEN, PARTIALLY_REVEALED, REVEALED, ARCHIVED
}

@Serializable
data class Secret(
    val id: String,
    val chronicleId: String,
    val title: String,
    val content: String = "",
    val linkedEntityIds: List<String> = emptyList(),
    val visibility: Visibility = Visibility.GM_ONLY,
    val status: SecretStatus = SecretStatus.HIDDEN,
    val revealedAtEventId: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)