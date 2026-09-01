package com.v20charactermanager.ui.chronicle

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.v20charactermanager.domain.model.*

@Composable
fun ChroniclePeopleTab(
    uiState: ChronicleDetailUiState,
    onCharacterClick: (String) -> Unit,
    onAddCharacter: (String, String, ChronicleMemberRole) -> Unit,
    onRemoveCharacter: (String, String) -> Unit,
    onCreateNpc: (String, String, CreatureType, String) -> Unit,
    onDeleteNpc: (String) -> Unit,
    onUpdateNpc: (NpcEntry) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddCharacterDialog by remember { mutableStateOf(false) }
    var showAddNpcDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // PG section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.storyteller_pg),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = { showAddCharacterDialog = true }) {
                    Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.action_add))
                }
            }
        }

        val pgMembers = uiState.members.filter { it.role == ChronicleMemberRole.PLAYER_CHARACTER }
        val pgCharacters = uiState.availableCharacters.filter { char ->
            pgMembers.any { it.characterId == char.id }
        }

        items(pgCharacters) { character ->
            CharacterLiveCard(
                character = character,
                onClick = { onCharacterClick(character.id) },
                onBloodChange = { /* Handled by live dashboard */ },
                onWillpowerChange = { /* Handled by live dashboard */ }
            )
        }

        // PNG section
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.storyteller_png),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = { showAddNpcDialog = true }) {
                    Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.action_add))
                }
            }
        }

        items(uiState.npcs) { npc ->
            NpcLiveCard(
                npc = npc,
                onClick = { /* TODO: Open NPC detail */ }
            )
        }
    }

    // Add Character Dialog
    if (showAddCharacterDialog) {
        AddCharacterToChronicleDialog(
            availableCharacters = uiState.availableCharacters,
            onAdd = { characterId ->
                uiState.chronicle?.let { chronicle ->
                    onAddCharacter(chronicle.id, characterId, ChronicleMemberRole.PLAYER_CHARACTER)
                }
                showAddCharacterDialog = false
            },
            onDismiss = { showAddCharacterDialog = false }
        )
    }

    // Add NPC Dialog
    if (showAddNpcDialog) {
        var npcName by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showAddNpcDialog = false },
            title = { Text(stringResource(R.string.chronicle_add_npc)) },
            text = {
                OutlinedTextField(
                    value = npcName,
                    onValueChange = { npcName = it },
                    label = { Text(stringResource(R.string.name_hint)) },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (npcName.isNotBlank()) {
                            uiState.chronicle?.let { chronicle ->
                                onCreateNpc(chronicle.id, npcName, CreatureType.MORTAL, "")
                            }
                            showAddNpcDialog = false
                        }
                    }
                ) {
                    Text(stringResource(R.string.action_create))
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddNpcDialog = false }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        )
    }
}

@Composable
private fun AddCharacterToChronicleDialog(
    availableCharacters: List<Character>,
    onAdd: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedCharacterId by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.chronicle_add_member)) },
        text = {
            if (availableCharacters.isEmpty()) {
                Text(stringResource(R.string.chronicle_no_characters_available))
            } else {
                LazyColumn {
                    items(availableCharacters) { character ->
                        ListItem(
                            headlineContent = { Text(character.identity.name) },
                            supportingContent = { Text(character.identity.clan.nameEn) },
                            leadingContent = {
                                RadioButton(
                                    selected = selectedCharacterId == character.id,
                                    onClick = { selectedCharacterId = character.id }
                                )
                            },
                            modifier = Modifier.clickable { selectedCharacterId = character.id }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    selectedCharacterId?.let { onAdd(it) }
                },
                enabled = selectedCharacterId != null
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
