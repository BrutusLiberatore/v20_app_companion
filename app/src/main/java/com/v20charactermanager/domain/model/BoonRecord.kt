package com.v20charactermanager.domain.model

import kotlinx.serialization.Serializable

enum class BoonStatus {
    OPEN, FULFILLED, FORGIVEN, DISPUTED, ARCHIVED
}

@Serializable
data class BoonRecord(
    val id: String,
    val chronicleId: String,
    val creditorEntityId: String,
    val debtorEntityId: String,
    val typeId: String? = null,
    val description: String = "",
    val status: BoonStatus = BoonStatus.OPEN,
    val witnessedBy: List<String> = emptyList(),
    val visibility: Visibility = Visibility.GM_ONLY,
    val narratorNotes: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)