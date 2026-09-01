package com.v20charactermanager.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "scenes",
    foreignKeys = [
        ForeignKey(
            entity = ChronicleEntity::class,
            parentColumns = ["id"],
            childColumns = ["chronicleId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("chronicleId")]
)
data class SceneEntity(
    @PrimaryKey val id: String,
    val chronicleId: String,
    val storyId: String? = null,
    val sessionId: String? = null,
    val title: String,
    val order: Int = 0,
    val locationId: String? = null,
    val participantIds: String = "",
    val npcIds: String = "",
    val hook: String? = null,
    val objective: String? = null,
    val conflict: String? = null,
    val mood: String? = null,
    val description: String? = null,
    val clueIds: String = "",
    val secretIds: String = "",
    val possibleComplications: String = "",
    val mediaAssetIds: String = "",
    val notes: String = "",
    val outcome: String? = null,
    val status: String = "PLANNED",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
