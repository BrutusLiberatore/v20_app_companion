package com.v20charactermanager.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ExperienceState(
    val earned: Int = 0,
    val spent: Int = 0
) {
    init {
        require(earned >= 0) { "Earned XP must be non-negative" }
        require(spent >= 0) { "Spent XP must be non-negative" }
        require(spent <= earned) { "Spent XP cannot exceed earned XP" }
    }

    val available: Int
        get() = earned - spent

    fun earn(amount: Int): ExperienceState {
        require(amount >= 0) { "Amount to earn must be non-negative" }
        return copy(earned = earned + amount)
    }

    fun spend(amount: Int): ExperienceState {
        require(amount >= 0) { "Amount to spend must be non-negative" }
        require(amount <= available) { "Not enough available XP" }
        return copy(spent = spent + amount)
    }
}
