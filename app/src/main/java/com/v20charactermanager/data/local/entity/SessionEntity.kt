package com.v20charactermanager.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "sessions",
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
data class SessionEntity(
    @PrimaryKey
    val id: String,
    val chronicleId: String,
    val number: Int,
    val title: String,
    val date: Long,
    val notes: String,
    val createdAt: Long,
    val updatedAt: Long
)
