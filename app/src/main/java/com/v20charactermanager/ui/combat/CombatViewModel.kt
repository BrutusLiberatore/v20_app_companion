package com.v20charactermanager.ui.combat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.v20charactermanager.domain.model.CombatEngine
import com.v20charactermanager.domain.model.CombatState
import com.v20charactermanager.domain.model.Combatant
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

/** Local (offline) combat tracker, backed by the in-memory [CombatHolder]. */
class CombatViewModel : ViewModel() {

    val state: StateFlow<CombatState> = CombatHolder.state
    private var timerAlertedFor: Long = 0

    init {
        viewModelScope.launch {
            while (true) {
                kotlinx.coroutines.delay(500)
                try {
                    tickTimer()
                } catch (_: Exception) {
                }
            }
        }
    }

    private fun tickTimer() {
        val s = CombatHolder.state.value
        if (!s.active || !CombatEngine.isTimerExpired(s, System.currentTimeMillis())) return
        if (!s.timerAutoAdvance) return
        if (timerAlertedFor == s.timerEndsAt) return
        timerAlertedFor = s.timerEndsAt
        advance()
    }

    fun start() {
        mutate {
            CombatEngine.start(
                combatants = it.combatants,
                reRollEachRound = it.reRollEachRound,
                previousConfig = it,
                now = System.currentTimeMillis()
            )
        }
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
        mutate { CombatEngine.advance(it, System.currentTimeMillis()) }
    }

    fun toggleReroll(enabled: Boolean) {
        mutate { CombatEngine.setReroll(it, enabled) }
    }

    fun setTimer(seconds: Int) {
        mutate { CombatEngine.setTimer(it, seconds, System.currentTimeMillis()) }
    }

    fun pauseTimer() {
        mutate { CombatEngine.pauseTimer(it, System.currentTimeMillis()) }
    }

    fun resumeTimer() {
        mutate { CombatEngine.resumeTimer(it, System.currentTimeMillis()) }
    }

    fun setTimerAutoAdvance(enabled: Boolean) {
        mutate { CombatEngine.setTimerAutoAdvance(it, enabled) }
    }

    fun end() {
        mutate { CombatEngine.end(it) }
    }

    private inline fun mutate(crossinline f: (CombatState) -> CombatState) {
        CombatHolder.state.update { f(it) }
    }
}
