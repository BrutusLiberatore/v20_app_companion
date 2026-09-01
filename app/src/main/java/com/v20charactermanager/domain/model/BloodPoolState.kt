package com.v20charactermanager.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class BloodPoolState(
    val maximum: Int = 10,
    val current: Int = 10
) {
    init {
        require(maximum > 0) { "Blood pool maximum must be positive" }
        require(current in 0..maximum) { "Blood pool current must be between 0 and maximum" }
    }

    val isFull: Boolean
        get() = current == maximum

    val isEmpty: Boolean
        get() = current == 0

    fun spend(amount: Int = 1): BloodPoolState {
        require(amount > 0) { "Amount to spend must be positive" }
        return copy(current = (current - amount).coerceAtLeast(0))
    }

    fun refill(amount: Int = 1): BloodPoolState {
        require(amount > 0) { "Amount to refill must be positive" }
        return copy(current = (current + amount).coerceAtMost(maximum))
    }

    fun setMaximum(newMax: Int): BloodPoolState {
        require(newMax > 0) { "Maximum must be positive" }
        return copy(
            maximum = newMax,
            current = current.coerceAtMost(newMax)
        )
    }
}
