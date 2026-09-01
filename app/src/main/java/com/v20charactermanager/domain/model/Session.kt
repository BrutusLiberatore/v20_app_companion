package com.v20charactermanager.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Session(
    val id: String,
    val chronicleId: String,
    val number: Int,
    val title: String = "",
    val date: Long = System.currentTimeMillis(),
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
