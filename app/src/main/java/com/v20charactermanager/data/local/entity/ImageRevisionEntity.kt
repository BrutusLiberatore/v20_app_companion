package com.v20charactermanager.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "image_revisions",
    foreignKeys = [
        ForeignKey(
            entity = ImageDocumentEntity::class,
            parentColumns = ["id"],
            childColumns = ["imageDocumentId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("imageDocumentId")]
)
data class ImageRevisionEntity(
    @PrimaryKey val id: String,
    val imageDocumentId: String,
    val mediaAssetId: String,
    val revisionNumber: Int,
    val createdAt: Long = System.currentTimeMillis(),
    val sessionId: String? = null,
    val description: String? = null,
    val annotationSnapshot: String = "[]",
    val layerSnapshot: String = "[]"
)
