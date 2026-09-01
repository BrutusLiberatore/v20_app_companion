package com.v20charactermanager.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "image_layers",
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
data class ImageLayerEntity(
    @PrimaryKey val id: String,
    val imageDocumentId: String,
    val name: String,
    val visible: Boolean = true,
    val visibility: String = "GM_ONLY",
    val locked: Boolean = false,
    val order: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)
