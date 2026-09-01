package com.v20charactermanager.ui.chronicle

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StorytellerLiveScreen(
    chronicle: Chronicle?,
    session: Session?,
    scenes: List<ChronicleScene>,
    members: List<ChronicleMember>,
    npcs: List<NpcEntry>,
    availableCharacters: List<Character>,
    quickNotes: List<QuickNote>,
    sessionEvents: List<SessionEvent>,
    onStartSession: (Session) -> Unit,
    onEndSession: (Session) -> Unit,
    onCharacterClick: (String) -> Unit,
    onCharacterBloodChange: (Character, Int) -> Unit,
    onCharacterWillpowerChange: (Character, Int) -> Unit,
    onNpcClick: (NpcEntry) -> Unit,
    onOpenScene: (ChronicleScene) -> Unit,
    onChangeScene: () -> Unit,
    onDiceClick: () -> Unit,
    onQuickNote: (String) -> Unit,
    onEventClick: () -> Unit,
    onMediaClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showNewNoteDialog by remember { mutableStateOf(false) }
    var showNewEventDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Session header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = chronicle?.name ?: "",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    if (session != null) {
                        Text(
                            text = "${stringResource(R.string.session_title)} ${session.number}" +
                                    if (session.status == SessionStatus.ACTIVE) " \u2022 LIVE" else "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (session.status == SessionStatus.ACTIVE)
                                MaterialTheme.colorScheme.error
                            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }

                if (session != null) {
                    if (session.status == SessionStatus.PLANNED) {
                        Button(onClick = { onStartSession(session) }) {
                            Icon(Icons.Filled.PlayArrow, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(stringResource(R.string.storyteller_start_session))
                        }
                    } else if (session.status == SessionStatus.ACTIVE) {
                        Button(
                            onClick = { onEndSession(session) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Icon(Icons.Filled.Stop, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(stringResource(R.string.storyteller_end_session))
                        }
                    }
                }
            }
        }

        // No session warning
        if (session == null) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Filled.Warning,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = stringResource(R.string.storyteller_no_active_session),
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
        }

        // Active Scene
        if (session?.status == SessionStatus.ACTIVE) {
            item {
                val activeScene = scenes.find { it.id == session.activeSceneId }
                ActiveSceneCard(
                    session = session,
                    scene = activeScene,
                    onOpenClick = { activeScene?.let { onOpenScene(it) } },
                    onChangeClick = onChangeScene
                )
            }
        }

        // PG Section
        if (members.isNotEmpty()) {
            item {
                SectionHeader(
                    title = stringResource(R.string.storyteller_pg),
                    icon = Icons.Filled.People
                )
            }

            val pgCharacters = availableCharacters.filter { char ->
                members.any { it.characterId == char.id && it.role == ChronicleMemberRole.PLAYER_CHARACTER }
            }

            items(pgCharacters) { character ->
                CharacterLiveCard(
                    character = character,
                    onClick = { onCharacterClick(character.id) },
                    onBloodChange = { delta -> onCharacterBloodChange(character, delta) },
                    onWillpowerChange = { delta -> onCharacterWillpowerChange(character, delta) }
                )
            }
        }

        // PNG Section
        if (npcs.isNotEmpty()) {
            item {
                SectionHeader(
                    title = stringResource(R.string.storyteller_png),
                    icon = Icons.Filled.Person
                )
            }

            items(npcs) { npc ->
                NpcLiveCard(
                    npc = npc,
                    onClick = { onNpcClick(npc) }
                )
            }
        }

        // Quick Actions
        if (session?.status == SessionStatus.ACTIVE) {
            item {
                QuickActionBar(
                    onDiceClick = onDiceClick,
                    onNoteClick = { showNewNoteDialog = true },
                    onEventClick = { showNewEventDialog = true }
                )
            }
        }
    }

    // Quick Note Dialog
    if (showNewNoteDialog) {
        var noteText by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showNewNoteDialog = false },
            title = { Text(stringResource(R.string.storyteller_quick_note)) },
            text = {
                OutlinedTextField(
                    value = noteText,
                    onValueChange = { noteText = it },
                    label = { Text(stringResource(R.string.storyteller_note_hint)) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (noteText.isNotBlank()) {
                            onQuickNote(noteText)
                            showNewNoteDialog = false
                        }
                    }
                ) {
                    Text(stringResource(R.string.save))
                }
            },
            dismissButton = {
                TextButton(onClick = { showNewNoteDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    // Quick Event Dialog
    if (showNewEventDialog) {
        var eventTitle by remember { mutableStateOf("") }
        var eventDesc by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showNewEventDialog = false },
            title = { Text(stringResource(R.string.storyteller_event)) },
            text = {
                Column {
                    OutlinedTextField(
                        value = eventTitle,
                        onValueChange = { eventTitle = it },
                        label = { Text(stringResource(R.string.title_hint)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = eventDesc,
                        onValueChange = { eventDesc = it },
                        label = { Text(stringResource(R.string.storyteller_description)) },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (eventTitle.isNotBlank()) {
                            onEventClick()
                            showNewEventDialog = false
                        }
                    }
                ) {
                    Text(stringResource(R.string.save))
                }
            },
            dismissButton = {
                TextButton(onClick = { showNewEventDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

@Composable
private fun SectionHeader(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(top = 8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
    }
}
