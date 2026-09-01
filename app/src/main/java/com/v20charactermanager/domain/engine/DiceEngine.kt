package com.v20charactermanager.domain.engine

import com.v20charactermanager.domain.definition.RuleSet
import kotlin.random.Random

data class DiceResult(
    val individualResults: List<Int>,
    val successes: Int,
    val ones: Int,
    val isBotch: Boolean,
    val isFailure: Boolean,
    val isSuccess: Boolean,
    val netSuccesses: Int,
    val bonusDiceRolled: Int = 0
)

data class DiceRules(
    val explodingTensAvailable: Boolean = true,
    val explodingTensDefault: Boolean = false,
    val explodingTensRecursive: Boolean = false
)

object DiceEngine {

    private var diceRules = DiceRules()

    fun configure(rules: DiceRules) {
        diceRules = rules
    }

    fun roll(
        pool: Int,
        difficulty: Int = RuleSet.DIFFICULTY_STANDARD,
        diceModifier: Int = 0,
        difficultyModifier: Int = 0,
        extraDice: Int = 0,
        willpowerUsed: Boolean = false,
        explodingTens: Boolean? = null
    ): DiceResult {
        require(pool > 0) { "Dice pool must be positive" }

        val finalDifficulty = (difficulty + difficultyModifier).coerceIn(2, 10)
        val finalPool = (pool + diceModifier + extraDice + if (willpowerUsed) 1 else 0).coerceAtLeast(1)
        val useExploding = explodingTens ?: diceRules.explodingTensDefault

        val results = mutableListOf<Int>()
        var bonusDiceRemaining = 0

        for (i in 1..finalPool) {
            val die = Random.nextInt(1, 11)
            results.add(die)
            if (useExploding && die == 10) {
                bonusDiceRemaining++
            }
        }

        var bonusDiceRolled = 0
        if (useExploding && diceRules.explodingTensAvailable) {
            while (bonusDiceRemaining > 0) {
                val die = Random.nextInt(1, 11)
                results.add(die)
                bonusDiceRolled++
                bonusDiceRemaining--
                if (diceRules.explodingTensRecursive && die == 10) {
                    bonusDiceRemaining++
                }
            }
        }

        var successes = 0
        var ones = 0

        results.forEach { result ->
            if (result >= finalDifficulty) successes++
            if (result == 1) ones++
        }

        if (willpowerUsed) successes++

        val netSuccesses = successes - ones
        val isBotch = netSuccesses <= 0 && ones > 0
        val isFailure = netSuccesses <= 0 && !isBotch
        val isSuccess = netSuccesses > 0

        return DiceResult(
            individualResults = results,
            successes = successes,
            ones = ones,
            isBotch = isBotch,
            isFailure = isFailure,
            isSuccess = isSuccess,
            netSuccesses = netSuccesses,
            bonusDiceRolled = bonusDiceRolled
        )
    }

    fun rollDamage(
        baseDamage: Int,
        damageType: String = "bashing",
        difficulty: Int = RuleSet.DIFFICULTY_STANDARD
    ): DiceResult {
        return roll(baseDamage, difficulty)
    }
}
