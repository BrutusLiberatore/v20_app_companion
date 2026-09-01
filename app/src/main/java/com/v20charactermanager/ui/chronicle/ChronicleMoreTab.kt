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
    modifier: Modifier = Modifier
) {
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
                    IconButton(onClick = { onDeleteLocation(location.id) }) {
                        Icon(Icons.Filled.Delete, contentDescription = stringResource(R.string.action_delete))
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
            Text(
                text = stringResource(R.string.chronicle_tab_sessions),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        items(uiState.sessions) { session ->
            ListItem(
                headlineContent = {
                    Text("${stringResource(R.string.session_title)} ${session.number}: ${session.title}")
                },
                supportingContent = { Text(session.status.name) },
                leadingContent = { Icon(Icons.Filled.DateRange, contentDescription = null) }
            )
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
}
