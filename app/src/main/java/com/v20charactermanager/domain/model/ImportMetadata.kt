package com.v20charactermanager.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ImportMetadata(
    val importedAt: Long? = null,
    val sourceCharacterId: String? = null,
    val sourceSchemaVersion: Int? = null
)
