package com.v20charactermanager.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class MeritValue(
    val id: String,
    val name: String,
    val cost: Int = 0,
    val description: String = "",
    val notes: String? = null
)
