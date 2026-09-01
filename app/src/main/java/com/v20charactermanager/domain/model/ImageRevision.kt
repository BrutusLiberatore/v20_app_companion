package com.v20charactermanager.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ImageRevision(
    val id: String,
    val imageDocumentId: String,
    val mediaAssetId: String,
    val revisionNumber: Int,
    val createdAt: Long = System.currentTimeMillis(),
    val sessionId: String? = null,
    val description: String? = null,
    val annotationSnapshot: String = "[]",
    val layerSnapshot: String = "[]"
)
