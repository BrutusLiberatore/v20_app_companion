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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.v20charactermanager.R
import com.v20charactermanager.domain.model.*

@Composable
fun ChroniclePlotsTab(
    uiState: ChronicleDetailUiState,
    onCreatePlotArc: (String, String, PlotType) -> Unit,
    onDeletePlotArc: (String) -> Unit,
    onUpdatePlotArc: (PlotArc) -> Unit,
    onCreateNote: (String, String) -> Unit,
    onUpdateNote: (ChronicleNote) -> Unit,
    onDeleteNote: (String) -> Unit,
    onCreateCharacterNote: (String, String, String) -> Unit,
    onUpdateCharacterNote: (ChronicleCharacterNote) -> Unit,
    onDeleteCharacterNote: (String) -> Unit,
    onLinkClick: (String, String) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    var showAddPlotDialog by remember { mutableStateOf(false) }
    var showAddNoteDialog by remember { mutableStateOf(false) }
    var showAddCharNoteDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Plot Arcs
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.chronicle_tab_plots),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = { showAddPlotDialog = true }) {
                    Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.action_add))
                }
            }
        }

        items(uiState.plotArcs) { plot ->
            var showEditDialog by remember { mutableStateOf(false) }
            var showDeleteConfirm by remember { mutableStateOf(false) }

            PlotArcCard(
                plot = plot,
                onEdit = { showEditDialog = true },
                onDelete = { showDeleteConfirm = true }
            )

            if (showEditDialog) {
                ItemTextDialog(
                    title = stringResource(R.string.action_edit),
                    initialName = plot.title,
                    nameFieldLabel = stringResource(R.string.title_hint),
                    initialContent = plot.summary,
                    contentLabel = stringResource(R.string.chronicle_description),
                    onConfirm = { title, summary ->
                        onUpdatePlotArc(plot.copy(title = title, summary = summary, updatedAt = System.currentTimeMillis()))
                        showEditDialog = false
                    },
                    onDismiss = { showEditDialog = false }
                )
            }

            if (showDeleteConfirm) {
                ConfirmDeleteDialog(
                    onConfirm = {
                        onDeletePlotArc(plot.id)
                        showDeleteConfirm = false
                    },
                    onDismiss = { showDeleteConfirm = false }
                )
            }
        }

        // Notes section
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.chronicle_tab_notes),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = { showAddNoteDialog = true }) {
                    Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.action_add))
                }
            }
        }

        items(uiState.notes, key = { it.id }) { note ->
            EditableNoteCard(
                note = note,
                linkableItems = uiState.toLinkableItems(),
                onUpdate = onUpdateNote,
                onDelete = { onDeleteNote(note.id) },
                onLinkClick = onLinkClick
            )
        }

        // Character notes
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.chronicle_tab_character_notes),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = { showAddCharNoteDialog = true }) {
                    Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.action_add))
                }
            }
        }

        if (uiState.characterNotes.isEmpty()) {
            item {
                Text(
                    text = stringResource(R.string.chronicle_no_character_notes),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }

        items(uiState.characterNotes, key = { it.id }) { note ->
            var showEditDialog by remember { mutableStateOf(false) }
            var showDeleteConfirm by remember { mutableStateOf(false) }
            val charName = uiState.availableCharacters.find { it.id == note.characterId }?.identity?.name ?: "—"

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = charName,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = note.text,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    IconButton(onClick = { showEditDialog = true }, modifier = Modifier.size(32.dp)) {
                        Icon(
                            Icons.Filled.Edit,
                            contentDescription = stringResource(R.string.action_edit),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(onClick = { showDeleteConfirm = true }, modifier = Modifier.size(32.dp)) {
                        Icon(
                            Icons.Filled.Delete,
                            contentDescription = stringResource(R.string.action_delete),
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            if (showEditDialog) {
                ItemTextDialog(
                    title = stringResource(R.string.action_edit),
                    initialName = note.text,
                    nameFieldLabel = stringResource(R.string.chronicle_note_text),
                    onConfirm = { text, _ ->
                        onUpdateCharacterNote(note.copy(text = text, updatedAt = System.currentTimeMillis()))
                        showEditDialog = false
                    },
                    onDismiss = { showEditDialog = false }
                )
            }

            if (showDeleteConfirm) {
                ConfirmDeleteDialog(
                    onConfirm = {
                        onDeleteCharacterNote(note.id)
                        showDeleteConfirm = false
                    },
                    onDismiss = { showDeleteConfirm = false }
                )
            }
        }

        // Scenes
        if (uiState.scenes.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.storyteller_scene_deck_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            items(uiState.scenes) { scene ->
                SceneCard(scene = scene)
            }
        }
    }

    if (showAddPlotDialog) {
        var plotTitle by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showAddPlotDialog = false },
            title = { Text(stringResource(R.string.chronicle_add_plot)) },
            text = {
                OutlinedTextField(
                    value = plotTitle,
                    onValueChange = { plotTitle = it },
                    label = { Text(stringResource(R.string.title_hint)) },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (plotTitle.isNotBlank()) {
                            uiState.chronicle?.let { chronicle ->
                                onCreatePlotArc(chronicle.id, plotTitle, PlotType.MAIN)
                            }
                            showAddPlotDialog = false
                        }
                    }
                ) {
                    Text(stringResource(R.string.action_create))
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddPlotDialog = false }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        )
    }

    if (showAddNoteDialog) {
        var noteText by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showAddNoteDialog = false },
            title = { Text(stringResource(R.string.chronicle_add_note)) },
            text = {
                LinkedTextEditor(
                    value = noteText,
                    onValueChange = { noteText = it },
                    linkableItems = uiState.toLinkableItems(),
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 4,
                    placeholder = stringResource(R.string.npc_notes_hint)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (noteText.isNotBlank()) {
                            uiState.chronicle?.let { chronicle ->
                                onCreateNote(chronicle.id, noteText)
                            }
                            showAddNoteDialog = false
                        }
                    }
                ) {
                    Text(stringResource(R.string.action_create))
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddNoteDialog = false }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        )
    }

    if (showAddCharNoteDialog) {
        var selectedCharId by remember { mutableStateOf<String?>(null) }
        var noteText by remember { mutableStateOf("") }
        val pgMembers = uiState.members.filter { it.role == ChronicleMemberRole.PLAYER_CHARACTER }
        val pgCharacters = uiState.availableCharacters.filter { char ->
            pgMembers.any { it.characterId == char.id }
        }

        AlertDialog(
            onDismissRequest = { showAddCharNoteDialog = false },
            title = {
                Text(
                    text = stringResource(R.string.chronicle_new_character_note),
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    LazyColumn(modifier = Modifier.heightIn(max = 160.dp)) {
                        items(pgCharacters) { character ->
                            ListItem(
                                headlineContent = { Text(character.identity.name) },
                                supportingContent = {
                                    Text(character.identity.clan.nameEn, style = MaterialTheme.typography.bodySmall)
                                },
                                leadingContent = {
                                    RadioButton(
                                        selected = selectedCharId == character.id,
                                        onClick = { selectedCharId = character.id }
                                    )
                                },
                                modifier = Modifier.clickable { selectedCharId = character.id }
                            )
                        }
                    }
                    if (pgCharacters.isEmpty()) {
                        Text(
                            text = stringResource(R.string.chronicle_no_characters_available),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    OutlinedTextField(
                        value = noteText,
                        onValueChange = { noteText = it },
                        label = { Text(stringResource(R.string.chronicle_note_text)) },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        uiState.chronicle?.let { chronicle ->
                            selectedCharId?.let { charId ->
                                onCreateCharacterNote(chronicle.id, charId, noteText)
                            }
                        }
                        showAddCharNoteDialog = false
                    },
                    enabled = selectedCharId != null && noteText.isNotBlank()
                ) { Text(stringResource(R.string.action_create)) }
            },
            dismissButton = {
                TextButton(onClick = { showAddCharNoteDialog = false }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        )
    }
}

@Composable
private fun EditableNoteCard(
    note: ChronicleNote,
    linkableItems: List<LinkableItem>,
    onUpdate: (ChronicleNote) -> Unit,
    onDelete: () -> Unit,
    onLinkClick: (String, String) -> Unit = { _, _ -> }
) {
    var isEditing by remember { mutableStateOf(false) }
    var editText by remember(note.text) { mutableStateOf(note.text) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault())
                        .format(java.util.Date(note.updatedAt)),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
                Row {
                    IconButton(onClick = { isEditing = !isEditing }, modifier = Modifier.size(32.dp)) {
                        Icon(
                            if (isEditing) Icons.Filled.Check else Icons.Filled.Edit,
                            contentDescription = stringResource(R.string.action_edit),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(onClick = { showDeleteConfirm = true }, modifier = Modifier.size(32.dp)) {
                        Icon(
                            Icons.Filled.Delete,
                            contentDescription = stringResource(R.string.action_delete),
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            if (isEditing) {
                Spacer(modifier = Modifier.height(8.dp))
                LinkedTextEditor(
                    value = editText,
                    onValueChange = { editText = it },
                    linkableItems = linkableItems,
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        onUpdate(note.copy(text = editText, updatedAt = System.currentTimeMillis()))
                        isEditing = false
                    }
                ) {
                    Text(stringResource(R.string.save))
                }
            } else {
                Spacer(modifier = Modifier.height(4.dp))
                if (note.text.contains("[") || note.text.contains("#")) {
                    LinkedTextDisplay(
                        text = note.text,
                        linkableItems = linkableItems,
                        onLinkClick = onLinkClick
                    )
                } else {
                    Text(
                        text = note.text,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text(stringResource(R.string.action_delete)) },
            text = { Text(stringResource(R.string.confirm_delete)) },
            confirmButton = {
                TextButton(onClick = {
                    onDelete()
                    showDeleteConfirm = false
                }) {
                    Text(stringResource(R.string.action_delete), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        )
    }
}

@Composable
private fun PlotArcCard(
    plot: PlotArc,
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = plot.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                    Icon(
                        Icons.Filled.Edit,
                        contentDescription = stringResource(R.string.action_edit),
                        modifier = Modifier.size(18.dp)
                    )
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(
                        Icons.Filled.Delete,
                        contentDescription = stringResource(R.string.action_delete),
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
            if (plot.summary.isNotEmpty()) {
                Text(
                    text = plot.summary,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun SceneCard(scene: ChronicleScene) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = scene.title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
            scene.hook?.takeIf { it.isNotEmpty() }?.let { hook ->
                Text(
                    text = hook,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    maxLines = 2
                )
            }
        }
    }
}
