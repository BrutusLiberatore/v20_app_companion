package com.v20charactermanager.domain.model

import kotlinx.serialization.Serializable

enum class PlotType {
    MAIN, SUBPLOT, PERSONAL, CUSTOM
}

enum class PlotStatus {
    PLANNED, ACTIVE, PAUSED, RESOLVED, ABANDONED, ARCHIVED
}

@Serializable
data class PlotArc(
    val id: String,
    val chronicleId: String,
    val title: String,
    val summary: String = "",
    val type: PlotType = PlotType.MAIN,
    val status: PlotStatus = PlotStatus.PLANNED,
    val themeIds: List<String> = emptyList(),
    val characterIds: List<String> = emptyList(),
    val npcIds: List<String> = emptyList(),
    val locationIds: List<String> = emptyList(),
    val startingSituation: String = "",
    val possibleDevelopments: List<String> = emptyList(),
    val possibleClimax: String = "",
    val resolutionNotes: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)