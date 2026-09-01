package com.v20charactermanager.domain.definition

import kotlinx.serialization.Serializable

@Serializable
data class GenerationDefinition(
    val generation: Int,
    val maxTrait: Int,
    val bloodPoolMax: Int,
    val bloodPerTurn: Int
) {
    companion object {
        val definitions = listOf(
            GenerationDefinition(3, 10, -1, -1),   // ??? values from DOCX
            GenerationDefinition(4, 9, 50, 10),
            GenerationDefinition(5, 8, 40, 8),
            GenerationDefinition(6, 7, 30, 6),
            GenerationDefinition(7, 6, 20, 4),
            GenerationDefinition(8, 5, 15, 3),
            GenerationDefinition(9, 5, 14, 2),
            GenerationDefinition(10, 5, 13, 1),
            GenerationDefinition(11, 5, 12, 1),
            GenerationDefinition(12, 5, 11, 1),
            GenerationDefinition(13, 5, 10, 1)
        )

        fun forGeneration(generation: Int): GenerationDefinition? =
            definitions.find { it.generation == generation }

        fun isValidGeneration(generation: Int): Boolean =
            generation in 3..13

        fun isBloodPoolDefined(generation: Int): Boolean =
            generation in 4..13

        fun isBloodPerTurnDefined(generation: Int): Boolean =
            generation in 4..13
    }
}
