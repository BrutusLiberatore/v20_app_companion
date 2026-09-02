package com.v20charactermanager.ui.chronicle

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.v20charactermanager.R
import com.v20charactermanager.domain.model.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NpcDetailSheet(
    npc: NpcEntry,
    linkableItems: List<LinkableItem>,
    onUpdate: (NpcEntry) -> Unit,
    onCreateSheet: (NpcEntry) -> Unit,
    onOpenSheet: (String) -> Unit,
    onLinkClick: (String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(npc.name) }
    var role by remember { mutableStateOf(npc.role) }
    var description by remember { mutableStateOf(npc.description) }
    var notes by remember { mutableStateOf(npc.narratorNotes) }
    var isEditing by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = npc.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { isEditing = !isEditing }) {
                    Icon(
                        if (isEditing) Icons.Filled.Check else Icons.Filled.Edit,
                        contentDescription = stringResource(R.string.action_edit)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Type & Status
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AssistChip(
                    onClick = { },
                    label = { Text(npc.creatureType.name) }
                )
                AssistChip(
                    onClick = { },
                    label = { Text(npc.status.name) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Role
            Text(
                text = stringResource(R.string.npc_role),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
            if (isEditing) {
                OutlinedTextField(
                    value = role,
                    onValueChange = { role = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            } else {
                Text(
                    text = role.ifEmpty { "—" },
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Description
            Text(
                text = stringResource(R.string.npc_description),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
            if (isEditing) {
                LinkedTextEditor(
                    value = description,
                    onValueChange = { description = it },
                    linkableItems = linkableItems,
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
            } else {
                if (description.contains("[") || description.contains("#")) {
                    LinkedTextDisplay(
                        text = description,
                        linkableItems = linkableItems,
                        onLinkClick = onLinkClick
                    )
                } else {
                    Text(
                        text = description.ifEmpty { "—" },
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Narrator Notes
            Text(
                text = stringResource(R.string.npc_notes),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
            if (isEditing) {
                LinkedTextEditor(
                    value = notes,
                    onValueChange = { notes = it },
                    linkableItems = linkableItems,
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    placeholder = stringResource(R.string.npc_notes_hint)
                )
            } else {
                if (notes.contains("[") || notes.contains("#")) {
                    LinkedTextDisplay(
                        text = notes,
                        linkableItems = linkableItems,
                        onLinkClick = onLinkClick
                    )
                } else {
                    Text(
                        text = notes.ifEmpty { "—" },
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            if (isEditing) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        onUpdate(
                            npc.copy(
                                name = name,
                                role = role,
                                description = description,
                                narratorNotes = notes,
                                updatedAt = System.currentTimeMillis()
                            )
                        )
                        isEditing = false
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Filled.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.save))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()

            if (npc.characterId != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = { onOpenSheet(npc.characterId) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Filled.Description, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.npc_open_sheet))
                }
            } else {
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(
                    onClick = { onCreateSheet(npc) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Filled.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.npc_create_sheet))
                }
            }
        }
    }
}
