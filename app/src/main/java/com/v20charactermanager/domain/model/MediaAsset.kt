package com.v20charactermanager.domain.model

import kotlinx.serialization.Serializable

enum class MediaAssetType {
    MAP, LOCATION_MAP, PORTRAIT, SYMBOL, DOCUMENT, PHOTO, CLUE_VISUAL, DIAGRAM, VIDEO, OTHER
}

enum class MediaAssetCategory {
    ALL, MAPS, NPC, LOCATIONS, CLUES, DOCUMENTS, OTHER
}

@Serializable
data class MediaAsset(
    val id: String,
    val chronicleId: String,
    val type: MediaAssetType = MediaAssetType.OTHER,
    val title: String,
    val description: String = "",
    val originalFilePath: String,
    val thumbnailFilePath: String? = null,
    val width: Int = 0,
    val height: Int = 0,
    val tags: List<String> = emptyList(),
    val linkedEntityIds: List<String> = emptyList(),
    val visibility: Visibility = Visibility.GM_ONLY,
    val createdAt: Long = System.currentTimeMillis(),
    val modifiedAt: Long = System.currentTimeMillis()
)
