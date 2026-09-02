package com.v20charactermanager.domain.model

import kotlinx.serialization.Serializable

enum class AudioTrackCategory {
    AMBIENCE, MUSIC, SFX, CUSTOM
}

@Serializable
data class AudioTrack(
    val id: String,
    val chronicleId: String,
    val title: String,
    val filePath: String,
    val category: AudioTrackCategory = AudioTrackCategory.CUSTOM,
    val isLooping: Boolean = true,
    val volume: Float = 0.7f,
    val isActive: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
