package com.v20charactermanager.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ChronicleNote(
    val id: String,
    val chronicleId: String,
    val text: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
