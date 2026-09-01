package com.v20charactermanager.domain.engine

import com.v20charactermanager.domain.definition.GenerationDefinition

object GenerationRules {

    fun getBloodPoolMax(generation: Int): Int {
        val def = GenerationDefinition.forGeneration(generation) ?: return 10
        return if (GenerationDefinition.isBloodPoolDefined(generation)) def.bloodPoolMax else 10
    }

    fun getBloodPerTurn(generation: Int): Int {
        val def = GenerationDefinition.forGeneration(generation) ?: return 1
        return if (GenerationDefinition.isBloodPerTurnDefined(generation)) def.bloodPerTurn else 1
    }

    fun getMaxTrait(generation: Int): Int {
        return GenerationDefinition.forGeneration(generation)?.maxTrait ?: 5
    }

    fun isValidGeneration(generation: Int): Boolean {
        return GenerationDefinition.isValidGeneration(generation)
    }

    fun getMaxDisciplineLevel(generation: Int): Int {
        return getMaxTrait(generation)
    }

    fun getMaxBackgroundLevel(generation: Int): Int {
        return getMaxTrait(generation)
    }
}
