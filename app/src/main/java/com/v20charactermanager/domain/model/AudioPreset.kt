package com.v20charactermanager.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class AudioPresetTrack(
    val trackId: String,
    val volume: Float = 0.7f,
    val isLooping: Boolean = true
)

@Serializable
data class AudioPreset(
    val id: String,
    val chronicleId: String,
    val name: String,
    val tracks: List<AudioPresetTrack> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
)
