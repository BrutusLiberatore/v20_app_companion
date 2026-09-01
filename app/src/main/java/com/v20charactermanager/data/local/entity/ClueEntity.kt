package com.v20charactermanager.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "clues",
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
data class ClueEntity(
    @PrimaryKey val id: String,
    val chronicleId: String,
    val title: String,
    val content: String? = null,
    val mediaAssetId: String? = null,
    val linkedSecretIds: String = "",
    val status: String = "UNKNOWN",
    val discoveredAtEventId: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
