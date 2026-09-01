package com.v20charactermanager.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ChronicleMember(
    val id: String,
    val chronicleId: String,
    val characterId: String,
    val role: ChronicleMemberRole = ChronicleMemberRole.PLAYER_CHARACTER,
    val createdAt: Long = System.currentTimeMillis()
)
