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

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
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
            Text(
                text = stringResource(R.string.chronicle_tab_locations),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        items(uiState.locations) { location ->
            ListItem(
                headlineContent = { Text(location.name) },
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
                        IconButton(onClick = { onDeleteLocation(location.id) }) {
                            Icon(Icons.Filled.Delete, contentDescription = stringResource(R.string.action_delete))
                        }
                    }
                }
            )
        }

        // Factions
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.chronicle_tab_factions),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        items(uiState.factions) { faction ->
            ListItem(
                headlineContent = { Text(faction.name) },
                leadingContent = { Icon(Icons.Filled.Group, contentDescription = null) },
                trailingContent = {
                    IconButton(onClick = { onDeleteFaction(faction.id) }) {
                        Icon(Icons.Filled.Delete, contentDescription = stringResource(R.string.action_delete))
                    }
                }
            )
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
        if (uiState.secrets.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.chronicle_tab_secrets),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            items(uiState.secrets) { secret ->
                ListItem(
                    headlineContent = { Text(secret.title) },
                    leadingContent = { Icon(Icons.Filled.Lock, contentDescription = null) }
                )
            }
        }

        // Clues
        if (uiState.clues.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.chronicle_tab_clues),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            items(uiState.clues) { clue ->
                ListItem(
                    headlineContent = { Text(clue.title) },
                    leadingContent = { Icon(Icons.Filled.Search, contentDescription = null) }
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
}
