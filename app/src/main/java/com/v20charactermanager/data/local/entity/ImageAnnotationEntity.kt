package com.v20charactermanager.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "image_annotations",
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
data class ImageAnnotationEntity(
    @PrimaryKey val id: String,
    val layerId: String,
    val imageDocumentId: String,
    val type: String,
    val geometryJson: String = "{}",
    val styleJson: String = "{}",
    val text: String? = null,
    val pinType: String? = null,
    val linkedEntityId: String? = null,
    val linkedEntityType: String? = null,
    val visibility: String = "GM_ONLY",
    val createdAt: Long = System.currentTimeMillis(),
    val modifiedAt: Long = System.currentTimeMillis()
)
