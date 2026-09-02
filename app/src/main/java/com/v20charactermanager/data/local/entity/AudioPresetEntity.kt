package com.v20charactermanager.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "audio_presets",
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
data class AudioPresetEntity(
    @PrimaryKey val id: String,
    val chronicleId: String,
    val name: String,
    val tracksJson: String = "[]",
    val createdAt: Long = System.currentTimeMillis()
)
