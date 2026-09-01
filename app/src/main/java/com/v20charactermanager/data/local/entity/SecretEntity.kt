package com.v20charactermanager.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "secrets",
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
data class SecretEntity(
    @PrimaryKey val id: String,
    val chronicleId: String,
    val title: String,
    val content: String = "",
    val linkedEntityIds: String = "",
    val visibility: String = "GM_ONLY",
    val status: String = "HIDDEN",
    val revealedAtEventId: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
