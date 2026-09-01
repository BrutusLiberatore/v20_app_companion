package com.v20charactermanager.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "plot_arcs",
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
data class PlotArcEntity(
    @PrimaryKey val id: String,
    val chronicleId: String,
    val title: String,
    val summary: String = "",
    val type: String = "MAIN",
    val status: String = "PLANNED",
    val themeIds: String = "",
    val characterIds: String = "",
    val npcIds: String = "",
    val locationIds: String = "",
    val startingSituation: String = "",
    val possibleDevelopments: String = "",
    val possibleClimax: String = "",
    val resolutionNotes: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
