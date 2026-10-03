package com.v20charactermanager.domain.engine

import com.v20charactermanager.domain.model.HouseRules

/**
 * Applies the chronicle house rules to the process-wide engines
 * (dice rolling and XP costs). Called when the rules screen opens/saves
 * and at app startup for the last configured chronicle.
 */
fun HouseRules.applyToEngines() {
    DiceEngine.configure(
        DiceRules(
            explodingTensAvailable = explodingTensAvailable,
            explodingTensDefault = explodingTensDefault,
            explodingTensRecursive = explodingTensRecursive,
            difficultyDefault = difficultyDefault
        )
    )
    XpCostCalculator.configure(toXpCostRules())
}
