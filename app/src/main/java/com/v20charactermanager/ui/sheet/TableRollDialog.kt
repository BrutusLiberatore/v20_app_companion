package com.v20charactermanager.ui.sheet

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.v20charactermanager.R
import com.v20charactermanager.domain.model.Character
import com.v20charactermanager.domain.model.RollSpec

private data class RollTrait(val key: String, val name: String, val value: Int)

/**
 * V20 table roll from the character sheet: the player picks one, two or three
 * traits (Attributes/Abilities) and rolls a pool of dice equal to their total
 * value, using standard V20 resolution (6+ successes, 1s subtract, per the
 * manual rules implemented by DiceEngine).
 */
@Composable
fun TableRollDialog(
    character: Character,
    onRoll: (RollSpec) -> Unit,
    onDismiss: () -> Unit
) {
    var difficulty by remember { mutableIntStateOf(com.v20charactermanager.domain.engine.DiceEngine.defaultDifficulty()) }
    var diceModifier by remember { mutableIntStateOf(0) }
    var willpowerUsed by remember { mutableStateOf(false) }
    var explodingTens by remember { mutableStateOf(com.v20charactermanager.domain.engine.DiceEngine.currentRules().explodingTensDefault) }
    val selected = remember { mutableStateListOf<RollTrait>() }

    val groups = remember(character) {
        buildList<Pair<String, List<RollTrait>>> {
            character.attributeByCategory.forEach { (category, attrs) ->
                val rows = attrs.map { RollTrait("A:${it.id}", it.id.nameEn, it.value) }
                if (rows.isNotEmpty()) add(category.name to rows)
            }
            character.abilityByCategory.forEach { (category, abilities) ->
                val rows = abilities.filter { it.value > 0 }
                    .map { RollTrait("B:${it.id}", it.id.nameEn, it.value) }
                if (rows.isNotEmpty()) add(category.name to rows)
            }
        }
    }

    val chosen = groups.flatMap { it.second }.filter { t -> selected.any { it.key == t.key } }
    val pool = chosen.sumOf { it.value }
    val label = chosen.joinToString(" + ") { "${it.name} ${it.value}" }
    val finalPool = (pool + diceModifier + if (willpowerUsed) 1 else 0).coerceAtLeast(1)

    fun toggle(trait: RollTrait) {
        if (selected.any { it.key == trait.key }) {
            selected.removeAll { it.key == trait.key }
        } else if (selected.size < 3) {
            selected.add(trait)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.sheet_roll_to_table),
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Text(
                    text = stringResource(R.string.sheet_roll_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Column(
                    modifier = Modifier
                        .heightIn(max = 320.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    groups.forEach { (groupName, rows) ->
                        Text(
                            text = groupName,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 6.dp)
                        )
                        rows.forEach { trait ->
                            val isChecked = selected.any { it.key == trait.key }
                            val disabled = !isChecked && selected.size >= 3
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable(enabled = !disabled) { toggle(trait) }
                            ) {
                                Checkbox(
                                    checked = isChecked,
                                    enabled = !disabled,
                                    onCheckedChange = { toggle(trait) }
                                )
                                Text(
                                    text = trait.name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.weight(1f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = trait.value.toString(),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.sheet_roll_pool, finalPool),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(R.string.live_roll_difficulty),
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    IconButton(
                        onClick = { if (difficulty > 2) difficulty-- },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "-", modifier = Modifier.size(18.dp))
                    }
                    Text(
                        text = difficulty.toString(),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.width(32.dp)
                    )
                    IconButton(
                        onClick = { if (difficulty < 10) difficulty++ },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "+", modifier = Modifier.size(18.dp))
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(R.string.dice_modifier),
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    IconButton(
                        onClick = { if (diceModifier > -5) diceModifier-- },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "-", modifier = Modifier.size(18.dp))
                    }
                    Text(
                        text = diceModifier.toString(),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.width(32.dp)
                    )
                    IconButton(
                        onClick = { if (diceModifier < 10) diceModifier++ },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "+", modifier = Modifier.size(18.dp))
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { willpowerUsed = !willpowerUsed }
                ) {
                    Checkbox(
                        checked = willpowerUsed,
                        onCheckedChange = { willpowerUsed = it }
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = stringResource(R.string.dice_willpower),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { explodingTens = !explodingTens }
                ) {
                    Checkbox(
                        checked = explodingTens,
                        enabled = com.v20charactermanager.domain.engine.DiceEngine.currentRules().explodingTensAvailable,
                        onCheckedChange = { explodingTens = it }
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = stringResource(R.string.dice_exploding_tens),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = finalPool >= 1,
                onClick = {
                    onRoll(
                        RollSpec(
                            pool = pool,
                            difficulty = difficulty,
                            diceModifier = diceModifier,
                            willpowerUsed = willpowerUsed,
                            explodingTens = explodingTens,
                            reason = label
                        )
                    )
                }
            ) {
                Text(
                    text = stringResource(R.string.dashboard_roll_dice),
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_cancel))
            }
        }
    )
}
