package com.v20charactermanager.domain.model

import kotlinx.serialization.Serializable

enum class NpcType {
    QUICK, FULL
}

enum class NpcStatus {
    ACTIVE, INACTIVE, DEAD, UNKNOWN, ARCHIVED
}

enum class CreatureType {
    MORTAL, VAMPIRE, GHOUL, OTHER
}

@Serializable
data class NpcEntry(
    val id: String,
    val chronicleId: String,
    val name: String,
    val portraitAssetId: String? = null,
    val creatureType: CreatureType = CreatureType.MORTAL,
    val clanId: String? = null,
    val sectId: String? = null,
    val role: String = "",
    val description: String = "",
    val personality: String = "",
    val motivation: String = "",
    val narratorNotes: String = "",
    val imagePath: String? = null,
    val status: NpcStatus = NpcStatus.ACTIVE,
    val type: NpcType = NpcType.QUICK,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
