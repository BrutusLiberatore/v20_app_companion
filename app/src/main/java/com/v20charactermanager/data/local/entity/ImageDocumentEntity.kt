package com.v20charactermanager.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "image_documents",
    foreignKeys = [
        ForeignKey(
            entity = MediaAssetEntity::class,
            parentColumns = ["id"],
            childColumns = ["mediaAssetId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("mediaAssetId")]
)
data class ImageDocumentEntity(
    @PrimaryKey val id: String,
    val mediaAssetId: String,
    val currentRevisionId: String? = null,
    val zoomDefaults: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val modifiedAt: Long = System.currentTimeMillis()
)
