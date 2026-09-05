package com.v20charactermanager.ui.chronicle

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.v20charactermanager.R
import com.v20charactermanager.domain.model.CreatureType

@Composable
fun QuickActionBar(
    onDiceClick: () -> Unit,
    onNoteClick: () -> Unit,
    onEventClick: () -> Unit,
    onQuickNpc: (String, CreatureType, String) -> Unit,
    onLiveRoom: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showQuickNpcDialog by remember { mutableStateOf(false) }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AssistChip(
            onClick = onDiceClick,
            label = {
                Text(
                    text = stringResource(R.string.dice_title),
                    fontWeight = FontWeight.Bold
                )
            },
            leadingIcon = {
                Icon(Icons.Filled.Casino, contentDescription = null, modifier = Modifier.size(18.dp))
            },
            modifier = Modifier.weight(1f)
        )
        AssistChip(
            onClick = onNoteClick,
            label = {
                Text(
                    text = stringResource(R.string.storyteller_quick_note),
                    fontWeight = FontWeight.Bold
                )
            },
            leadingIcon = {
                Icon(Icons.Filled.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
            },
            modifier = Modifier.weight(1f)
        )
        AssistChip(
            onClick = onEventClick,
            label = {
                Text(
                    text = stringResource(R.string.storyteller_event),
                    fontWeight = FontWeight.Bold
                )
            },
            leadingIcon = {
                Icon(Icons.Filled.AddCircle, contentDescription = null, modifier = Modifier.size(18.dp))
            },
            modifier = Modifier.weight(1f)
        )
        AssistChip(
            onClick = { showQuickNpcDialog = true },
            label = {
                Text(
                    text = stringResource(R.string.quick_npc),
                    fontWeight = FontWeight.Bold
                )
            },
            leadingIcon = {
                Icon(Icons.Filled.PersonAdd, contentDescription = null, modifier = Modifier.size(18.dp))
            },
            modifier = Modifier.weight(1f)
        )
        AssistChip(
            onClick = onLiveRoom,
            label = {
                Text(
                    text = stringResource(R.string.live_room),
                    fontWeight = FontWeight.Bold
                )
            },
            leadingIcon = {
                Icon(Icons.Filled.Casino, contentDescription = null, modifier = Modifier.size(18.dp))
            },
            modifier = Modifier.weight(1f)
        )
    }

    if (showQuickNpcDialog) {
        QuickNpcDialog(
            onDismiss = { showQuickNpcDialog = false },
            onCreate = { name, creatureType, role ->
                onQuickNpc(name, creatureType, role)
                showQuickNpcDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QuickNpcDialog(
    onDismiss: () -> Unit,
    onCreate: (String, CreatureType, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var creatureType by remember { mutableStateOf(CreatureType.MORTAL) }
    var role by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.quick_npc_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.quick_npc_name)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it }
                ) {
                    OutlinedTextField(
                        value = creatureType.name.replaceFirstChar { it.titlecase() },
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(R.string.quick_npc_type)) },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        CreatureType.entries.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type.name.replaceFirstChar { it.titlecase() }) },
                                onClick = {
                                    creatureType = type
                                    expanded = false
                                }
                            )
                        }
                    }
                }
                OutlinedTextField(
                    value = role,
                    onValueChange = { role = it },
                    label = { Text(stringResource(R.string.quick_npc_role)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isNotBlank()) {
                        onCreate(name.trim(), creatureType, role.trim())
                    }
                },
                enabled = name.isNotBlank()
            ) {
                Text(stringResource(R.string.quick_npc_create))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}
