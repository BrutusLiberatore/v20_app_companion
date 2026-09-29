package com.v20charactermanager.ui.combat

import com.v20charactermanager.domain.model.CombatState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Process-lifetime holder for the standalone combat tracker.
 * State survives screen navigation and resets when the app process dies.
 */
object CombatHolder {
    val state = MutableStateFlow(CombatState())
}
