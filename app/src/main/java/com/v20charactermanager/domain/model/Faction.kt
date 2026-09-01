package com.v20charactermanager.domain.model

import kotlinx.serialization.Serializable

enum class FactionStatus {
    ACTIVE, INACTIVE, DISSOLVED, ARCHIVED
}

@Serializable
data class Faction(
    val id: String,
    val chronicleId: String,
    val name: String,
    val typeId: String? = null,
    val sectId: String? = null,
    val description: String = "",
    val leaderEntityId: String? = null,
    val memberIds: List<String> = emptyList(),
    val objectives: List<String> = emptyList(),
    val allyFactionIds: List<String> = emptyList(),
    val enemyFactionIds: List<String> = emptyList(),
    val locationIds: List<String> = emptyList(),
    val narratorNotes: String = "",
    val imagePath: String? = null,
    val status: FactionStatus = FactionStatus.ACTIVE,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)