package com.v20charactermanager.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "quick_notes",
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
data class QuickNoteEntity(
    @PrimaryKey val id: String,
    val chronicleId: String,
    val scopeType: String,
    val scopeId: String?,
    val text: String,
    val visibility: String,
    val createdAt: Long,
    val modifiedAt: Long
)
