package com.v20charactermanager.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "session_events",
    foreignKeys = [
        ForeignKey(
            entity = ChronicleEntity::class,
            parentColumns = ["id"],
            childColumns = ["chronicleId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("chronicleId"), Index("sessionId")]
)
data class SessionEventEntity(
    @PrimaryKey val id: String,
    val chronicleId: String,
    val sessionId: String?,
    val sceneId: String?,
    val timestamp: Long,
    val type: String,
    val title: String,
    val description: String?,
    val entityRefs: String,
    val visibility: String,
    val metadata: String?,
    val origin: String,
    val createdAt: Long
)
