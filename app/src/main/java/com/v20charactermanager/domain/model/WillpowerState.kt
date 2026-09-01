package com.v20charactermanager.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class WillpowerState(
    val permanent: Int = 5,
    val current: Int = 5
) {
    init {
        require(permanent in 1..10) { "Permanent willpower must be between 1 and 10" }
        require(current in 0..permanent) { "Current willpower must be between 0 and permanent" }
    }

    val isFull: Boolean
        get() = current == permanent

    val isEmpty: Boolean
        get() = current == 0

    fun spend(amount: Int = 1): WillpowerState {
        require(amount > 0) { "Amount to spend must be positive" }
        return copy(current = (current - amount).coerceAtLeast(0))
    }

    fun recover(amount: Int = 1): WillpowerState {
        require(amount > 0) { "Amount to recover must be positive" }
        return copy(current = (current + amount).coerceAtMost(permanent))
    }

    fun setPermanent(newPermanent: Int): WillpowerState {
        require(newPermanent in 1..10) { "Permanent willpower must be between 1 and 10" }
        return copy(
            permanent = newPermanent,
            current = current.coerceAtMost(newPermanent)
        )
    }
}
