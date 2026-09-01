package com.v20charactermanager.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "factions",
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
data class FactionEntity(
    @PrimaryKey val id: String,
    val chronicleId: String,
    val name: String,
    val typeId: String? = null,
    val sectId: String? = null,
    val description: String = "",
    val leaderEntityId: String? = null,
    val memberIds: String = "",
    val objectives: String = "",
    val allyFactionIds: String = "",
    val enemyFactionIds: String = "",
    val locationIds: String = "",
    val narratorNotes: String = "",
    val imagePath: String? = null,
    val status: String = "ACTIVE",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
