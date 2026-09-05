package com.v20charactermanager.ui.session

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
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionRecapScreen(
    session: Session,
    events: List<SessionEvent>,
    characters: List<Character>,
    npcs: List<NpcEntry>,
    scenes: List<ChronicleScene>,
    onBack: () -> Unit,
    onCloneSession: () -> Unit,
    onNavigateToSheet: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }
    val duration = remember(session) {
        if (session.realStartDateTime != null && session.realEndDateTime != null) {
            val ms = session.realEndDateTime - session.realStartDateTime
            val hours = TimeUnit.MILLISECONDS.toHours(ms)
            val mins = TimeUnit.MILLISECONDS.toMinutes(ms) % 60
            "${hours}h ${mins}m"
        } else null
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.recap_title, session.number)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.action_back))
                    }
                },
                actions = {
                    IconButton(onClick = onCloneSession) {
                        Icon(Icons.Default.ContentCopy, contentDescription = stringResource(R.string.recap_clone))
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Session Header
            item {
                SessionHeaderCard(session, duration, dateFormat)
            }

            // Participants
            val participantChars = characters.filter { char ->
                session.participantCharacterIds.contains(char.id)
            }
            if (participantChars.isNotEmpty() || npcs.isNotEmpty()) {
                item {
                    RecapSectionTitle(stringResource(R.string.recap_participants), Icons.Default.People)
                }
                items(participantChars) { char ->
                    ParticipantCard(char, onNavigateToSheet)
                }
                if (npcs.isNotEmpty()) {
                    item {
                        NpcSummaryCard(npcs)
                    }
                }
            }

            // Scenes
            val sessionScenes = scenes.filter { scene ->
                events.any { it.sceneId == scene.id } || session.activeSceneId == scene.id
            }
            if (sessionScenes.isNotEmpty()) {
                item {
                    RecapSectionTitle(stringResource(R.string.recap_scenes), Icons.Default.Theaters)
                }
                items(sessionScenes) { scene ->
                    SceneRecapCard(scene, events)
                }
            }

            // Events Timeline
            if (events.isNotEmpty()) {
                item {
                    RecapSectionTitle(stringResource(R.string.recap_events), Icons.Default.Timeline)
                }
                items(events) { event ->
                    EventCard(event, dateFormat)
                }
            }

            // Quick Notes
            val noteEvents = events.filter { it.type == SessionEventType.NOTE_CREATED || it.type == SessionEventType.MANUAL_EVENT }
            if (noteEvents.isNotEmpty()) {
                item {
                    RecapSectionTitle(stringResource(R.string.recap_notes), Icons.Default.Note)
                }
                items(noteEvents) { event ->
                    NoteCard(event)
                }
            }

            // Dice Rolls
            val rollEvents = events.filter { it.type == SessionEventType.ROLL_PERFORMED }
            if (rollEvents.isNotEmpty()) {
                item {
                    RecapSectionTitle(stringResource(R.string.recap_rolls), Icons.Default.Casino)
                }
                items(rollEvents) { event ->
                    RollCard(event)
                }
            }

            // Media Presented
            val mediaEvents = events.filter { it.type == SessionEventType.MEDIA_PRESENTED }
            if (mediaEvents.isNotEmpty()) {
                item {
                    RecapSectionTitle(stringResource(R.string.recap_media), Icons.Default.Image)
                }
                items(mediaEvents) { event ->
                    MediaEventCard(event)
                }
            }

            // Recap Text
            if (session.recap.isNotBlank()) {
                item {
                    RecapSectionTitle(stringResource(R.string.recap_recap), Icons.Default.Summarize)
                }
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Text(
                            text = session.recap,
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            // XP Awarded
            if (session.xpAwarded > 0) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = stringResource(R.string.recap_xp, session.xpAwarded),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SessionHeaderCard(session: Session, duration: String?, dateFormat: SimpleDateFormat) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = session.title.ifEmpty { stringResource(R.string.session_title, session.number) },
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                if (session.realStartDateTime != null) {
                    RecapInfoChip(
                        icon = Icons.Default.CalendarToday,
                        text = dateFormat.format(Date(session.realStartDateTime))
                    )
                }
                if (duration != null) {
                    RecapInfoChip(
                        icon = Icons.Default.Timer,
                        text = duration
                    )
                }
            }
            if (session.inGameDate != null) {
                Spacer(modifier = Modifier.height(4.dp))
                RecapInfoChip(
                    icon = Icons.Default.Flight,
                    text = stringResource(R.string.recap_ingame, session.inGameDate)
                )
            }
        }
    }
}

@Composable
private fun RecapInfoChip(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun RecapSectionTitle(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(top = 8.dp)
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun ParticipantCard(character: Character, onNavigateToSheet: (String) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = { onNavigateToSheet(character.id) }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = character.identity.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${character.identity.clan.nameEn} • ${character.identity.generation}ª Gen",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "🩸 ${character.bloodPool.current}/${character.bloodPool.maximum}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "💪 ${character.willpower.current}/${character.willpower.permanent}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
            )
        }
    }
}

@Composable
private fun NpcSummaryCard(npcs: List<NpcEntry>) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = stringResource(R.string.recap_npcs, npcs.size),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            npcs.take(5).forEach { npc ->
                Text(
                    text = "• ${npc.name}${if (npc.role.isNotEmpty()) " (${npc.role})" else ""}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            if (npcs.size > 5) {
                Text(
                    text = stringResource(R.string.recap_and_more, npcs.size - 5),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@Composable
private fun SceneRecapCard(scene: ChronicleScene, events: List<SessionEvent>) {
    val sceneEvents = events.filter { it.sceneId == scene.id }
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = scene.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${sceneEvents.size} ${stringResource(R.string.recap_events_lowercase)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
            if (!scene.description.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = scene.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    maxLines = 2
                )
            }
        }
    }
}

@Composable
private fun EventCard(event: SessionEvent, dateFormat: SimpleDateFormat) {
    val icon = when (event.type) {
        SessionEventType.SESSION_STARTED -> Icons.Default.PlayArrow
        SessionEventType.SESSION_ENDED -> Icons.Default.Stop
        SessionEventType.SCENE_STARTED, SessionEventType.SCENE_CHANGED -> Icons.Default.Theaters
        SessionEventType.SCENE_ENDED -> Icons.Default.Theaters
        SessionEventType.CHARACTER_BLOOD_CHANGED -> Icons.Default.WaterDrop
        SessionEventType.CHARACTER_WILLPOWER_CHANGED -> Icons.Default.EmojiEvents
        SessionEventType.CHARACTER_HEALTH_CHANGED -> Icons.Default.HealthAndSafety
        SessionEventType.NPC_ADDED_TO_SCENE -> Icons.Default.PersonAdd
        SessionEventType.NPC_REMOVED_FROM_SCENE -> Icons.Default.PersonRemove
        SessionEventType.CLUE_REVEALED -> Icons.Default.Search
        SessionEventType.MEDIA_PRESENTED -> Icons.Default.Image
        SessionEventType.PLOT_STATUS_CHANGED -> Icons.Default.AccountTree
        SessionEventType.ROLL_PERFORMED -> Icons.Default.Casino
        SessionEventType.NOTE_CREATED -> Icons.Default.Note
        SessionEventType.MANUAL_EVENT -> Icons.Default.Event
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                if (!event.description.isNullOrBlank()) {
                    Text(
                        text = event.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
            Text(
                text = dateFormat.format(Date(event.timestamp)),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            )
        }
    }
}

@Composable
private fun NoteCard(event: SessionEvent) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = event.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            if (!event.description.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = event.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
private fun RollCard(event: SessionEvent) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Casino,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                if (!event.description.isNullOrBlank()) {
                    Text(
                        text = event.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
            if (event.metadata != null) {
                Text(
                    text = event.metadata,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun MediaEventCard(event: SessionEvent) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Image,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = event.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
