package com.v20charactermanager.domain.model

import kotlinx.serialization.Serializable

enum class ChronicleUserRole {
    PLAYER,
    STORYTELLER
}

enum class ChronicleMemberRole {
    PLAYER_CHARACTER,
    NPC
}

@Serializable
data class Chronicle(
    val id: String,
    val name: String,
    val description: String = "",
    val storytellerName: String = "",
    val userRole: ChronicleUserRole = ChronicleUserRole.PLAYER,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
