package com.v20charactermanager.ui.chronicle

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.v20charactermanager.R
import com.v20charactermanager.data.local.ChronicleImageManager
import com.v20charactermanager.domain.model.*
import com.v20charactermanager.domain.model.SecretStatus
import com.v20charactermanager.domain.model.ClueStatus
import com.v20charactermanager.domain.model.PlotStatus
import com.v20charactermanager.ui.components.V20BloodButton
import com.v20charactermanager.ui.components.V20ControlButton
import com.v20charactermanager.ui.components.V20IvoryButton
import com.v20charactermanager.ui.theme.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChronicleDetailScreen(
    uiState: ChronicleDetailUiState,
    onBack: () -> Unit,
    onTabSelected: (Int) -> Unit,
    onAddCharacter: (String, String, ChronicleMemberRole) -> Unit,
    onRemoveCharacter: (String, String) -> Unit,
    onCreateSession: (String, String) -> Unit,
    onUpdateSession: (Session) -> Unit,
    onDeleteSession: (String) -> Unit,
    onCreateNote: (String, String) -> Unit,
    onUpdateNote: (ChronicleNote) -> Unit,
    onDeleteNote: (String) -> Unit,
    onCreateCharacterNote: (String, String, String) -> Unit,
    onUpdateCharacterNote: (ChronicleCharacterNote) -> Unit,
    onDeleteCharacterNote: (String) -> Unit,
    onUpdateChronicle: (Chronicle) -> Unit,
    onNavigateToCharacter: (String) -> Unit,
    onNavigateToDice: () -> Unit,
    onCreateNpc: (String, String, CreatureType, String) -> Unit,
    onDeleteNpc: (String) -> Unit,
    onUpdateNpc: (NpcEntry) -> Unit,
    onCreateLocation: (String, String) -> Unit,
    onDeleteLocation: (String) -> Unit,
    onUpdateLocation: (ChronicleLocation) -> Unit,
    onCreateFaction: (String, String) -> Unit,
    onDeleteFaction: (String) -> Unit,
    onUpdateFaction: (Faction) -> Unit,
    onCreatePlotArc: (String, String, PlotType) -> Unit,
    onDeletePlotArc: (String) -> Unit,
    onUpdatePlotArc: (PlotArc) -> Unit,
    onCreateSecret: (String, String, String) -> Unit,
    onDeleteSecret: (String) -> Unit,
    onUpdateSecret: (Secret) -> Unit,
    onCreateClue: (String, String, String?) -> Unit,
    onDeleteClue: (String) -> Unit,
    onUpdateClue: (Clue) -> Unit,
    onCreateEvent: (String, String) -> Unit,
    onDeleteEvent: (String) -> Unit,
    onUpdateEvent: (ChronicleEvent) -> Unit,
    onOpenMediaLibrary: (String) -> Unit,
    onLinkClick: (String, String) -> Unit = { _, _ -> }
) {
    val chronicle = uiState.chronicle ?: return
    val tabs = listOf(
        stringResource(R.string.chronicle_tab_members),
        stringResource(R.string.chronicle_tab_npcs),
        stringResource(R.string.chronicle_tab_sessions),
        stringResource(R.string.chronicle_tab_locations),
        stringResource(R.string.chronicle_tab_factions),
        stringResource(R.string.chronicle_tab_plots),
        stringResource(R.string.chronicle_tab_secrets),
        stringResource(R.string.chronicle_tab_events),
        stringResource(R.string.media_library),
        stringResource(R.string.chronicle_tab_notes),
        stringResource(R.string.chronicle_tab_character_notes)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = chronicle.name,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, stringResource(R.string.action_back))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = V20Green,
                    titleContentColor = V20Ink,
                    navigationIconContentColor = V20Ink
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            TabRow(
                selectedTabIndex = uiState.selectedTab,
                containerColor = V20Surface2,
                contentColor = V20GreenBright
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = uiState.selectedTab == index,
                        onClick = { onTabSelected(index) },
                        text = {
                            Text(
                                text = title,
                                color = if (uiState.selectedTab == index) V20GreenBright else V20InkDim
                            )
                        }
                    )
                }
            }

            when (uiState.selectedTab) {
                0 -> MembersTab(
                    chronicle = chronicle,
                    members = uiState.members,
                    availableCharacters = uiState.availableCharacters,
                    onAddCharacter = onAddCharacter,
                    onRemoveCharacter = onRemoveCharacter,
                    onNavigateToCharacter = onNavigateToCharacter
                )
                1 -> NpcsTab(
                    chronicleId = chronicle.id,
                    npcs = uiState.npcs,
                    onCreateNpc = { name, creatureType, role ->
                        onCreateNpc(chronicle.id, name, creatureType, role)
                    },
                    onDeleteNpc = onDeleteNpc,
                    onUpdateNpc = onUpdateNpc
                )
                2 -> SessionsTab(
                    chronicleId = chronicle.id,
                    sessions = uiState.sessions,
                    onCreateSession = onCreateSession,
                    onUpdateSession = onUpdateSession,
                    onDeleteSession = onDeleteSession
                )
                3 -> LocationsTab(
                    chronicleId = chronicle.id,
                    locations = uiState.locations,
                    onCreateLocation = { name, typeId ->
                        onCreateLocation(chronicle.id, name)
                    },
                    onDeleteLocation = onDeleteLocation,
                    onUpdateLocation = onUpdateLocation
                )
                4 -> FactionsTab(
                    chronicleId = chronicle.id,
                    factions = uiState.factions,
                    onCreateFaction = { name, description ->
                        onCreateFaction(chronicle.id, name)
                    },
                    onDeleteFaction = onDeleteFaction,
                    onUpdateFaction = onUpdateFaction
                )
                5 -> PlotsTab(
                    chronicleId = chronicle.id,
                    plotArcs = uiState.plotArcs,
                    onCreatePlotArc = { title, type ->
                        onCreatePlotArc(chronicle.id, title, type)
                    },
                    onDeletePlotArc = onDeletePlotArc,
                    onUpdatePlotArc = onUpdatePlotArc
                )
                6 -> SecretsTab(
                    chronicleId = chronicle.id,
                    secrets = uiState.secrets,
                    clues = uiState.clues,
                    onCreateSecret = { chronicleId, title, content ->
                        onCreateSecret(chronicle.id, title, content)
                    },
                    onDeleteSecret = onDeleteSecret,
                    onUpdateSecret = onUpdateSecret,
                    onCreateClue = { chronicleId, title, content ->
                        onCreateClue(chronicle.id, title, content)
                    },
                    onDeleteClue = onDeleteClue,
                    onUpdateClue = onUpdateClue
                )
                7 -> EventsTab(
                    chronicleId = chronicle.id,
                    events = uiState.events,
                    onCreateEvent = { chronicleId, title ->
                        onCreateEvent(chronicle.id, title)
                    },
                    onDeleteEvent = onDeleteEvent,
                    onUpdateEvent = onUpdateEvent
                )
                8 -> {
                    onOpenMediaLibrary(chronicle.id)
                    onTabSelected(0)
                }
                9 -> NotesTab(
                    chronicleId = chronicle.id,
                    notes = uiState.notes,
                    linkableItems = uiState.toLinkableItems(),
                    onCreateNote = onCreateNote,
                    onUpdateNote = onUpdateNote,
                    onDeleteNote = onDeleteNote,
                    onLinkClick = onLinkClick
                )
                10 -> CharacterNotesTab(
                    chronicleId = chronicle.id,
                    notes = uiState.characterNotes,
                    members = uiState.members,
                    availableCharacters = uiState.availableCharacters,
                    linkableItems = uiState.toLinkableItems(),
                    onCreateNote = onCreateCharacterNote,
                    onUpdateNote = onUpdateCharacterNote,
                    onDeleteNote = onDeleteCharacterNote,
                    onLinkClick = onLinkClick
                )
            }
        }
    }
}

@Composable
fun MembersTab(
    chronicle: Chronicle,
    members: List<ChronicleMember>,
    availableCharacters: List<Character>,
    onAddCharacter: (String, String, ChronicleMemberRole) -> Unit,
    onRemoveCharacter: (String, String) -> Unit,
    onNavigateToCharacter: (String) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.chronicle_members_count, members.size),
                style = MaterialTheme.typography.titleMedium,
                color = V20Ink
            )
            V20BloodButton(
                text = stringResource(R.string.chronicle_add_member),
                onClick = { showAddDialog = true }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (members.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.chronicle_no_members),
                    color = V20InkDim
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(members) { member ->
                    val character = availableCharacters.find { it.id == member.characterId }
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = V20Surface)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onNavigateToCharacter(member.characterId) }
                            ) {
                                Text(
                                    text = character?.identity?.name ?: stringResource(R.string.character_unnamed),
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = V20Ink
                                )
                                Text(
                                    text = "${character?.identity?.clan?.nameEn ?: ""} · ${member.role.name}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = V20InkDim
                                )
                            }
                            IconButton(onClick = { onRemoveCharacter(chronicle.id, member.characterId) }) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = stringResource(R.string.action_remove),
                                    tint = V20Error
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        val assignedIds = members.map { it.characterId }.toSet()
        val unassigned = availableCharacters.filter { it.id !in assignedIds }
        AddMemberDialog(
            characters = unassigned,
            onDismiss = { showAddDialog = false },
            onAdd = { characterId, role ->
                onAddCharacter(chronicle.id, characterId, role)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun AddMemberDialog(
    characters: List<Character>,
    onDismiss: () -> Unit,
    onAdd: (String, ChronicleMemberRole) -> Unit
) {
    var selectedRole by remember { mutableStateOf(ChronicleMemberRole.PLAYER_CHARACTER) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.chronicle_add_member)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    RadioButton(
                        selected = selectedRole == ChronicleMemberRole.PLAYER_CHARACTER,
                        onClick = { selectedRole = ChronicleMemberRole.PLAYER_CHARACTER }
                    )
                    Text(stringResource(R.string.chronicle_role_player))
                    RadioButton(
                        selected = selectedRole == ChronicleMemberRole.NPC,
                        onClick = { selectedRole = ChronicleMemberRole.NPC }
                    )
                    Text(stringResource(R.string.chronicle_role_npc))
                }

                if (characters.isEmpty()) {
                    Text(
                        text = stringResource(R.string.chronicle_no_available_characters),
                        color = V20InkDim
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 300.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(characters) { character ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onAdd(character.id, selectedRole) },
                                colors = CardDefaults.cardColors(containerColor = V20Surface2)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Person,
                                        contentDescription = null,
                                        tint = V20GreenBright,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = character.identity.name.ifEmpty { stringResource(R.string.character_unnamed) },
                                            fontWeight = FontWeight.Bold,
                                            color = V20Ink
                                        )
                                        Text(
                                            text = "${character.identity.clan.nameEn} · ${stringResource(R.string.character_generation_format, character.identity.generation)}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = V20InkDim
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_cancel))
            }
        }
    )
}

@Composable
fun SessionsTab(
    chronicleId: String,
    sessions: List<Session>,
    onCreateSession: (String, String) -> Unit,
    onUpdateSession: (Session) -> Unit,
    onDeleteSession: (String) -> Unit
) {
    var showCreateDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.chronicle_sessions_count, sessions.size),
                style = MaterialTheme.typography.titleMedium,
                color = V20Ink
            )
            V20BloodButton(
                text = stringResource(R.string.chronicle_new_session),
                onClick = { showCreateDialog = true }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (sessions.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.chronicle_no_sessions),
                    color = V20InkDim
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(sessions) { session ->
                    SessionCard(
                        session = session,
                        onUpdate = onUpdateSession,
                        onDelete = { onDeleteSession(session.id) }
                    )
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateSessionDialog(
            onDismiss = { showCreateDialog = false },
            onCreate = { title ->
                onCreateSession(chronicleId, title)
                showCreateDialog = false
            }
        )
    }
}

@Composable
fun SessionCard(
    session: Session,
    onUpdate: (Session) -> Unit,
    onDelete: () -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }
    var editNotes by remember { mutableStateOf(session.notes) }
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = V20Surface)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${stringResource(R.string.chronicle_session)} #${session.number}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = V20Ink
                    )
                    if (session.title.isNotEmpty()) {
                        Text(
                            text = session.title,
                            style = MaterialTheme.typography.bodyMedium,
                            color = V20InkDim
                        )
                    }
                    Text(
                        text = dateFormat.format(Date(session.date)),
                        style = MaterialTheme.typography.bodySmall,
                        color = V20InkFaint
                    )
                }
                Row {
                    IconButton(onClick = { isEditing = !isEditing }) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = stringResource(R.string.action_edit),
                            tint = V20GreenBright
                        )
                    }
                    IconButton(onClick = onDelete) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = stringResource(R.string.action_delete),
                            tint = V20Error
                        )
                    }
                }
            }

            if (isEditing) {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = editNotes,
                    onValueChange = { editNotes = it },
                    label = { Text(stringResource(R.string.chronicle_session_notes)) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
                Spacer(modifier = Modifier.height(8.dp))
                V20IvoryButton(
                    text = stringResource(R.string.action_save),
                    onClick = {
                        onUpdate(session.copy(notes = editNotes))
                        isEditing = false
                    }
                )
            } else if (session.notes.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = session.notes,
                    style = MaterialTheme.typography.bodyMedium,
                    color = V20InkDim
                )
            }
        }
    }
}

@Composable
fun CreateSessionDialog(
    onDismiss: () -> Unit,
    onCreate: (String) -> Unit
) {
    var title by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.chronicle_new_session)) },
        text = {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text(stringResource(R.string.chronicle_session_title)) },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(onClick = { onCreate(title) }) {
                Text(stringResource(R.string.action_create))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_cancel))
            }
        }
    )
}

@Composable
fun NotesTab(
    chronicleId: String,
    notes: List<ChronicleNote>,
    linkableItems: List<LinkableItem>,
    onCreateNote: (String, String) -> Unit,
    onUpdateNote: (ChronicleNote) -> Unit,
    onDeleteNote: (String) -> Unit,
    onLinkClick: (String, String) -> Unit = { _, _ -> }
) {
    var showCreateDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.chronicle_notes_count, notes.size),
                style = MaterialTheme.typography.titleMedium,
                color = V20Ink
            )
            V20BloodButton(
                text = stringResource(R.string.chronicle_new_note),
                onClick = { showCreateDialog = true }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (notes.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.chronicle_no_notes),
                    color = V20InkDim
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(notes) { note ->
                    NoteCard(
                        note = note,
                        linkableItems = linkableItems,
                        onUpdate = onUpdateNote,
                        onDelete = { onDeleteNote(note.id) },
                        onLinkClick = onLinkClick
                    )
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateNoteDialog(
            linkableItems = linkableItems,
            onDismiss = { showCreateDialog = false },
            onCreate = { text ->
                onCreateNote(chronicleId, text)
                showCreateDialog = false
            }
        )
    }
}

@Composable
fun NoteCard(
    note: ChronicleNote,
    linkableItems: List<LinkableItem>,
    onUpdate: (ChronicleNote) -> Unit,
    onDelete: () -> Unit,
    onLinkClick: (String, String) -> Unit = { _, _ -> }
) {
    var isEditing by remember { mutableStateOf(false) }
    var editText by remember { mutableStateOf(note.text) }
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = V20Surface)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = dateFormat.format(Date(note.updatedAt)),
                    style = MaterialTheme.typography.bodySmall,
                    color = V20InkFaint
                )
                Row {
                    IconButton(onClick = { isEditing = !isEditing }) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = stringResource(R.string.action_edit),
                            tint = V20GreenBright,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(onClick = onDelete) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = stringResource(R.string.action_delete),
                            tint = V20Error,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            if (isEditing) {
                LinkedTextEditor(
                    value = editText,
                    onValueChange = { editText = it },
                    linkableItems = linkableItems,
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
                Spacer(modifier = Modifier.height(8.dp))
                V20IvoryButton(
                    text = stringResource(R.string.action_save),
                    onClick = {
                        onUpdate(note.copy(text = editText))
                        isEditing = false
                    }
                )
            } else {
                if (note.text.contains("[") || note.text.contains("#")) {
                    LinkedTextDisplay(
                        text = note.text,
                        linkableItems = linkableItems,
                        onLinkClick = onLinkClick
                    )
                } else {
                    Text(
                        text = note.text,
                        style = MaterialTheme.typography.bodyMedium,
                        color = V20Ink
                    )
                }
            }
        }
    }
}

@Composable
fun CreateNoteDialog(
    linkableItems: List<LinkableItem>,
    onDismiss: () -> Unit,
    onCreate: (String) -> Unit
) {
    var text by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.chronicle_new_note)) },
        text = {
            LinkedTextEditor(
                value = text,
                onValueChange = { text = it },
                linkableItems = linkableItems,
                modifier = Modifier.fillMaxWidth(),
                minLines = 5,
                placeholder = stringResource(R.string.chronicle_note_text)
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onCreate(text) },
                enabled = text.isNotBlank()
            ) {
                Text(stringResource(R.string.action_create))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_cancel))
            }
        }
    )
}

@Composable
fun CharacterNotesTab(
    chronicleId: String,
    notes: List<ChronicleCharacterNote>,
    members: List<ChronicleMember>,
    availableCharacters: List<Character>,
    linkableItems: List<LinkableItem>,
    onCreateNote: (String, String, String) -> Unit,
    onUpdateNote: (ChronicleCharacterNote) -> Unit,
    onDeleteNote: (String) -> Unit,
    onLinkClick: (String, String) -> Unit = { _, _ -> }
) {
    var showCreateDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.chronicle_character_notes_count, notes.size),
                style = MaterialTheme.typography.titleMedium,
                color = V20Ink
            )
            V20BloodButton(
                text = stringResource(R.string.chronicle_new_note),
                onClick = { showCreateDialog = true }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (notes.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.chronicle_no_character_notes),
                    color = V20InkDim
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(notes) { note ->
                    val character = availableCharacters.find { it.id == note.characterId }
                    CharacterNoteCard(
                        note = note,
                        characterName = character?.identity?.name ?: stringResource(R.string.character_unnamed),
                        linkableItems = linkableItems,
                        onUpdate = onUpdateNote,
                        onDelete = { onDeleteNote(note.id) },
                        onLinkClick = onLinkClick
                    )
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateCharacterNoteDialog(
            characters = members.mapNotNull { m ->
                availableCharacters.find { it.id == m.characterId }
            },
            linkableItems = linkableItems,
            onDismiss = { showCreateDialog = false },
            onCreate = { characterId, text ->
                onCreateNote(chronicleId, characterId, text)
                showCreateDialog = false
            }
        )
    }
}

@Composable
fun CharacterNoteCard(
    note: ChronicleCharacterNote,
    characterName: String,
    linkableItems: List<LinkableItem>,
    onUpdate: (ChronicleCharacterNote) -> Unit,
    onDelete: () -> Unit,
    onLinkClick: (String, String) -> Unit = { _, _ -> }
) {
    var isEditing by remember { mutableStateOf(false) }
    var editText by remember { mutableStateOf(note.text) }
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = V20Surface)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = characterName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = V20GreenBright
                    )
                    Text(
                        text = "${stringResource(R.string.chronicle_private_storyteller)} · ${dateFormat.format(Date(note.updatedAt))}",
                        style = MaterialTheme.typography.bodySmall,
                        color = V20InkFaint
                    )
                }
                Row {
                    IconButton(onClick = { isEditing = !isEditing }) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = stringResource(R.string.action_edit),
                            tint = V20GreenBright,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(onClick = onDelete) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = stringResource(R.string.action_delete),
                            tint = V20Error,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            if (isEditing) {
                LinkedTextEditor(
                    value = editText,
                    onValueChange = { editText = it },
                    linkableItems = linkableItems,
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
                Spacer(modifier = Modifier.height(8.dp))
                V20IvoryButton(
                    text = stringResource(R.string.action_save),
                    onClick = {
                        onUpdate(note.copy(text = editText))
                        isEditing = false
                    }
                )
            } else {
                if (note.text.contains("[") || note.text.contains("#")) {
                    LinkedTextDisplay(
                        text = note.text,
                        linkableItems = linkableItems,
                        onLinkClick = onLinkClick
                    )
                } else {
                    Text(
                        text = note.text,
                        style = MaterialTheme.typography.bodyMedium,
                        color = V20Ink
                    )
                }
            }
        }
    }
}

@Composable
fun CreateCharacterNoteDialog(
    characters: List<Character>,
    linkableItems: List<LinkableItem>,
    onDismiss: () -> Unit,
    onCreate: (String, String) -> Unit
) {
    var selectedCharacter by remember { mutableStateOf<Character?>(null) }
    var text by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.chronicle_new_character_note)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = stringResource(R.string.chronicle_select_character),
                    style = MaterialTheme.typography.labelMedium,
                    color = V20InkDim
                )
                LazyColumn(
                    modifier = Modifier.heightIn(max = 150.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(characters) { character ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedCharacter = character },
                            colors = CardDefaults.cardColors(
                                containerColor = if (selectedCharacter?.id == character.id) V20GreenDark else V20Surface2
                            )
                        ) {
                            Text(
                                text = character.identity.name.ifEmpty { stringResource(R.string.character_unnamed) },
                                modifier = Modifier.padding(8.dp),
                                color = V20Ink
                            )
                        }
                    }
                }
                LinkedTextEditor(
                    value = text,
                    onValueChange = { text = it },
                    linkableItems = linkableItems,
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    placeholder = stringResource(R.string.chronicle_note_text)
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    selectedCharacter?.let { onCreate(it.id, text) }
                },
                enabled = selectedCharacter != null && text.isNotBlank()
            ) {
                Text(stringResource(R.string.action_create))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_cancel))
            }
        }
    )
}

@Composable
fun NpcsTab(
    chronicleId: String,
    npcs: List<NpcEntry>,
    onCreateNpc: (String, CreatureType, String) -> Unit,
    onDeleteNpc: (String) -> Unit,
    onUpdateNpc: (NpcEntry) -> Unit
) {
    var showCreateDialog by remember { mutableStateOf(false) }
    var editingNpc by remember { mutableStateOf<NpcEntry?>(null) }
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(text = stringResource(R.string.chronicle_npcs_count, npcs.size), style = MaterialTheme.typography.titleMedium, color = V20Ink)
            V20BloodButton(text = stringResource(R.string.chronicle_new_npc), onClick = { showCreateDialog = true })
        }
        Spacer(modifier = Modifier.height(12.dp))
        if (npcs.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = stringResource(R.string.chronicle_no_npcs), color = V20InkDim)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(npcs) { npc ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = V20Surface),
                        onClick = { editingNpc = npc }
                    ) {
                        Row(modifier = Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            if (npc.imagePath != null) {
                                val ctx = LocalContext.current
                                val file = File(npc.imagePath)
                                if (file.exists()) {
                                    AsyncImage(model = ImageRequest.Builder(ctx).data(file).crossfade(true).build(), contentDescription = null, modifier = Modifier.size(48.dp).clip(RoundedCornerShape(6.dp)).border(1.dp, V20Green, RoundedCornerShape(6.dp)), contentScale = ContentScale.Crop)
                                }
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = npc.name, fontWeight = FontWeight.Bold, color = V20Ink)
                                Text(text = "${npc.creatureType.name} · ${npc.role}", style = MaterialTheme.typography.bodySmall, color = V20InkDim)
                                if (npc.description.isNotBlank()) {
                                    Text(text = npc.description, style = MaterialTheme.typography.bodySmall, color = V20InkFaint, maxLines = 2)
                                }
                            }
                            IconButton(onClick = { onDeleteNpc(npc.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.action_delete), tint = V20Error)
                            }
                        }
                    }
                }
            }
        }
    }
    if (showCreateDialog) {
        var name by remember { mutableStateOf("") }
        var role by remember { mutableStateOf("") }
        var description by remember { mutableStateOf("") }
        var personality by remember { mutableStateOf("") }
        AlertDialog(onDismissRequest = { showCreateDialog = false }, title = { Text(stringResource(R.string.chronicle_new_npc)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text(stringResource(R.string.chronicle_npc_name)) }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = role, onValueChange = { role = it }, label = { Text(stringResource(R.string.chronicle_npc_role)) }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text(stringResource(R.string.chronicle_secret_content)) }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = personality, onValueChange = { personality = it }, label = { Text(stringResource(R.string.chronicle_npc_personality)) }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = { TextButton(onClick = { if (name.isNotBlank()) { onCreateNpc(name, CreatureType.MORTAL, role); showCreateDialog = false } }, enabled = name.isNotBlank()) { Text(stringResource(R.string.action_create)) } },
            dismissButton = { TextButton(onClick = { showCreateDialog = false }) { Text(stringResource(R.string.action_cancel)) } }
        )
    }
    editingNpc?.let { npc ->
        var name by remember { mutableStateOf(npc.name) }
        var role by remember { mutableStateOf(npc.role) }
        var description by remember { mutableStateOf(npc.description) }
        var personality by remember { mutableStateOf(npc.personality) }
        var motivation by remember { mutableStateOf(npc.motivation) }
        var imagePath by remember { mutableStateOf(npc.imagePath) }
        AlertDialog(onDismissRequest = { editingNpc = null }, title = { Text(stringResource(R.string.chronicle_npc_edit)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ChronicleImageRow(imagePath = imagePath, onImagePicked = { path -> imagePath = path }, onImageRemoved = { imagePath = null }, label = stringResource(R.string.chronicle_npc_image))
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text(stringResource(R.string.chronicle_npc_name)) }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = role, onValueChange = { role = it }, label = { Text(stringResource(R.string.chronicle_npc_role)) }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text(stringResource(R.string.chronicle_secret_content)) }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = personality, onValueChange = { personality = it }, label = { Text(stringResource(R.string.chronicle_npc_personality)) }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = motivation, onValueChange = { motivation = it }, label = { Text(stringResource(R.string.chronicle_npc_motivation)) }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = { TextButton(onClick = { onUpdateNpc(npc.copy(name = name, role = role, description = description, personality = personality, motivation = motivation, imagePath = imagePath)); editingNpc = null }) { Text(stringResource(R.string.action_save)) } },
            dismissButton = { TextButton(onClick = { editingNpc = null }) { Text(stringResource(R.string.action_cancel)) } }
        )
    }
}

@Composable
fun LocationsTab(chronicleId: String, locations: List<ChronicleLocation>, onCreateLocation: (String, String) -> Unit, onDeleteLocation: (String) -> Unit, onUpdateLocation: (ChronicleLocation) -> Unit) {
    var showCreateDialog by remember { mutableStateOf(false) }
    var editingLocation by remember { mutableStateOf<ChronicleLocation?>(null) }
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(text = stringResource(R.string.chronicle_locations_count, locations.size), style = MaterialTheme.typography.titleMedium, color = V20Ink)
            V20BloodButton(text = stringResource(R.string.chronicle_new_location), onClick = { showCreateDialog = true })
        }
        Spacer(modifier = Modifier.height(12.dp))
        if (locations.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(text = stringResource(R.string.chronicle_no_locations), color = V20InkDim) }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(locations) { loc ->
                    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = V20Surface), onClick = { editingLocation = loc }) {
                        Row(modifier = Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            if (loc.imagePath != null) {
                                val ctx = LocalContext.current
                                val file = File(loc.imagePath)
                                if (file.exists()) {
                                    AsyncImage(model = ImageRequest.Builder(ctx).data(file).crossfade(true).build(), contentDescription = null, modifier = Modifier.size(48.dp).clip(RoundedCornerShape(6.dp)).border(1.dp, V20Green, RoundedCornerShape(6.dp)), contentScale = ContentScale.Crop)
                                }
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = loc.name, fontWeight = FontWeight.Bold, color = V20Ink)
                                Text(text = loc.typeId, style = MaterialTheme.typography.bodySmall, color = V20InkDim)
                                if (loc.description.isNotBlank()) {
                                    Text(text = loc.description, style = MaterialTheme.typography.bodySmall, color = V20InkFaint, maxLines = 2)
                                }
                            }
                            IconButton(onClick = { onDeleteLocation(loc.id) }) { Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.action_delete), tint = V20Error) }
                        }
                    }
                }
            }
        }
    }
    if (showCreateDialog) {
        var name by remember { mutableStateOf("") }
        var description by remember { mutableStateOf("") }
        AlertDialog(onDismissRequest = { showCreateDialog = false }, title = { Text(stringResource(R.string.chronicle_new_location)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text(stringResource(R.string.chronicle_location_name)) }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text(stringResource(R.string.chronicle_secret_content)) }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = { TextButton(onClick = { if (name.isNotBlank()) { onCreateLocation(name, "Generic Location"); showCreateDialog = false } }, enabled = name.isNotBlank()) { Text(stringResource(R.string.action_create)) } },
            dismissButton = { TextButton(onClick = { showCreateDialog = false }) { Text(stringResource(R.string.action_cancel)) } }
        )
    }
    editingLocation?.let { loc ->
        var name by remember { mutableStateOf(loc.name) }
        var typeId by remember { mutableStateOf(loc.typeId) }
        var description by remember { mutableStateOf(loc.description) }
        var narratorNotes by remember { mutableStateOf(loc.narratorNotes) }
        var imagePath by remember { mutableStateOf(loc.imagePath) }
        AlertDialog(onDismissRequest = { editingLocation = null }, title = { Text(stringResource(R.string.chronicle_location_edit)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ChronicleImageRow(imagePath = imagePath, onImagePicked = { path -> imagePath = path }, onImageRemoved = { imagePath = null }, label = stringResource(R.string.chronicle_location_image))
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text(stringResource(R.string.chronicle_location_name)) }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = typeId, onValueChange = { typeId = it }, label = { Text(stringResource(R.string.chronicle_location_type)) }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text(stringResource(R.string.chronicle_secret_content)) }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = narratorNotes, onValueChange = { narratorNotes = it }, label = { Text(stringResource(R.string.chronicle_npc_notes)) }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = { TextButton(onClick = { onUpdateLocation(loc.copy(name = name, typeId = typeId, description = description, narratorNotes = narratorNotes, imagePath = imagePath)); editingLocation = null }) { Text(stringResource(R.string.action_save)) } },
            dismissButton = { TextButton(onClick = { editingLocation = null }) { Text(stringResource(R.string.action_cancel)) } }
        )
    }
}

@Composable
fun FactionsTab(chronicleId: String, factions: List<Faction>, onCreateFaction: (String, String) -> Unit, onDeleteFaction: (String) -> Unit, onUpdateFaction: (Faction) -> Unit) {
    var showCreateDialog by remember { mutableStateOf(false) }
    var editingFaction by remember { mutableStateOf<Faction?>(null) }
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(text = stringResource(R.string.chronicle_factions_count, factions.size), style = MaterialTheme.typography.titleMedium, color = V20Ink)
            V20BloodButton(text = stringResource(R.string.chronicle_new_faction), onClick = { showCreateDialog = true })
        }
        Spacer(modifier = Modifier.height(12.dp))
        if (factions.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(text = stringResource(R.string.chronicle_no_factions), color = V20InkDim) }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(factions) { f ->
                    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = V20Surface), onClick = { editingFaction = f }) {
                        Row(modifier = Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            if (f.imagePath != null) {
                                val ctx = LocalContext.current
                                val file = File(f.imagePath)
                                if (file.exists()) {
                                    AsyncImage(model = ImageRequest.Builder(ctx).data(file).crossfade(true).build(), contentDescription = null, modifier = Modifier.size(48.dp).clip(RoundedCornerShape(6.dp)).border(1.dp, V20Green, RoundedCornerShape(6.dp)), contentScale = ContentScale.Crop)
                                }
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = f.name, fontWeight = FontWeight.Bold, color = V20Ink)
                                if (f.description.isNotBlank()) {
                                    Text(text = f.description, style = MaterialTheme.typography.bodySmall, color = V20InkFaint, maxLines = 2)
                                }
                            }
                            IconButton(onClick = { onDeleteFaction(f.id) }) { Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.action_delete), tint = V20Error) }
                        }
                    }
                }
            }
        }
    }
    if (showCreateDialog) {
        var name by remember { mutableStateOf("") }
        var description by remember { mutableStateOf("") }
        AlertDialog(onDismissRequest = { showCreateDialog = false }, title = { Text(stringResource(R.string.chronicle_new_faction)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text(stringResource(R.string.chronicle_faction_name)) }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text(stringResource(R.string.chronicle_secret_content)) }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = { TextButton(onClick = { if (name.isNotBlank()) { onCreateFaction(name, description); showCreateDialog = false } }, enabled = name.isNotBlank()) { Text(stringResource(R.string.action_create)) } },
            dismissButton = { TextButton(onClick = { showCreateDialog = false }) { Text(stringResource(R.string.action_cancel)) } }
        )
    }
    editingFaction?.let { f ->
        var name by remember { mutableStateOf(f.name) }
        var description by remember { mutableStateOf(f.description) }
        var narratorNotes by remember { mutableStateOf(f.narratorNotes) }
        var imagePath by remember { mutableStateOf(f.imagePath) }
        AlertDialog(onDismissRequest = { editingFaction = null }, title = { Text(stringResource(R.string.chronicle_faction_edit)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ChronicleImageRow(imagePath = imagePath, onImagePicked = { path -> imagePath = path }, onImageRemoved = { imagePath = null }, label = stringResource(R.string.chronicle_faction_image))
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text(stringResource(R.string.chronicle_faction_name)) }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text(stringResource(R.string.chronicle_secret_content)) }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = narratorNotes, onValueChange = { narratorNotes = it }, label = { Text(stringResource(R.string.chronicle_npc_notes)) }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = { TextButton(onClick = { onUpdateFaction(f.copy(name = name, description = description, narratorNotes = narratorNotes, imagePath = imagePath)); editingFaction = null }) { Text(stringResource(R.string.action_save)) } },
            dismissButton = { TextButton(onClick = { editingFaction = null }) { Text(stringResource(R.string.action_cancel)) } }
        )
    }
}

@Composable
fun PlotsTab(chronicleId: String, plotArcs: List<PlotArc>, onCreatePlotArc: (String, PlotType) -> Unit, onDeletePlotArc: (String) -> Unit, onUpdatePlotArc: (PlotArc) -> Unit) {
    var showCreateDialog by remember { mutableStateOf(false) }
    var editingPlot by remember { mutableStateOf<PlotArc?>(null) }
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(text = stringResource(R.string.chronicle_plots_count, plotArcs.size), style = MaterialTheme.typography.titleMedium, color = V20Ink)
            V20BloodButton(text = stringResource(R.string.chronicle_new_plot), onClick = { showCreateDialog = true })
        }
        Spacer(modifier = Modifier.height(12.dp))
        if (plotArcs.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(text = stringResource(R.string.chronicle_no_plots), color = V20InkDim) }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(plotArcs) { p ->
                    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = V20Surface), onClick = { editingPlot = p }) {
                        Row(modifier = Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = p.title, fontWeight = FontWeight.Bold, color = V20Ink)
                                Text(text = "${p.type.name} · ${p.status.name}", style = MaterialTheme.typography.bodySmall, color = V20InkDim)
                                if (p.summary.isNotBlank()) {
                                    Text(text = p.summary, style = MaterialTheme.typography.bodySmall, color = V20InkFaint, maxLines = 2)
                                }
                            }
                            IconButton(onClick = { onDeletePlotArc(p.id) }) { Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.action_delete), tint = V20Error) }
                        }
                    }
                }
            }
        }
    }
    if (showCreateDialog) {
        var title by remember { mutableStateOf("") }
        var summary by remember { mutableStateOf("") }
        AlertDialog(onDismissRequest = { showCreateDialog = false }, title = { Text(stringResource(R.string.chronicle_new_plot)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text(stringResource(R.string.chronicle_plot_title)) }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = summary, onValueChange = { summary = it }, label = { Text(stringResource(R.string.chronicle_plot_summary)) }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = { TextButton(onClick = { if (title.isNotBlank()) { onCreatePlotArc(title, PlotType.MAIN); showCreateDialog = false } }, enabled = title.isNotBlank()) { Text(stringResource(R.string.action_create)) } },
            dismissButton = { TextButton(onClick = { showCreateDialog = false }) { Text(stringResource(R.string.action_cancel)) } }
        )
    }
    editingPlot?.let { p ->
        var title by remember { mutableStateOf(p.title) }
        var summary by remember { mutableStateOf(p.summary) }
        var startingSituation by remember { mutableStateOf(p.startingSituation) }
        var resolutionNotes by remember { mutableStateOf(p.resolutionNotes) }
        AlertDialog(onDismissRequest = { editingPlot = null }, title = { Text(stringResource(R.string.chronicle_plot_edit)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text(stringResource(R.string.chronicle_plot_title)) }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = summary, onValueChange = { summary = it }, label = { Text(stringResource(R.string.chronicle_plot_summary)) }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = startingSituation, onValueChange = { startingSituation = it }, label = { Text(stringResource(R.string.chronicle_plot_starting)) }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = resolutionNotes, onValueChange = { resolutionNotes = it }, label = { Text(stringResource(R.string.chronicle_plot_resolution)) }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = { TextButton(onClick = { onUpdatePlotArc(p.copy(title = title, summary = summary, startingSituation = startingSituation, resolutionNotes = resolutionNotes)); editingPlot = null }) { Text(stringResource(R.string.action_save)) } },
            dismissButton = { TextButton(onClick = { editingPlot = null }) { Text(stringResource(R.string.action_cancel)) } }
        )
    }
}

@Composable
fun SecretsTab(chronicleId: String, secrets: List<Secret>, clues: List<Clue>, onCreateSecret: (String, String, String) -> Unit, onDeleteSecret: (String) -> Unit, onCreateClue: (String, String, String?) -> Unit, onDeleteClue: (String) -> Unit, onUpdateSecret: (Secret) -> Unit, onUpdateClue: (Clue) -> Unit) {
    var showCreateDialog by remember { mutableStateOf(false) }
    var createType by remember { mutableStateOf("secret") }
    var editingSecret by remember { mutableStateOf<Secret?>(null) }
    var editingClue by remember { mutableStateOf<Clue?>(null) }
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(text = "${stringResource(R.string.chronicle_secrets_count, secrets.size)} · ${stringResource(R.string.chronicle_clues_count, clues.size)}", style = MaterialTheme.typography.titleMedium, color = V20Ink)
            V20BloodButton(text = stringResource(R.string.chronicle_new_secret), onClick = { showCreateDialog = true })
        }
        Spacer(modifier = Modifier.height(12.dp))
        if (secrets.isEmpty() && clues.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(text = stringResource(R.string.chronicle_no_secrets), color = V20InkDim) }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(secrets) { s ->
                    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = V20Surface), onClick = { editingSecret = s }) {
                        Row(modifier = Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = s.title, fontWeight = FontWeight.Bold, color = V20Ink)
                                Text(text = s.status.name, style = MaterialTheme.typography.bodySmall, color = V20InkDim)
                                if (s.content.isNotBlank()) {
                                    Text(text = s.content, style = MaterialTheme.typography.bodySmall, color = V20InkFaint, maxLines = 2)
                                }
                            }
                            IconButton(onClick = { onDeleteSecret(s.id) }) { Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.action_delete), tint = V20Error) }
                        }
                    }
                }
                items(clues) { c ->
                    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = V20Surface), onClick = { editingClue = c }) {
                        Row(modifier = Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = c.title, fontWeight = FontWeight.Bold, color = V20GoldBright)
                                Text(text = c.status.name, style = MaterialTheme.typography.bodySmall, color = V20InkDim)
                                if (c.content != null && c.content.isNotBlank()) {
                                    Text(text = c.content, style = MaterialTheme.typography.bodySmall, color = V20InkFaint, maxLines = 2)
                                }
                            }
                            IconButton(onClick = { onDeleteClue(c.id) }) { Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.action_delete), tint = V20Error) }
                        }
                    }
                }
            }
        }
    }
    if (showCreateDialog) {
        var title by remember { mutableStateOf("") }
        var content by remember { mutableStateOf("") }
        AlertDialog(onDismissRequest = { showCreateDialog = false }, title = { Text(stringResource(R.string.chronicle_new_secret)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(selected = createType == "secret", onClick = { createType = "secret" }, label = { Text(stringResource(R.string.chronicle_secret)) })
                        FilterChip(selected = createType == "clue", onClick = { createType = "clue" }, label = { Text(stringResource(R.string.chronicle_clue)) })
                    }
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text(stringResource(R.string.chronicle_secret_title)) }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = content, onValueChange = { content = it }, label = { Text(stringResource(R.string.chronicle_secret_content)) }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = { TextButton(onClick = { if (title.isNotBlank()) { if (createType == "secret") onCreateSecret(chronicleId, title, content) else onCreateClue(chronicleId, title, content.ifEmpty { null }); showCreateDialog = false } }, enabled = title.isNotBlank()) { Text(stringResource(R.string.action_create)) } },
            dismissButton = { TextButton(onClick = { showCreateDialog = false }) { Text(stringResource(R.string.action_cancel)) } }
        )
    }
    editingSecret?.let { s ->
        var title by remember { mutableStateOf(s.title) }
        var content by remember { mutableStateOf(s.content) }
        AlertDialog(onDismissRequest = { editingSecret = null }, title = { Text(stringResource(R.string.chronicle_secret_edit)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text(stringResource(R.string.chronicle_secret_title)) }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = content, onValueChange = { content = it }, label = { Text(stringResource(R.string.chronicle_secret_content)) }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = { TextButton(onClick = { onUpdateSecret(s.copy(title = title, content = content)); editingSecret = null }) { Text(stringResource(R.string.action_save)) } },
            dismissButton = { TextButton(onClick = { editingSecret = null }) { Text(stringResource(R.string.action_cancel)) } }
        )
    }
    editingClue?.let { c ->
        var title by remember { mutableStateOf(c.title) }
        var content by remember { mutableStateOf(c.content ?: "") }
        AlertDialog(onDismissRequest = { editingClue = null }, title = { Text(stringResource(R.string.chronicle_clue_edit)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text(stringResource(R.string.chronicle_secret_title)) }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = content, onValueChange = { content = it }, label = { Text(stringResource(R.string.chronicle_secret_content)) }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = { TextButton(onClick = { onUpdateClue(c.copy(title = title, content = content.ifEmpty { null })); editingClue = null }) { Text(stringResource(R.string.action_save)) } },
            dismissButton = { TextButton(onClick = { editingClue = null }) { Text(stringResource(R.string.action_cancel)) } }
        )
    }
}

@Composable
fun EventsTab(chronicleId: String, events: List<ChronicleEvent>, onCreateEvent: (String, String) -> Unit, onDeleteEvent: (String) -> Unit, onUpdateEvent: (ChronicleEvent) -> Unit) {
    var showCreateDialog by remember { mutableStateOf(false) }
    var editingEvent by remember { mutableStateOf<ChronicleEvent?>(null) }
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault()) }
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(text = stringResource(R.string.chronicle_events_count, events.size), style = MaterialTheme.typography.titleMedium, color = V20Ink)
            V20BloodButton(text = stringResource(R.string.chronicle_new_event), onClick = { showCreateDialog = true })
        }
        Spacer(modifier = Modifier.height(12.dp))
        if (events.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(text = stringResource(R.string.chronicle_no_events), color = V20InkDim) }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(events) { e ->
                    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = V20Surface), onClick = { editingEvent = e }) {
                        Row(modifier = Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            if (e.imagePath != null) {
                                val ctx = LocalContext.current
                                val file = File(e.imagePath)
                                if (file.exists()) {
                                    AsyncImage(model = ImageRequest.Builder(ctx).data(file).crossfade(true).build(), contentDescription = null, modifier = Modifier.size(48.dp).clip(RoundedCornerShape(6.dp)).border(1.dp, V20Green, RoundedCornerShape(6.dp)), contentScale = ContentScale.Crop)
                                }
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = e.title, fontWeight = FontWeight.Bold, color = V20Ink)
                                Text(text = "${e.typeId} · ${dateFormat.format(java.util.Date(e.timestamp))}", style = MaterialTheme.typography.bodySmall, color = V20InkDim)
                                if (e.description != null && e.description.isNotBlank()) {
                                    Text(text = e.description, style = MaterialTheme.typography.bodySmall, color = V20InkFaint, maxLines = 2)
                                }
                            }
                            IconButton(onClick = { onDeleteEvent(e.id) }) { Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.action_delete), tint = V20Error) }
                        }
                    }
                }
            }
        }
    }
    if (showCreateDialog) {
        var title by remember { mutableStateOf("") }
        var description by remember { mutableStateOf("") }
        AlertDialog(onDismissRequest = { showCreateDialog = false }, title = { Text(stringResource(R.string.chronicle_new_event)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text(stringResource(R.string.chronicle_event_title)) }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text(stringResource(R.string.chronicle_secret_content)) }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = { TextButton(onClick = { if (title.isNotBlank()) { onCreateEvent(chronicleId, title); showCreateDialog = false } }, enabled = title.isNotBlank()) { Text(stringResource(R.string.action_create)) } },
            dismissButton = { TextButton(onClick = { showCreateDialog = false }) { Text(stringResource(R.string.action_cancel)) } }
        )
    }
    editingEvent?.let { e ->
        var title by remember { mutableStateOf(e.title) }
        var description by remember { mutableStateOf(e.description ?: "") }
        var inGameTime by remember { mutableStateOf(e.inGameTime ?: "") }
        var imagePath by remember { mutableStateOf(e.imagePath) }
        AlertDialog(onDismissRequest = { editingEvent = null }, title = { Text(stringResource(R.string.chronicle_event_edit)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ChronicleImageRow(imagePath = imagePath, onImagePicked = { path -> imagePath = path }, onImageRemoved = { imagePath = null }, label = stringResource(R.string.chronicle_event_title))
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text(stringResource(R.string.chronicle_event_title)) }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text(stringResource(R.string.chronicle_secret_content)) }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = inGameTime, onValueChange = { inGameTime = it }, label = { Text(stringResource(R.string.chronicle_event_ingame)) }, singleLine = true, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = { TextButton(onClick = { onUpdateEvent(e.copy(title = title, description = description.ifEmpty { null }, inGameTime = inGameTime.ifEmpty { null }, imagePath = imagePath)); editingEvent = null }) { Text(stringResource(R.string.action_save)) } },
            dismissButton = { TextButton(onClick = { editingEvent = null }) { Text(stringResource(R.string.action_cancel)) } }
        )
    }
}

@Composable
fun ChronicleImageRow(
    imagePath: String?,
    onImagePicked: (String) -> Unit,
    onImageRemoved: () -> Unit,
    label: String
) {
    val context = LocalContext.current
    var currentPath by remember { mutableStateOf(imagePath) }
    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val saved = ChronicleImageManager.saveImage(context, UUID.randomUUID().toString(), it)
            if (saved != null) {
                currentPath = saved
                onImagePicked(saved)
            }
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = V20InkDim)
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (currentPath != null) {
                val file = File(currentPath!!)
                if (file.exists()) {
                    AsyncImage(
                        model = ImageRequest.Builder(context).data(file).crossfade(true).build(),
                        contentDescription = label,
                        modifier = Modifier.size(64.dp).clip(RoundedCornerShape(8.dp)).border(1.dp, V20Green, RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
            } else {
                Box(modifier = Modifier.size(64.dp).clip(RoundedCornerShape(8.dp)).background(V20Surface2).border(1.dp, V20InkFaint, RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Image, contentDescription = null, tint = V20InkFaint, modifier = Modifier.size(24.dp))
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                V20IvoryButton(text = stringResource(R.string.chronicle_pick_image), onClick = { launcher.launch("image/*") })
                if (currentPath != null) {
                    Text(text = stringResource(R.string.chronicle_remove_image), color = V20Error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.clickable { currentPath = null; onImageRemoved() })
                }
            }
        }
    }
}
