package com.v20charactermanager.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ImageDocument(
    val id: String,
    val mediaAssetId: String,
    val currentRevisionId: String? = null,
    val zoomDefaults: ZoomDefaults? = null,
    val layerIds: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val modifiedAt: Long = System.currentTimeMillis()
)

@Serializable
data class ZoomDefaults(
    val minZoom: Float = 0.5f,
    val maxZoom: Float = 5.0f,
    val initialZoom: Float = 1.0f,
    val panX: Float = 0f,
    val panY: Float = 0f
)
