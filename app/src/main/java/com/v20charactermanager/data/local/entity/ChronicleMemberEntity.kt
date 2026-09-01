package com.v20charactermanager.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "chronicle_members",
    foreignKeys = [
        ForeignKey(
            entity = ChronicleEntity::class,
            parentColumns = ["id"],
            childColumns = ["chronicleId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CharacterEntity::class,
            parentColumns = ["id"],
            childColumns = ["characterId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("chronicleId"),
        Index("characterId"),
        Index(value = ["chronicleId", "characterId"], unique = true)
    ]
)
data class ChronicleMemberEntity(
    @PrimaryKey
    val id: String,
    val chronicleId: String,
    val characterId: String,
    val role: String,
    val createdAt: Long
)
