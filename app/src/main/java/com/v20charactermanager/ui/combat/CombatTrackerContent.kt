package com.v20charactermanager.ui.combat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.v20charactermanager.R
import com.v20charactermanager.domain.model.CombatEngine
import com.v20charactermanager.domain.model.CombatState
import java.util.Locale

/**
 * Shared combat tracker UI (V20: initiative = 1d10 + Dexterity + Wits,
 * highest acts first, order re-sorted when initiative changes).
 *
 * Used both in the live room (master controls / read-only player view)
 * and in the standalone local tracker.
 */
@Composable
fun CombatTrackerContent(
    combat: CombatState,
    canControl: Boolean,
    localCharacterId: String? = null,
    onRollInitiative: (() -> Unit)? = null,
    onStart: () -> Unit = {},
    onAdd: (String, Int) -> Unit = { _, _ -> },
    onRemove: (String) -> Unit = {},
    onAdvance: () -> Unit = {},
    onEnd: () -> Unit = {},
    onToggleReroll: (Boolean) -> Unit = {},
    onTimerSet: (Int) -> Unit = {},
    onTimerPause: () -> Unit = {},
    onTimerResume: () -> Unit = {},
    onTimerAutoAdvance: (Boolean) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }
    val canRoll = !canControl && combat.active && combat.reRollEachRound && onRollInitiative != null

    Column(modifier = modifier) {
        if (combat.active) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.combat_round, combat.round),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.weight(1f))
                if (canControl) {
                    Text(
                        text = stringResource(R.string.combat_reroll_toggle),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Switch(
                        checked = combat.reRollEachRound,
                        onCheckedChange = onToggleReroll,
                        modifier = Modifier.height(24.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (combat.active) {
            TurnTimerSection(
                combat = combat,
                canControl = canControl,
                onTimerSet = onTimerSet,
                onTimerPause = onTimerPause,
                onTimerResume = onTimerResume,
                onTimerAutoAdvance = onTimerAutoAdvance
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (combat.combatants.isEmpty()) {
            Text(
                text = stringResource(
                    if (combat.active) R.string.combat_empty else R.string.combat_waiting
                ),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            LazyColumn(
                modifier = Modifier.heightIn(max = 320.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                itemsIndexed(combat.combatants) { index, combatant ->
                    val isCurrent = combat.active && index == combat.currentIndex
                    val isLocal = localCharacterId != null &&
                        combatant.characterId == localCharacterId
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = if (isCurrent) {
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)
                                } else {
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                },
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "${index + 1}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.width(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = combatant.name,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
                            )
                            if (isLocal) {
                                Text(
                                    text = stringResource(R.string.combat_your_turn),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Surface(
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = combatant.initiative.toString(),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                        if (canControl) {
                            IconButton(
                                onClick = { onRemove(combatant.id) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = stringResource(R.string.combat_remove),
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (combat.active && combat.combatants.isNotEmpty()) {
            Text(
                text = stringResource(
                    R.string.combat_current_turn,
                    combat.combatants[combat.currentIndex.coerceIn(0, combat.combatants.size - 1)].name
                ),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (canControl) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (!combat.active) {
                    Button(
                        onClick = onStart,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(stringResource(R.string.combat_start))
                    }
                } else {
                    OutlinedButton(
                        onClick = { showAddDialog = true },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(stringResource(R.string.combat_add))
                    }
                    Button(
                        onClick = onAdvance,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.SkipNext, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(stringResource(R.string.combat_next))
                    }
                }
            }
            if (combat.active) {
                Spacer(modifier = Modifier.height(4.dp))
                TextButton(
                    onClick = onEnd,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text(stringResource(R.string.combat_end))
                }
            }
        } else if (canRoll) {
            Button(
                onClick = { onRollInitiative?.invoke() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.combat_roll_initiative))
            }
        }
    }

    if (showAddDialog) {
        CombatantEditDialog(
            title = stringResource(R.string.combat_add),
            onConfirm = { name, initiative ->
                onAdd(name, initiative)
                showAddDialog = false
            },
            onDismiss = { showAddDialog = false }
        )
    }
}

@Composable
private fun TurnTimerSection(
    combat: CombatState,
    canControl: Boolean,
    onTimerSet: (Int) -> Unit,
    onTimerPause: () -> Unit,
    onTimerResume: () -> Unit,
    onTimerAutoAdvance: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val timerEnabled = combat.timerSeconds > 0
    if (!timerEnabled && !canControl) return

    var nowMs by remember { mutableStateOf(System.currentTimeMillis()) }
    LaunchedEffect(combat.timerEndsAt) {
        if (combat.timerEndsAt <= 0) return@LaunchedEffect
        var alerted = false
        while (true) {
            nowMs = System.currentTimeMillis()
            if (nowMs >= combat.timerEndsAt) {
                if (!alerted) {
                    alerted = true
                    CombatTimerAlert.fire(context)
                }
                break
            }
            kotlinx.coroutines.delay(150)
        }
    }

    val remainingMs = CombatEngine.timerRemainingMs(combat, nowMs)
    val expired = combat.timerEndsAt > 0 && nowMs >= combat.timerEndsAt
    val running = combat.timerEndsAt > 0
    val paused = combat.timerPausedRemainingMs > 0
    val totalSeconds = remainingMs / 1000
    val timeText = if (!timerEnabled && !paused) {
        "—"
    } else {
        String.format(Locale.US, "%02d:%02d", totalSeconds / 60, totalSeconds % 60)
    }
    val countdownColor = when {
        expired -> MaterialTheme.colorScheme.error
        running && remainingMs <= 5000 -> MaterialTheme.colorScheme.error
        paused -> MaterialTheme.colorScheme.onSurfaceVariant
        else -> MaterialTheme.colorScheme.primary
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = if (expired) stringResource(R.string.combat_timer_expired) else timeText,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = countdownColor
        )
        if (paused) {
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = stringResource(R.string.combat_timer_pause),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        if (canControl && (running || paused)) {
            IconButton(onClick = { if (running) onTimerPause() else onTimerResume() }) {
                Icon(
                    imageVector = if (running) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = stringResource(
                        if (running) R.string.combat_timer_pause else R.string.combat_timer_resume
                    ),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }

    if (canControl) {
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            listOf(0, 15, 30, 60, 120).forEach { seconds ->
                val label = if (seconds == 0) {
                    stringResource(R.string.combat_timer_off)
                } else {
                    stringResource(R.string.combat_timer_seconds, seconds)
                }
                FilterChip(
                    selected = combat.timerSeconds == seconds,
                    onClick = { onTimerSet(seconds) },
                    label = { Text(label, style = MaterialTheme.typography.labelSmall) }
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.combat_timer_auto),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.weight(1f))
            Switch(
                checked = combat.timerAutoAdvance,
                onCheckedChange = onTimerAutoAdvance,
                modifier = Modifier.height(24.dp)
            )
        }
    }
}

@Composable
fun CombatantEditDialog(
    title: String,
    initialName: String = "",
    initialInitiative: Int = 0,
    onConfirm: (String, Int) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    var initiative by remember { mutableStateOf(initialInitiative.takeIf { it != 0 }?.toString() ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.combat_name_hint)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = initiative,
                    onValueChange = { input -> initiative = input.filter { it.isDigit() || (it == '-' && !initiative.contains('-')) } },
                    label = { Text(stringResource(R.string.combat_initiative_hint)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(name, initiative.toIntOrNull() ?: 0) },
                enabled = name.isNotBlank()
            ) {
                Text(stringResource(R.string.action_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_cancel))
            }
        }
    )
}
