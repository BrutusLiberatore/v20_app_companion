package com.v20charactermanager.domain.model

/**
 * When the cinematic dice reveal (full-screen) should interrupt the table.
 * Preference is stored as a string key; see [fromPrefValue]/[toPrefValue].
 */
enum class DiceRevealMode {
    /** Never show the cinematic reveal (the light 3D animation still plays). */
    OFF,

    /** Only on critical moments: botch or total success (default). */
    CRITICAL,

    /** On every visible roll. */
    EVERY;

    fun triggersOn(isCritical: Boolean): Boolean = when (this) {
        OFF -> false
        CRITICAL -> isCritical
        EVERY -> true
    }

    companion object {
        const val PREF_VALUE_OFF = "off"
        const val PREF_VALUE_CRITICAL = "critical"
        const val PREF_VALUE_EVERY = "every"

        fun fromPrefValue(value: String?): DiceRevealMode = when (value) {
            PREF_VALUE_EVERY -> EVERY
            PREF_VALUE_OFF -> OFF
            else -> CRITICAL
        }

        fun toPrefValue(mode: DiceRevealMode): String = when (mode) {
            OFF -> PREF_VALUE_OFF
            CRITICAL -> PREF_VALUE_CRITICAL
            EVERY -> PREF_VALUE_EVERY
        }
    }
}

object DiceReveal {

    /**
     * A roll is critical when it is a botch, or when every die succeeded
     * (net successes >= pool). Rolls without data (old senders) are never critical.
     */
    fun isCritical(isBotch: Boolean?, netSuccesses: Int?, pool: Int): Boolean {
        if (isBotch == true) return true
        if (isBotch == null && netSuccesses == null) return false
        return pool > 0 && netSuccesses != null && netSuccesses >= pool
    }

    fun isCritical(roll: LiveRoomMessage.DiceRoll): Boolean =
        isCritical(roll.isBotch, roll.netSuccesses, roll.pool.toIntOrNull() ?: 0)
}
