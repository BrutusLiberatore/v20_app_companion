package com.v20charactermanager.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "audio_tracks",
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
data class AudioTrackEntity(
    @PrimaryKey val id: String,
    val chronicleId: String,
    val title: String,
    val filePath: String,
    val category: String = "CUSTOM",
    val isLooping: Boolean = true,
    val volume: Float = 0.7f,
    val isActive: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
