package com.v20charactermanager.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "relationships",
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
data class RelationshipEntity(
    @PrimaryKey val id: String,
    val chronicleId: String,
    val fromEntityId: String,
    val fromEntityType: String,
    val toEntityId: String,
    val toEntityType: String,
    val typeId: String = "",
    val direction: String = "DIRECTED",
    val description: String = "",
    val strength: Int? = null,
    val visibility: String = "PUBLIC",
    val secret: Boolean = false,
    val status: String = "ACTIVE",
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
