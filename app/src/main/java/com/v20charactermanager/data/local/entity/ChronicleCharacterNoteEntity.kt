package com.v20charactermanager.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "chronicle_character_notes",
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
        Index("characterId")
    ]
)
data class ChronicleCharacterNoteEntity(
    @PrimaryKey
    val id: String,
    val chronicleId: String,
    val characterId: String,
    val text: String,
    val visibility: String,
    val createdAt: Long,
    val updatedAt: Long
)
