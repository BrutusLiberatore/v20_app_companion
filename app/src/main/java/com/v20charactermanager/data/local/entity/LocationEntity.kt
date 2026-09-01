package com.v20charactermanager.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "locations",
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
data class LocationEntity(
    @PrimaryKey val id: String,
    val chronicleId: String,
    val name: String,
    val typeId: String = "Generic Location",
    val description: String = "",
    val districtOrArea: String = "",
    val controllerEntityId: String? = null,
    val factionId: String? = null,
    val linkedNpcIds: String = "",
    val linkedPlotIds: String = "",
    val mediaAssetIds: String = "",
    val narratorNotes: String = "",
    val imagePath: String? = null,
    val status: String = "ACTIVE",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
