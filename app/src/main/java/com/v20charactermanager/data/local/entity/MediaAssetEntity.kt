package com.v20charactermanager.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "media_assets",
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
data class MediaAssetEntity(
    @PrimaryKey val id: String,
    val chronicleId: String,
    val type: String = "OTHER",
    val title: String,
    val description: String = "",
    val originalFilePath: String,
    val thumbnailFilePath: String? = null,
    val width: Int = 0,
    val height: Int = 0,
    val tags: String = "",
    val linkedEntityIds: String = "",
    val visibility: String = "GM_ONLY",
    val createdAt: Long = System.currentTimeMillis(),
    val modifiedAt: Long = System.currentTimeMillis()
)
