package com.v20charactermanager.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class FlawValue(
    val id: String,
    val name: String,
    val value: Int = 0,
    val description: String = "",
    val notes: String? = null
)
