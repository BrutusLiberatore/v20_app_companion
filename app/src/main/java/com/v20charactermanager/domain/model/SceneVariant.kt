package com.v20charactermanager.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class SceneVariant(
    val id: String,
    val sceneId: String,
    val name: String,
    val assetIds: List<String> = emptyList(),
    val notes: String? = null,
    val isDefault: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Returns the list with [variantId] as the only default variant of [sceneId].
 * Passing null as [variantId] clears the default of that scene.
 * Variants of other scenes are left untouched.
 */
fun List<SceneVariant>.withDefaultSelection(sceneId: String, variantId: String?): List<SceneVariant> =
    map { variant ->
        if (variant.sceneId != sceneId) variant
        else variant.copy(isDefault = variant.id == variantId)
    }
