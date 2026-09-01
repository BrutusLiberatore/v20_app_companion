package com.v20charactermanager.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class CharacterNarrative(
    val description: String = "",
    val appearance: String = "",
    val personality: String = "",
    val backstory: String = "",
    val embrace: String = "",
    val sireRelationship: String = "",
    val haven: String = "",
    val feedingHabits: String = "",
    val motivations: String = "",
    val goals: String = "",
    val connections: String = "",
    val prelude: String = "",
    val additionalNotes: String = ""
)
