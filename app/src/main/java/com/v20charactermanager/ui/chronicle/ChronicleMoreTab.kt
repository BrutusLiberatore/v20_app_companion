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
fun ItemTextDialog(
    title: String,
    initialName: String = "",
    nameFieldLabel: String,
    initialContent: String? = null,
    contentLabel: String? = null,
    onConfirm: (String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    var content by remember { mutableStateOf(initialContent ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(nameFieldLabel) },
                    modifier = Modifier.fillMaxWidth()
                )
                if (initialContent != null && contentLabel != null) {
                    OutlinedTextField(
                        value = content,
                        onValueChange = { content = it },
                        label = { Text(contentLabel) },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { if (name.isNotBlank()) onConfirm(name.trim(), content) },
                enabled = name.isNotBlank()
            ) { Text(stringResource(R.string.save)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.action_cancel)) }
        }
    )
}

@Composable
fun ConfirmDeleteDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.action_delete)) },
        text = { Text(stringResource(R.string.confirm_delete)) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(R.string.action_delete), color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.action_cancel)) }
        }
    )
}

@Composable
fun ChronicleMoreTab(
    uiState: ChronicleDetailUiState,
    onNavigateToDice: () -> Unit,
    onUpdateChronicle: (Chronicle) -> Unit,
    onCreateLocation: (String, String) -> Unit,
    onDeleteLocation: (String) -> Unit,
    onUpdateLocation: (ChronicleLocation) -> Unit,
    onLocationImageClick: (String, String) -> Unit,
    onCreateFaction: (String, String) -> Unit,
    onDeleteFaction: (String) -> Unit,
    onUpdateFaction: (Faction) -> Unit,
    onCreateSecret: (String, String, String) -> Unit,
    onDeleteSecret: (String) -> Unit,
    onUpdateSecret: (Secret) -> Unit,
    onCreateClue: (String, String, String?) -> Unit,
    onDeleteClue: (String) -> Unit,
    onUpdateClue: (Clue) -> Unit,
    onCreateEvent: (String, String) -> Unit,
    onDeleteEvent: (String) -> Unit,
    onUpdateEvent: (ChronicleEvent) -> Unit,
    onCreateSession: (String, String) -> Unit,
    onUpdateSession: (Session) -> Unit,
    onDeleteSession: (String) -> Unit,
    onViewRecap: (String, String) -> Unit,
    onCloneSession: (Session) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddSessionDialog by remember { mutableStateOf(false) }
    var showAddLocationDialog by remember { mutableStateOf(false) }
    var showAddFactionDialog by remember { mutableStateOf(false) }
    var showAddSecretDialog by remember { mutableStateOf(false) }
    var showAddClueDialog by remember { mutableStateOf(false) }
    var showAddEventDialog by remember { mutableStateOf(false) }
    var showEditChronicleDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Chronicle name (tap to rename)
        uiState.chronicle?.let { chronicle ->
            item {
                ListItem(
                    headlineContent = {
                        Text(
                            text = chronicle.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    supportingContent = if (chronicle.description.isNotBlank()) {
                        { Text(chronicle.description, maxLines = 2, overflow = TextOverflow.Ellipsis) }
                    } else null,
                    leadingContent = { Icon(Icons.Filled.Book, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                    trailingContent = {
                        IconButton(onClick = { showEditChronicleDialog = true }) {
                            Icon(Icons.Filled.Edit, contentDescription = stringResource(R.string.action_edit))
                        }
                    }
                )
            }
        }

        // Tools
        item {
            Text(
                text = stringResource(R.string.home_tools),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        item {
            ListItem(
                headlineContent = { Text(stringResource(R.string.nav_dice)) },
                leadingContent = { Icon(Icons.Filled.Casino, contentDescription = null) },
                modifier = Modifier.clickable { onNavigateToDice() }
            )
        }

        // Locations
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.chronicle_tab_locations),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = { showAddLocationDialog = true }) {
                    Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.action_add))
                }
            }
        }

        items(uiState.locations) { location ->
            var showEditDialog by remember { mutableStateOf(false) }

            ListItem(
                headlineContent = { Text(location.name) },
                supportingContent = if (location.description.isNotBlank()) {
                    { Text(location.description, maxLines = 1, overflow = TextOverflow.Ellipsis) }
                } else null,
                leadingContent = { Icon(Icons.Filled.LocationOn, contentDescription = null) },
                trailingContent = {
                    Row {
                        IconButton(onClick = {
                            uiState.chronicle?.let { chronicle ->
                                onLocationImageClick(chronicle.id, location.id)
                            }
                        }) {
                            Icon(Icons.Filled.Image, contentDescription = stringResource(R.string.location_import_image))
                        }
                        IconButton(onClick = { showEditDialog = true }) {
                            Icon(Icons.Filled.Edit, contentDescription = stringResource(R.string.action_edit))
                        }
                        IconButton(onClick = { onDeleteLocation(location.id) }) {
                            Icon(Icons.Filled.Delete, contentDescription = stringResource(R.string.action_delete))
                        }
                    }
                }
            )

            if (showEditDialog) {
                ItemTextDialog(
                    title = stringResource(R.string.action_edit),
                    initialName = location.name,
                    nameFieldLabel = stringResource(R.string.name_hint),
                    initialContent = location.description,
                    contentLabel = stringResource(R.string.chronicle_description),
                    onConfirm = { name, description ->
                        onUpdateLocation(location.copy(name = name, description = description, updatedAt = System.currentTimeMillis()))
                        showEditDialog = false
                    },
                    onDismiss = { showEditDialog = false }
                )
            }
        }

        // Factions
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.chronicle_tab_factions),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = { showAddFactionDialog = true }) {
                    Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.action_add))
                }
            }
        }

        items(uiState.factions) { faction ->
            var showEditDialog by remember { mutableStateOf(false) }

            ListItem(
                headlineContent = { Text(faction.name) },
                supportingContent = if (faction.description.isNotBlank()) {
                    { Text(faction.description, maxLines = 1, overflow = TextOverflow.Ellipsis) }
                } else null,
                leadingContent = { Icon(Icons.Filled.Group, contentDescription = null) },
                trailingContent = {
                    Row {
                        IconButton(onClick = { showEditDialog = true }) {
                            Icon(Icons.Filled.Edit, contentDescription = stringResource(R.string.action_edit))
                        }
                        IconButton(onClick = { onDeleteFaction(faction.id) }) {
                            Icon(Icons.Filled.Delete, contentDescription = stringResource(R.string.action_delete))
                        }
                    }
                }
            )

            if (showEditDialog) {
                ItemTextDialog(
                    title = stringResource(R.string.action_edit),
                    initialName = faction.name,
                    nameFieldLabel = stringResource(R.string.name_hint),
                    initialContent = faction.description,
                    contentLabel = stringResource(R.string.chronicle_description),
                    onConfirm = { name, description ->
                        onUpdateFaction(faction.copy(name = name, description = description, updatedAt = System.currentTimeMillis()))
                        showEditDialog = false
                    },
                    onDismiss = { showEditDialog = false }
                )
            }
        }

        // Sessions
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.chronicle_tab_sessions),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = { showAddSessionDialog = true }) {
                    Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.action_add))
                }
            }
        }

        items(uiState.sessions, key = { it.id }) { session ->
            var showEditDialog by remember { mutableStateOf(false) }
            var showDeleteConfirm by remember { mutableStateOf(false) }

            ListItem(
                headlineContent = {
                    Text("${stringResource(R.string.session_title)} ${session.number}: ${session.title}")
                },
                supportingContent = {
                    Text(
                        text = when (session.status) {
                            SessionStatus.PLANNED -> stringResource(R.string.session_status_planned)
                            SessionStatus.ACTIVE -> stringResource(R.string.session_status_active)
                            SessionStatus.COMPLETED -> stringResource(R.string.session_status_completed)
                            else -> session.status.name
                        },
                        color = when (session.status) {
                            SessionStatus.ACTIVE -> MaterialTheme.colorScheme.primary
                            SessionStatus.COMPLETED -> MaterialTheme.colorScheme.outline
                            else -> MaterialTheme.colorScheme.onSurface
                        }
                    )
                },
                leadingContent = { Icon(Icons.Filled.DateRange, contentDescription = null) },
                trailingContent = {
                    Row {
                        if (session.status == SessionStatus.COMPLETED) {
                            IconButton(onClick = { uiState.chronicle?.let { c -> onViewRecap(session.id, c.id) } }, modifier = Modifier.size(32.dp)) {
                                Icon(Icons.Filled.Assessment, contentDescription = stringResource(R.string.recap_view), modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.tertiary)
                            }
                            IconButton(onClick = { onCloneSession(session) }, modifier = Modifier.size(32.dp)) {
                                Icon(Icons.Filled.ContentCopy, contentDescription = stringResource(R.string.recap_clone), modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.secondary)
                            }
                        }
                        IconButton(onClick = { showEditDialog = true }, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Filled.Edit, contentDescription = stringResource(R.string.action_edit), modifier = Modifier.size(18.dp))
                        }
                        IconButton(onClick = { showDeleteConfirm = true }, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Filled.Delete, contentDescription = stringResource(R.string.action_delete), modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            )

            if (showEditDialog) {
                var editTitle by remember { mutableStateOf(session.title) }
                AlertDialog(
                    onDismissRequest = { showEditDialog = false },
                    title = { Text(stringResource(R.string.session_title) + " #${session.number}") },
                    text = {
                        OutlinedTextField(
                            value = editTitle,
                            onValueChange = { editTitle = it },
                            label = { Text(stringResource(R.string.title_hint)) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            onUpdateSession(session.copy(title = editTitle, updatedAt = System.currentTimeMillis()))
                            showEditDialog = false
                        }) { Text(stringResource(R.string.save)) }
                    },
                    dismissButton = {
                        TextButton(onClick = { showEditDialog = false }) { Text(stringResource(R.string.action_cancel)) }
                    }
                )
            }

            if (showDeleteConfirm) {
                AlertDialog(
                    onDismissRequest = { showDeleteConfirm = false },
                    title = { Text(stringResource(R.string.action_delete)) },
                    text = { Text(stringResource(R.string.confirm_delete)) },
                    confirmButton = {
                        TextButton(onClick = { onDeleteSession(session.id); showDeleteConfirm = false }) {
                            Text(stringResource(R.string.action_delete), color = MaterialTheme.colorScheme.error)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteConfirm = false }) { Text(stringResource(R.string.action_cancel)) }
                    }
                )
            }
        }

        // Secrets
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.chronicle_tab_secrets),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = { showAddSecretDialog = true }) {
                    Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.action_add))
                }
            }
        }

        items(uiState.secrets) { secret ->
            var showEditDialog by remember { mutableStateOf(false) }
            var showDeleteConfirm by remember { mutableStateOf(false) }

            ListItem(
                headlineContent = { Text(secret.title) },
                supportingContent = if (secret.content.isNotBlank()) {
                    { Text(secret.content, maxLines = 1, overflow = TextOverflow.Ellipsis) }
                } else null,
                leadingContent = { Icon(Icons.Filled.Lock, contentDescription = null) },
                trailingContent = {
                    Row {
                        IconButton(onClick = { showEditDialog = true }) {
                            Icon(Icons.Filled.Edit, contentDescription = stringResource(R.string.action_edit))
                        }
                        IconButton(onClick = { showDeleteConfirm = true }) {
                            Icon(Icons.Filled.Delete, contentDescription = stringResource(R.string.action_delete), tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            )

            if (showEditDialog) {
                ItemTextDialog(
                    title = stringResource(R.string.action_edit),
                    initialName = secret.title,
                    nameFieldLabel = stringResource(R.string.title_hint),
                    initialContent = secret.content,
                    contentLabel = stringResource(R.string.chronicle_description),
                    onConfirm = { title, content ->
                        onUpdateSecret(secret.copy(title = title, content = content, updatedAt = System.currentTimeMillis()))
                        showEditDialog = false
                    },
                    onDismiss = { showEditDialog = false }
                )
            }

            if (showDeleteConfirm) {
                ConfirmDeleteDialog(
                    onConfirm = {
                        onDeleteSecret(secret.id)
                        showDeleteConfirm = false
                    },
                    onDismiss = { showDeleteConfirm = false }
                )
            }
        }

        // Clues
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.chronicle_tab_clues),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = { showAddClueDialog = true }) {
                    Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.action_add))
                }
            }
        }

        items(uiState.clues) { clue ->
            var showEditDialog by remember { mutableStateOf(false) }
            var showDeleteConfirm by remember { mutableStateOf(false) }

            ListItem(
                headlineContent = { Text(clue.title) },
                supportingContent = if (!clue.content.isNullOrBlank()) {
                    { Text(clue.content ?: "", maxLines = 1, overflow = TextOverflow.Ellipsis) }
                } else null,
                leadingContent = { Icon(Icons.Filled.Search, contentDescription = null) },
                trailingContent = {
                    Row {
                        IconButton(onClick = { showEditDialog = true }) {
                            Icon(Icons.Filled.Edit, contentDescription = stringResource(R.string.action_edit))
                        }
                        IconButton(onClick = { showDeleteConfirm = true }) {
                            Icon(Icons.Filled.Delete, contentDescription = stringResource(R.string.action_delete), tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            )

            if (showEditDialog) {
                ItemTextDialog(
                    title = stringResource(R.string.action_edit),
                    initialName = clue.title,
                    nameFieldLabel = stringResource(R.string.title_hint),
                    initialContent = clue.content ?: "",
                    contentLabel = stringResource(R.string.chronicle_description),
                    onConfirm = { title, content ->
                        onUpdateClue(clue.copy(title = title, content = content, updatedAt = System.currentTimeMillis()))
                        showEditDialog = false
                    },
                    onDismiss = { showEditDialog = false }
                )
            }

            if (showDeleteConfirm) {
                ConfirmDeleteDialog(
                    onConfirm = {
                        onDeleteClue(clue.id)
                        showDeleteConfirm = false
                    },
                    onDismiss = { showDeleteConfirm = false }
                )
            }
        }

        // Events
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.chronicle_tab_events),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = { showAddEventDialog = true }) {
                    Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.action_add))
                }
            }
        }

        if (uiState.events.isEmpty()) {
            item {
                Text(
                    text = stringResource(R.string.chronicle_no_events),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }

        items(uiState.events) { event ->
            var showEditDialog by remember { mutableStateOf(false) }
            var showDeleteConfirm by remember { mutableStateOf(false) }

            ListItem(
                headlineContent = { Text(event.title) },
                supportingContent = if (!event.description.isNullOrBlank()) {
                    { Text(event.description ?: "", maxLines = 1, overflow = TextOverflow.Ellipsis) }
                } else null,
                leadingContent = { Icon(Icons.Filled.Warning, contentDescription = null) },
                trailingContent = {
                    Row {
                        IconButton(onClick = { showEditDialog = true }) {
                            Icon(Icons.Filled.Edit, contentDescription = stringResource(R.string.action_edit))
                        }
                        IconButton(onClick = { showDeleteConfirm = true }) {
                            Icon(Icons.Filled.Delete, contentDescription = stringResource(R.string.action_delete), tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            )

            if (showEditDialog) {
                ItemTextDialog(
                    title = stringResource(R.string.chronicle_event_edit),
                    initialName = event.title,
                    nameFieldLabel = stringResource(R.string.chronicle_event_title),
                    initialContent = event.description ?: "",
                    contentLabel = stringResource(R.string.chronicle_description),
                    onConfirm = { title, description ->
                        onUpdateEvent(event.copy(title = title, description = description))
                        showEditDialog = false
                    },
                    onDismiss = { showEditDialog = false }
                )
            }

            if (showDeleteConfirm) {
                ConfirmDeleteDialog(
                    onConfirm = {
                        onDeleteEvent(event.id)
                        showDeleteConfirm = false
                    },
                    onDismiss = { showDeleteConfirm = false }
                )
            }
        }
    }

    if (showAddSessionDialog) {
        var sessionTitle by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showAddSessionDialog = false },
            title = { Text(stringResource(R.string.session_create)) },
            text = {
                OutlinedTextField(
                    value = sessionTitle,
                    onValueChange = { sessionTitle = it },
                    label = { Text(stringResource(R.string.title_hint)) },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (sessionTitle.isNotBlank()) {
                        uiState.chronicle?.let { onCreateSession(it.id, sessionTitle) }
                        showAddSessionDialog = false
                    }
                }) { Text(stringResource(R.string.action_create)) }
            },
            dismissButton = {
                TextButton(onClick = { showAddSessionDialog = false }) { Text(stringResource(R.string.action_cancel)) }
            }
        )
    }

    if (showEditChronicleDialog) {
        uiState.chronicle?.let { chronicle ->
            ItemTextDialog(
                title = stringResource(R.string.action_edit),
                initialName = chronicle.name,
                nameFieldLabel = stringResource(R.string.name_hint),
                initialContent = chronicle.description,
                contentLabel = stringResource(R.string.chronicle_description),
                onConfirm = { name, description ->
                    onUpdateChronicle(chronicle.copy(name = name, description = description, updatedAt = System.currentTimeMillis()))
                    showEditChronicleDialog = false
                },
                onDismiss = { showEditChronicleDialog = false }
            )
        }
    }

    if (showAddLocationDialog) {
        uiState.chronicle?.let { chronicle ->
            ItemTextDialog(
                title = stringResource(R.string.chronicle_add_location),
                nameFieldLabel = stringResource(R.string.name_hint),
                onConfirm = { name, _ ->
                    onCreateLocation(chronicle.id, name)
                    showAddLocationDialog = false
                },
                onDismiss = { showAddLocationDialog = false }
            )
        }
    }

    if (showAddFactionDialog) {
        uiState.chronicle?.let { chronicle ->
            ItemTextDialog(
                title = stringResource(R.string.chronicle_add_faction),
                nameFieldLabel = stringResource(R.string.name_hint),
                onConfirm = { name, _ ->
                    onCreateFaction(chronicle.id, name)
                    showAddFactionDialog = false
                },
                onDismiss = { showAddFactionDialog = false }
            )
        }
    }

    if (showAddSecretDialog) {
        uiState.chronicle?.let { chronicle ->
            ItemTextDialog(
                title = stringResource(R.string.chronicle_add_secret),
                nameFieldLabel = stringResource(R.string.title_hint),
                initialContent = "",
                contentLabel = stringResource(R.string.chronicle_description),
                onConfirm = { title, content ->
                    onCreateSecret(chronicle.id, title, content)
                    showAddSecretDialog = false
                },
                onDismiss = { showAddSecretDialog = false }
            )
        }
    }

    if (showAddClueDialog) {
        uiState.chronicle?.let { chronicle ->
            ItemTextDialog(
                title = stringResource(R.string.chronicle_add_clue),
                nameFieldLabel = stringResource(R.string.title_hint),
                initialContent = "",
                contentLabel = stringResource(R.string.chronicle_description),
                onConfirm = { title, content ->
                    onCreateClue(chronicle.id, title, content)
                    showAddClueDialog = false
                },
                onDismiss = { showAddClueDialog = false }
            )
        }
    }

    if (showAddEventDialog) {
        uiState.chronicle?.let { chronicle ->
            ItemTextDialog(
                title = stringResource(R.string.chronicle_new_event),
                nameFieldLabel = stringResource(R.string.chronicle_event_title),
                onConfirm = { title, _ ->
                    onCreateEvent(chronicle.id, title)
                    showAddEventDialog = false
                },
                onDismiss = { showAddEventDialog = false }
            )
        }
    }
}
