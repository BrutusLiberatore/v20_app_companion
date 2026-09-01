package com.v20charactermanager.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ImageLayer(
    val id: String,
    val imageDocumentId: String,
    val name: String,
    val visible: Boolean = true,
    val visibility: Visibility = Visibility.GM_ONLY,
    val locked: Boolean = false,
    val order: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)
