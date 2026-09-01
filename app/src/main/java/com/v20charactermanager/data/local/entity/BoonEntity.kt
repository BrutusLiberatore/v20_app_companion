package com.v20charactermanager.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "boons",
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
data class BoonEntity(
    @PrimaryKey val id: String,
    val chronicleId: String,
    val creditorEntityId: String,
    val debtorEntityId: String,
    val typeId: String? = null,
    val description: String = "",
    val status: String = "OPEN",
    val witnessedBy: String = "",
    val visibility: String = "GM_ONLY",
    val narratorNotes: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
