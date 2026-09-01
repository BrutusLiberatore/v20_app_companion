package com.v20charactermanager.domain.model

import kotlinx.serialization.Serializable

enum class RelationshipDirection {
    DIRECTED, BIDIRECTIONAL
}

enum class RelationshipStatus {
    ACTIVE, ENDED
}

@Serializable
data class Relationship(
    val id: String,
    val chronicleId: String,
    val fromEntityId: String,
    val fromEntityType: String,
    val toEntityId: String,
    val toEntityType: String,
    val typeId: String = "",
    val direction: RelationshipDirection = RelationshipDirection.DIRECTED,
    val description: String = "",
    val strength: Int? = null,
    val visibility: Visibility = Visibility.PUBLIC,
    val secret: Boolean = false,
    val status: RelationshipStatus = RelationshipStatus.ACTIVE,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)