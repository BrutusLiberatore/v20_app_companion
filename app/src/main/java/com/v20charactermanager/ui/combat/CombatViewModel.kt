package com.v20charactermanager.ui.combat

import androidx.lifecycle.ViewModel
import com.v20charactermanager.domain.model.CombatEngine
import com.v20charactermanager.domain.model.CombatState
import com.v20charactermanager.domain.model.Combatant
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

/** Local (offline) combat tracker, backed by the in-memory [CombatHolder]. */
class CombatViewModel : ViewModel() {

    val state: StateFlow<CombatState> = CombatHolder.state

    fun start() {
        mutate { CombatEngine.start(it.combatants, it.reRollEachRound) }
    }

    fun add(name: String, initiative: Int) {
        val trimmed = name.trim().ifBlank { return }
        mutate {
            CombatEngine.upsert(
                it,
                Combatant(id = UUID.randomUUID().toString(), name = trimmed, initiative = initiative)
            )
        }
    }

    fun remove(id: String) {
        mutate { CombatEngine.remove(it, id) }
    }

    fun advance() {
        mutate { CombatEngine.advance(it) }
    }

    fun toggleReroll(enabled: Boolean) {
        mutate { CombatEngine.setReroll(it, enabled) }
    }

    fun end() {
        mutate { CombatEngine.end() }
    }

    private inline fun mutate(crossinline f: (CombatState) -> CombatState) {
        CombatHolder.state.update { f(it) }
    }
}
