package com.v20charactermanager.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "npcs",
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
data class NpcEntity(
    @PrimaryKey val id: String,
    val chronicleId: String,
    val name: String,
    val portraitAssetId: String? = null,
    val creatureType: String = "MORTAL",
    val clanId: String? = null,
    val sectId: String? = null,
    val role: String = "",
    val description: String = "",
    val personality: String = "",
    val motivation: String = "",
    val narratorNotes: String = "",
    val imagePath: String? = null,
    val status: String = "ACTIVE",
    val type: String = "QUICK",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
