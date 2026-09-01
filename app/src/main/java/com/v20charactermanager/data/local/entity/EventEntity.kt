package com.v20charactermanager.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "events",
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
data class EventEntity(
    @PrimaryKey val id: String,
    val chronicleId: String,
    val sessionId: String? = null,
    val sceneId: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val inGameTime: String? = null,
    val typeId: String = "GENERAL",
    val title: String,
    val description: String? = null,
    val involvedEntityIds: String = "",
    val consequenceNotes: String = "",
    val visibility: String = "GM_ONLY",
    val imagePath: String? = null,
    val mediaAssetIds: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
