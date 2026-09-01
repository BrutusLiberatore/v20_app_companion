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

data class SearchResult(
    val entityType: String,
    val entityId: String,
    val title: String,
    val subtitle: String,
    val icon: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChronicleSearchScreen(
    uiState: ChronicleDetailUiState,
    onEntityClick: (String, String) -> Unit,
    onBack: () -> Unit
) {
    var query by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("ALL") }

    val allResults = remember(uiState) {
        val results = mutableListOf<SearchResult>()

        uiState.members.filter { it.role == ChronicleMemberRole.PLAYER_CHARACTER }.forEach { m ->
            uiState.availableCharacters.find { it.id == m.characterId }?.let {
                results.add(SearchResult("PG", it.id, it.identity.name, "${it.identity.clan} Gen.${it.identity.generation}", "PG"))
            }
        }
        uiState.npcs.forEach {
            results.add(SearchResult("NPC", it.id, it.name, it.role, "NPC"))
        }
        uiState.locations.forEach {
            results.add(SearchResult("LUOGHI", it.id, it.name, it.typeId, "LUOGHI"))
        }
        uiState.plotArcs.forEach {
            results.add(SearchResult("TRAME", it.id, it.title, it.summary.take(60), "TRAME"))
        }
        uiState.scenes.forEach {
            results.add(SearchResult("SCENE", it.id, it.title, it.hook?.take(60) ?: "", "SCENE"))
        }
        uiState.sessions.forEach {
            results.add(SearchResult("SESSIONI", it.id, "Sessione #${it.number}", it.title, "SESSIONI"))
        }
        uiState.secrets.forEach {
            results.add(SearchResult("SEGRETI", it.id, it.title, it.content.take(60), "SEGRETI"))
        }
        uiState.clues.forEach {
            results.add(SearchResult("INDIZI", it.id, it.title, it.content?.take(60) ?: "", "INDIZI"))
        }
        uiState.notes.forEach {
            results.add(SearchResult("NOTE", it.id, "Nota", it.text.take(60), "NOTE"))
        }
        uiState.factions.forEach {
            results.add(SearchResult("FAZIONI", it.id, it.name, it.description.take(60), "FAZIONI"))
        }
        uiState.events.forEach {
            results.add(SearchResult("EVENTI", it.id, it.title, it.description?.take(60) ?: "", "EVENTI"))
        }
        results
    }

    val filteredResults = allResults.filter { result ->
        val matchesQuery = query.isEmpty() ||
                result.title.contains(query, ignoreCase = true) ||
                result.subtitle.contains(query, ignoreCase = true)
        val matchesFilter = selectedFilter == "ALL" || result.entityType == selectedFilter
        matchesQuery && matchesFilter
    }

    val filters = listOf(
        "ALL" to stringResource(R.string.media_all),
        "PG" to "PG",
        "NPC" to "NPC",
        "LUOGHI" to stringResource(R.string.media_locations),
        "TRAME" to stringResource(R.string.chronicle_tab_plots),
        "SESSIONI" to stringResource(R.string.chronicle_tab_sessions),
        "SEGRETI" to stringResource(R.string.chronicle_tab_secrets),
        "INDIZI" to stringResource(R.string.chronicle_tab_clues),
        "NOTE" to stringResource(R.string.chronicle_tab_notes),
        "FAZIONI" to stringResource(R.string.chronicle_tab_factions)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    OutlinedTextField(
                        value = query,
                        onValueChange = { query = it },
                        placeholder = { Text(stringResource(R.string.search_hint)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = MaterialTheme.colorScheme.surface,
                            focusedBorderColor = MaterialTheme.colorScheme.surface
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = stringResource(R.string.action_back))
                    }
                },
                actions = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { query = "" }) {
                            Icon(Icons.Filled.Clear, contentDescription = stringResource(R.string.action_clear))
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            androidx.compose.foundation.lazy.LazyRow(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filters.size) { index ->
                    val (filter, label) = filters[index]
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter },
                        label = { Text(label, style = MaterialTheme.typography.labelSmall) }
                    )
                }
            }

            if (filteredResults.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Filled.Search,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = stringResource(R.string.search_no_results),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    items(filteredResults, key = { "${it.entityType}_${it.entityId}" }) { result ->
                        ListItem(
                            headlineContent = { Text(result.title, fontWeight = FontWeight.Bold) },
                            supportingContent = { Text(result.subtitle, maxLines = 1) },
                            leadingContent = {
                                Icon(
                                    when (result.entityType) {
                                        "PG" -> Icons.Filled.Person
                                        "NPC" -> Icons.Filled.Face
                                        "LUOGHI" -> Icons.Filled.LocationOn
                                        "TRAME" -> Icons.Filled.AccountTree
                                        "SCENE" -> Icons.Filled.Theaters
                                        "SESSIONI" -> Icons.Filled.DateRange
                                        "SEGRETI" -> Icons.Filled.Lock
                                        "INDIZI" -> Icons.Filled.Search
                                        "NOTE" -> Icons.Filled.Note
                                        "FAZIONI" -> Icons.Filled.Groups
                                        "EVENTI" -> Icons.Filled.Event
                                        else -> Icons.Filled.Article
                                    },
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            },
                            trailingContent = {
                                AssistChip(
                                    onClick = { },
                                    label = { Text(result.entityType, style = MaterialTheme.typography.labelSmall) }
                                )
                            },
                            modifier = Modifier.clickable {
                                onEntityClick(result.entityType, result.entityId)
                            }
                        )
                    }
                }
            }
        }
    }
}
