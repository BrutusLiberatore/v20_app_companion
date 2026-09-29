package com.v20charactermanager.domain.model

import com.v20charactermanager.domain.definition.DamageType

/**
 * Applies a live-table [LiveRoomMessage.StatUpdate] to a character.
 * Returns null when the message does not apply (unknown field or bad values).
 *
 * Fields: "blood" (intValue), "willpower" (intValue),
 * "health" (stringValue = "index:DAMAGETYPE" or "index:HEAL").
 * Values are clamped to the character's current maximums.
 */
fun Character.applyStatUpdate(
    field: String,
    intValue: Int?,
    stringValue: String?
): Character? = when (field) {
    "blood" -> intValue?.let { v ->
        copy(bloodPool = bloodPool.copy(current = v.coerceIn(0, bloodPool.maximum)))
    }

    "willpower" -> intValue?.let { v ->
        copy(willpower = willpower.copy(current = v.coerceIn(0, willpower.permanent)))
    }

    "health" -> {
        val raw = stringValue
        val idx = raw?.substringBefore(':')?.toIntOrNull()
        if (raw == null || idx == null || idx !in health.levels.indices) {
            null
        } else {
            val kind = raw.substringAfter(':', "")
            if (kind == "HEAL") {
                copy(health = health.heal(idx))
            } else {
                val type = DamageType.entries.firstOrNull { it.name == kind }
                if (type == null) null else copy(health = health.withDamage(idx, type))
            }
        }
    }

    else -> null
}
