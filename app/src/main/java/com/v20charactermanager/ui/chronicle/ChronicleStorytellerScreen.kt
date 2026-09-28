package com.v20charactermanager.ui.chronicle

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.v20charactermanager.R
import com.v20charactermanager.domain.model.*
import com.v20charactermanager.ui.components.AdaptiveChronicleNavigation
import com.v20charactermanager.ui.components.AdaptiveLayoutType
import com.v20charactermanager.ui.components.rememberAdaptiveLayout
import com.v20charactermanager.ui.theme.V20Ink

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChronicleStorytellerScreen(
    uiState: ChronicleDetailUiState,
    onBack: () -> Unit,
    onStartSession: (Session) -> Unit,
    onEndSession: (Session) -> Unit,
    onCharacterClick: (String) -> Unit,
    onCharacterBloodChange: (Character, Int) -> Unit,
    onCharacterWillpowerChange: (Character, Int) -> Unit,
    onCharacterHealthChange: (Character, Int) -> Unit,
    onChangeScene: (String) -> Unit,
    onDiceClick: () -> Unit,
    onQuickNote: (String) -> Unit,
    onEventClick: (String, String) -> Unit,
    onMediaClick: () -> Unit,
    onOpenMediaLibrary: (String) -> Unit,
    onTabSelected: (Int) -> Unit,
    onCreateSession: (String, String) -> Unit,
    onUpdateSession: (Session) -> Unit,
    onDeleteSession: (String) -> Unit,
    onCreateNpc: (String, String, CreatureType, String, String?) -> Unit,
    onDeleteNpc: (String) -> Unit,
    onUpdateNpc: (NpcEntry) -> Unit,
    onCreateCharacterFromNpc: (NpcEntry, (String) -> Unit) -> Unit,
    onCreatePlotArc: (String, String, PlotType) -> Unit,
    onDeletePlotArc: (String) -> Unit,
    onUpdatePlotArc: (PlotArc) -> Unit,
    onCreateNote: (String, String) -> Unit,
    onUpdateNote: (ChronicleNote) -> Unit,
    onDeleteNote: (String) -> Unit,
    onCreateCharacterNote: (String, String, String) -> Unit,
    onUpdateCharacterNote: (ChronicleCharacterNote) -> Unit,
    onDeleteCharacterNote: (String) -> Unit,
    onUpdateChronicle: (Chronicle) -> Unit,
    onCreateScene: (String, String) -> Unit,
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
    onAddCharacter: (String, String, ChronicleMemberRole) -> Unit,
    onCreateCharacter: () -> Unit = {},
    onRemoveCharacter: (String, String) -> Unit,
    onNavigateToDice: () -> Unit,
    onLinkClick: (String, String) -> Unit,
    onSearchClick: () -> Unit = {},
    audioViewModel: AudioViewModel? = null,
    onViewRecap: (String, String) -> Unit,
    onCloneSession: (Session) -> Unit,
    onLiveRoom: () -> Unit,
    onJoinLiveRoom: () -> Unit = {},
    requestedTab: ChronicleBottomNavItem? = null,
    onTabRequestConsumed: () -> Unit = {}
) {
    var selectedNavItem by remember { mutableStateOf(ChronicleBottomNavItem.LIVE) }
    var showSceneDeck by remember { mutableStateOf(false) }
    var showNewSceneDialog by remember { mutableStateOf(false) }
    var newSceneTitle by remember { mutableStateOf("") }
    var selectedNpc by remember { mutableStateOf<NpcEntry?>(null) }
    var selectedScene by remember { mutableStateOf<ChronicleScene?>(null) }
    val layoutType = rememberAdaptiveLayout()

    LaunchedEffect(requestedTab) {
        requestedTab?.let {
            selectedNavItem = it
            onTabRequestConsumed()
        }
    }

    val chronicle = uiState.chronicle

    when (layoutType) {
        AdaptiveLayoutType.COMPACT -> {
            CompactStorytellerLayout(
                uiState = uiState,
                selectedNavItem = selectedNavItem,
                onItemSelected = { selectedNavItem = it },
                onBack = onBack,
                chronicle = chronicle,
                showSceneDeck = showSceneDeck,
                onShowSceneDeckChange = { showSceneDeck = it },
                showNewSceneDialog = showNewSceneDialog,
                onShowNewSceneDialogChange = { showNewSceneDialog = it },
                newSceneTitle = newSceneTitle,
                onNewSceneTitleChange = { newSceneTitle = it },
                onStartSession = onStartSession,
                onEndSession = onEndSession,
                onCharacterClick = onCharacterClick,
                onCharacterBloodChange = onCharacterBloodChange,
                onCharacterWillpowerChange = onCharacterWillpowerChange,
                onCharacterHealthChange = onCharacterHealthChange,
                onNpcClick = { selectedNpc = it },
                onOpenScene = { selectedScene = it },
                onChangeScene = { showSceneDeck = true },
                onDiceClick = onDiceClick,
                onQuickNote = onQuickNote,
                onEventClick = onEventClick,
                onMediaClick = onMediaClick,
                onQuickNpc = { name, creatureType, role ->
                    uiState.activeSession?.let { session ->
                        onCreateNpc(session.chronicleId, name, creatureType, role, null)
                    }
                },
                onLiveRoom = onLiveRoom,
                onJoinLiveRoom = onJoinLiveRoom,
                onAddCharacter = onAddCharacter,
                onCreateCharacter = onCreateCharacter,
                onRemoveCharacter = onRemoveCharacter,
                onDeleteNpc = onDeleteNpc,
                onUpdateNpc = onUpdateNpc,
                onCreateCharacterFromNpc = onCreateCharacterFromNpc,
                onCreateNpc = onCreateNpc,
                onNavigateToDice = onNavigateToDice,
                onLinkClick = onLinkClick,
                onSearchClick = onSearchClick,
                audioViewModel = audioViewModel,
                onViewRecap = onViewRecap,
                onCloneSession = onCloneSession,
                onOpenMediaLibrary = onOpenMediaLibrary,
                onUpdateChronicle = onUpdateChronicle,
                onCreateLocation = onCreateLocation,
                onDeleteLocation = onDeleteLocation,
                onUpdateLocation = onUpdateLocation,
                onLocationImageClick = onLocationImageClick,
                onCreateFaction = onCreateFaction,
                onDeleteFaction = onDeleteFaction,
                onUpdateFaction = onUpdateFaction,
                onCreateSecret = onCreateSecret,
                onDeleteSecret = onDeleteSecret,
                onUpdateSecret = onUpdateSecret,
                onCreateClue = onCreateClue,
                onDeleteClue = onDeleteClue,
                onUpdateClue = onUpdateClue,
                onCreateEvent = onCreateEvent,
                onDeleteEvent = onDeleteEvent,
                onUpdateEvent = onUpdateEvent,
                onCreateSession = onCreateSession,
                onUpdateSession = onUpdateSession,
                onDeleteSession = onDeleteSession,
                onCreateNote = onCreateNote,
                onUpdateNote = onUpdateNote,
                onDeleteNote = onDeleteNote,
                onCreateCharacterNote = onCreateCharacterNote,
                onUpdateCharacterNote = onUpdateCharacterNote,
                onDeleteCharacterNote = onDeleteCharacterNote,
                onCreatePlotArc = onCreatePlotArc,
                onDeletePlotArc = onDeletePlotArc,
                onUpdatePlotArc = onUpdatePlotArc,
                onCreateScene = onCreateScene
            )
        }
        AdaptiveLayoutType.MEDIUM,
        AdaptiveLayoutType.EXPANDED -> {
            Row(modifier = Modifier.fillMaxSize()) {
                AdaptiveChronicleNavigation(
                    selectedItem = selectedNavItem,
                    onItemSelected = { selectedNavItem = it },
                    layoutType = layoutType
                )
                Surface(
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.background
                ) {
                    StorytellerContent(
                        uiState = uiState,
                        selectedNavItem = selectedNavItem,
                        chronicle = chronicle,
                        showSceneDeck = showSceneDeck,
                        onShowSceneDeckChange = { showSceneDeck = it },
                        showNewSceneDialog = showNewSceneDialog,
                        onShowNewSceneDialogChange = { showNewSceneDialog = it },
                        newSceneTitle = newSceneTitle,
                        onNewSceneTitleChange = { newSceneTitle = it },
                        onStartSession = onStartSession,
                        onEndSession = onEndSession,
                        onCharacterClick = onCharacterClick,
                        onCharacterBloodChange = onCharacterBloodChange,
                        onCharacterWillpowerChange = onCharacterWillpowerChange,
                        onCharacterHealthChange = onCharacterHealthChange,
                        onNpcClick = { selectedNpc = it },
                        onOpenScene = { selectedScene = it },
                        onChangeScene = { showSceneDeck = true },
                        onDiceClick = onDiceClick,
                        onQuickNote = onQuickNote,
                        onEventClick = onEventClick,
                        onMediaClick = onMediaClick,
                        onQuickNpc = { name, creatureType, role ->
                            uiState.activeSession?.let { session ->
                                onCreateNpc(session.chronicleId, name, creatureType, role, null)
                            }
                        },
                        onLiveRoom = onLiveRoom,
                        onAddCharacter = onAddCharacter,
                        onCreateCharacter = onCreateCharacter,
                        onRemoveCharacter = onRemoveCharacter,
                        onDeleteNpc = onDeleteNpc,
                        onUpdateNpc = onUpdateNpc,
                        onCreateCharacterFromNpc = onCreateCharacterFromNpc,
                        onCreateNpc = onCreateNpc,
                        onNavigateToDice = onNavigateToDice,
                        onLinkClick = onLinkClick,
                        onSearchClick = onSearchClick,
                        audioViewModel = audioViewModel,
                        onViewRecap = onViewRecap,
                        onCloneSession = onCloneSession,
                        onOpenMediaLibrary = onOpenMediaLibrary,
                        onUpdateChronicle = onUpdateChronicle,
                        onCreateLocation = onCreateLocation,
                        onDeleteLocation = onDeleteLocation,
                        onUpdateLocation = onUpdateLocation,
                        onLocationImageClick = onLocationImageClick,
                        onCreateFaction = onCreateFaction,
                        onDeleteFaction = onDeleteFaction,
                        onUpdateFaction = onUpdateFaction,
                        onCreateSecret = onCreateSecret,
                        onDeleteSecret = onDeleteSecret,
                        onUpdateSecret = onUpdateSecret,
                        onCreateClue = onCreateClue,
                        onDeleteClue = onDeleteClue,
                        onUpdateClue = onUpdateClue,
                        onCreateEvent = onCreateEvent,
                        onDeleteEvent = onDeleteEvent,
                        onUpdateEvent = onUpdateEvent,
                        onCreateSession = onCreateSession,
                        onUpdateSession = onUpdateSession,
                        onDeleteSession = onDeleteSession,
                        onCreateNote = onCreateNote,
                        onUpdateNote = onUpdateNote,
                        onDeleteNote = onDeleteNote,
                        onCreateCharacterNote = onCreateCharacterNote,
                        onUpdateCharacterNote = onUpdateCharacterNote,
                        onDeleteCharacterNote = onDeleteCharacterNote,
                        onCreatePlotArc = onCreatePlotArc,
                        onDeletePlotArc = onDeletePlotArc,
                        onUpdatePlotArc = onUpdatePlotArc,
                        onCreateScene = onCreateScene
                    )
                }
            }
        }
    }

    // Scene Deck Bottom Sheet
    if (showSceneDeck) {
        SceneDeckSheet(
            scenes = uiState.scenes,
            activeSceneId = uiState.activeSession?.activeSceneId,
            onSceneSelect = { scene ->
                uiState.activeSession?.let { session ->
                    onChangeScene(scene.id)
                }
                showSceneDeck = false
            },
            onNewScene = {
                showSceneDeck = false
                showNewSceneDialog = true
            },
            onDismiss = { showSceneDeck = false }
        )
    }

    // New Scene Dialog
    if (showNewSceneDialog) {
        AlertDialog(
            onDismissRequest = { showNewSceneDialog = false },
            title = { Text(stringResource(R.string.storyteller_new_scene)) },
            text = {
                OutlinedTextField(
                    value = newSceneTitle,
                    onValueChange = { newSceneTitle = it },
                    label = { Text(stringResource(R.string.storyteller_scene_title)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newSceneTitle.isNotBlank()) {
                            chronicle?.let { c ->
                                onCreateScene(c.id, newSceneTitle)
                            }
                            newSceneTitle = ""
                            showNewSceneDialog = false
                        }
                    }
                ) {
                    Text(stringResource(R.string.action_create))
                }
            },
            dismissButton = {
                TextButton(onClick = { newSceneTitle = ""; showNewSceneDialog = false }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        )
    }

    // NPC Detail Sheet (opened from LIVE tab)
    selectedNpc?.let { npc ->
        val linkableItems = remember(uiState) { uiState.toLinkableItems() }
        NpcDetailSheet(
            npc = npc,
            linkableItems = linkableItems,
            onUpdate = { updatedNpc ->
                onUpdateNpc(updatedNpc)
                selectedNpc = null
            },
            onCreateSheet = { sheetNpc ->
                onCreateCharacterFromNpc(sheetNpc) { newCharacterId ->
                    selectedNpc = null
                    onCharacterClick(newCharacterId)
                }
            },
            onOpenSheet = { characterId ->
                onCharacterClick(characterId)
                selectedNpc = null
            },
            onLinkClick = onLinkClick,
            onDelete = {
                onDeleteNpc(npc.id)
                selectedNpc = null
            },
            onDismiss = { selectedNpc = null }
        )
    }

    // Scene Detail Dialog (opened from LIVE tab "Open")
    selectedScene?.let { scene ->
        SceneDetailDialog(
            scene = scene,
            isActive = uiState.activeSession?.activeSceneId == scene.id,
            onActivate = {
                onChangeScene(scene.id)
                selectedScene = null
            },
            onDismiss = { selectedScene = null }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CompactStorytellerLayout(
    uiState: ChronicleDetailUiState,
    selectedNavItem: ChronicleBottomNavItem,
    onItemSelected: (ChronicleBottomNavItem) -> Unit,
    onBack: () -> Unit,
    chronicle: Chronicle?,
    showSceneDeck: Boolean,
    onShowSceneDeckChange: (Boolean) -> Unit,
    showNewSceneDialog: Boolean,
    onShowNewSceneDialogChange: (Boolean) -> Unit,
    newSceneTitle: String,
    onNewSceneTitleChange: (String) -> Unit,
    onStartSession: (Session) -> Unit,
    onEndSession: (Session) -> Unit,
    onCharacterClick: (String) -> Unit,
    onCharacterBloodChange: (Character, Int) -> Unit,
    onCharacterWillpowerChange: (Character, Int) -> Unit,
    onCharacterHealthChange: (Character, Int) -> Unit,
    onNpcClick: (NpcEntry) -> Unit,
    onOpenScene: (ChronicleScene) -> Unit,
    onChangeScene: () -> Unit,
    onDiceClick: () -> Unit,
    onQuickNote: (String) -> Unit,
    onEventClick: (String, String) -> Unit,
    onMediaClick: () -> Unit,
    onQuickNpc: (String, CreatureType, String) -> Unit,
    onLiveRoom: () -> Unit,
    onJoinLiveRoom: () -> Unit,
    onAddCharacter: (String, String, ChronicleMemberRole) -> Unit,
    onCreateCharacter: () -> Unit = {},
    onRemoveCharacter: (String, String) -> Unit,
    onDeleteNpc: (String) -> Unit,
    onUpdateNpc: (NpcEntry) -> Unit,
    onCreateCharacterFromNpc: (NpcEntry, (String) -> Unit) -> Unit,
    onCreateNpc: (String, String, CreatureType, String, String?) -> Unit,
    onNavigateToDice: () -> Unit,
    onLinkClick: (String, String) -> Unit,
    onSearchClick: () -> Unit,
    audioViewModel: AudioViewModel?,
    onViewRecap: (String, String) -> Unit,
    onCloneSession: (Session) -> Unit,
    onOpenMediaLibrary: (String) -> Unit,
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
    onCreateNote: (String, String) -> Unit,
    onUpdateNote: (ChronicleNote) -> Unit,
    onDeleteNote: (String) -> Unit,
    onCreateCharacterNote: (String, String, String) -> Unit,
    onUpdateCharacterNote: (ChronicleCharacterNote) -> Unit,
    onDeleteCharacterNote: (String) -> Unit,
    onCreatePlotArc: (String, String, PlotType) -> Unit,
    onDeletePlotArc: (String) -> Unit,
    onUpdatePlotArc: (PlotArc) -> Unit,
    onCreateScene: (String, String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(chronicle?.name ?: "") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = stringResource(R.string.action_back))
                    }
                },
                actions = {
                    if (selectedNavItem == ChronicleBottomNavItem.AUDIO && audioViewModel != null) {
                        IconButton(onClick = { audioViewModel.stopAll() }) {
                            Icon(Icons.Filled.Stop, contentDescription = stringResource(R.string.stop_all), tint = Color.Red)
                        }
                    }
                    if (uiState.activeSession != null) {
                        Text(
                            text = stringResource(R.string.live_badge),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    if (uiState.activeSession != null) {
                        AssistChip(
                            onClick = onLiveRoom,
                            label = {
                                Text(
                                    text = stringResource(R.string.live_room_create_table),
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Filled.Casino,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        )
                    }
                    AssistChip(
                        onClick = onJoinLiveRoom,
                        label = {
                            Text(
                                text = stringResource(R.string.live_room_join_table),
                                fontWeight = FontWeight.Bold
                            )
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Filled.Casino,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    )
                    IconButton(onClick = onSearchClick) {
                        Icon(Icons.Filled.Search, contentDescription = stringResource(R.string.action_search))
                    }
                }
            )
        },
        bottomBar = {
            AdaptiveChronicleNavigation(
                selectedItem = selectedNavItem,
                onItemSelected = onItemSelected,
                layoutType = AdaptiveLayoutType.COMPACT
            )
        }
    ) { padding ->
        StorytellerContent(
            uiState = uiState,
            selectedNavItem = selectedNavItem,
            chronicle = chronicle,
            showSceneDeck = showSceneDeck,
            onShowSceneDeckChange = onShowSceneDeckChange,
            showNewSceneDialog = showNewSceneDialog,
            onShowNewSceneDialogChange = onShowNewSceneDialogChange,
            newSceneTitle = newSceneTitle,
            onNewSceneTitleChange = onNewSceneTitleChange,
            onStartSession = onStartSession,
            onEndSession = onEndSession,
            onCharacterClick = onCharacterClick,
            onCharacterBloodChange = onCharacterBloodChange,
            onCharacterWillpowerChange = onCharacterWillpowerChange,
            onCharacterHealthChange = onCharacterHealthChange,
            onNpcClick = onNpcClick,
            onOpenScene = onOpenScene,
            onChangeScene = onChangeScene,
            onDiceClick = onDiceClick,
            onQuickNote = onQuickNote,
            onEventClick = onEventClick,
            onMediaClick = onMediaClick,
            onQuickNpc = onQuickNpc,
            onLiveRoom = onLiveRoom,
            onAddCharacter = onAddCharacter,
            onCreateCharacter = onCreateCharacter,
            onRemoveCharacter = onRemoveCharacter,
            onDeleteNpc = onDeleteNpc,
            onUpdateNpc = onUpdateNpc,
            onCreateCharacterFromNpc = onCreateCharacterFromNpc,
            onCreateNpc = onCreateNpc,
            onNavigateToDice = onNavigateToDice,
            onLinkClick = onLinkClick,
            onSearchClick = onSearchClick,
            audioViewModel = audioViewModel,
            onViewRecap = onViewRecap,
            onCloneSession = onCloneSession,
            onOpenMediaLibrary = onOpenMediaLibrary,
            onUpdateChronicle = onUpdateChronicle,
            onCreateLocation = onCreateLocation,
            onDeleteLocation = onDeleteLocation,
            onUpdateLocation = onUpdateLocation,
            onLocationImageClick = onLocationImageClick,
            onCreateFaction = onCreateFaction,
            onDeleteFaction = onDeleteFaction,
            onUpdateFaction = onUpdateFaction,
            onCreateSecret = onCreateSecret,
            onDeleteSecret = onDeleteSecret,
            onUpdateSecret = onUpdateSecret,
            onCreateClue = onCreateClue,
            onDeleteClue = onDeleteClue,
            onUpdateClue = onUpdateClue,
            onCreateEvent = onCreateEvent,
            onDeleteEvent = onDeleteEvent,
            onUpdateEvent = onUpdateEvent,
            onCreateSession = onCreateSession,
            onUpdateSession = onUpdateSession,
            onDeleteSession = onDeleteSession,
            onCreateNote = onCreateNote,
            onUpdateNote = onUpdateNote,
            onDeleteNote = onDeleteNote,
            onCreateCharacterNote = onCreateCharacterNote,
            onUpdateCharacterNote = onUpdateCharacterNote,
            onDeleteCharacterNote = onDeleteCharacterNote,
            onCreatePlotArc = onCreatePlotArc,
            onDeletePlotArc = onDeletePlotArc,
            onUpdatePlotArc = onUpdatePlotArc,
            onCreateScene = onCreateScene,
            modifier = Modifier.padding(padding)
        )
    }
}

@Composable
private fun StorytellerContent(
    uiState: ChronicleDetailUiState,
    selectedNavItem: ChronicleBottomNavItem,
    chronicle: Chronicle?,
    showSceneDeck: Boolean,
    onShowSceneDeckChange: (Boolean) -> Unit,
    showNewSceneDialog: Boolean,
    onShowNewSceneDialogChange: (Boolean) -> Unit,
    newSceneTitle: String,
    onNewSceneTitleChange: (String) -> Unit,
    onStartSession: (Session) -> Unit,
    onEndSession: (Session) -> Unit,
    onCharacterClick: (String) -> Unit,
    onCharacterBloodChange: (Character, Int) -> Unit,
    onCharacterWillpowerChange: (Character, Int) -> Unit,
    onCharacterHealthChange: (Character, Int) -> Unit,
    onNpcClick: (NpcEntry) -> Unit,
    onOpenScene: (ChronicleScene) -> Unit,
    onChangeScene: () -> Unit,
    onDiceClick: () -> Unit,
    onQuickNote: (String) -> Unit,
    onEventClick: (String, String) -> Unit,
    onMediaClick: () -> Unit,
    onQuickNpc: (String, CreatureType, String) -> Unit,
    onLiveRoom: () -> Unit,
    onAddCharacter: (String, String, ChronicleMemberRole) -> Unit,
    onCreateCharacter: () -> Unit = {},
    onRemoveCharacter: (String, String) -> Unit,
    onDeleteNpc: (String) -> Unit,
    onUpdateNpc: (NpcEntry) -> Unit,
    onCreateCharacterFromNpc: (NpcEntry, (String) -> Unit) -> Unit,
    onCreateNpc: (String, String, CreatureType, String, String?) -> Unit,
    onNavigateToDice: () -> Unit,
    onLinkClick: (String, String) -> Unit,
    onSearchClick: () -> Unit = {},
    audioViewModel: AudioViewModel? = null,
    onViewRecap: (String, String) -> Unit,
    onCloneSession: (Session) -> Unit,
    onOpenMediaLibrary: (String) -> Unit,
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
    onCreateNote: (String, String) -> Unit,
    onUpdateNote: (ChronicleNote) -> Unit,
    onDeleteNote: (String) -> Unit,
    onCreateCharacterNote: (String, String, String) -> Unit,
    onUpdateCharacterNote: (ChronicleCharacterNote) -> Unit,
    onDeleteCharacterNote: (String) -> Unit,
    onCreatePlotArc: (String, String, PlotType) -> Unit,
    onDeletePlotArc: (String) -> Unit,
    onUpdatePlotArc: (PlotArc) -> Unit,
    onCreateScene: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    when (selectedNavItem) {
        ChronicleBottomNavItem.LIVE -> {
            StorytellerLiveScreen(
                chronicle = chronicle,
                session = uiState.activeSession,
                scenes = uiState.scenes,
                members = uiState.members,
                npcs = uiState.npcs,
                availableCharacters = uiState.availableCharacters,
                quickNotes = uiState.quickNotes,
                sessionEvents = uiState.sessionEvents,
                plotArcs = uiState.plotArcs,
                onStartSession = onStartSession,
                onEndSession = onEndSession,
                onCharacterClick = onCharacterClick,
                onCharacterBloodChange = onCharacterBloodChange,
                onCharacterWillpowerChange = onCharacterWillpowerChange,
                onCharacterHealthChange = onCharacterHealthChange,
                onNpcClick = onNpcClick,
                onOpenScene = onOpenScene,
                onChangeScene = onChangeScene,
                onDiceClick = onDiceClick,
                onQuickNote = onQuickNote,
                onEventClick = { title, desc -> onEventClick(title, desc) },
                onMediaClick = onMediaClick,
                onQuickNpc = { name, creatureType, role ->
                    uiState.activeSession?.let { session ->
                        onCreateNpc(session.chronicleId, name, creatureType, role, null)
                    }
                },
                modifier = modifier
            )
        }
        ChronicleBottomNavItem.PEOPLE -> {
            ChroniclePeopleTab(
                uiState = uiState,
                onCharacterClick = onCharacterClick,
                onAddCharacter = onAddCharacter,
                onCreateCharacter = onCreateCharacter,
                onRemoveCharacter = onRemoveCharacter,
                onCreateNpc = onCreateNpc,
                onDeleteNpc = onDeleteNpc,
                onUpdateNpc = onUpdateNpc,
                onBloodChange = { character, delta -> onCharacterBloodChange(character, delta) },
                onWillpowerChange = { character, delta -> onCharacterWillpowerChange(character, delta) },
                onHealthChange = { character, delta -> onCharacterHealthChange(character, delta) },
                onCreateCharacterFromNpc = onCreateCharacterFromNpc,
                onOpenSheet = onCharacterClick,
                onLinkClick = onLinkClick,
                modifier = modifier
            )
        }
        ChronicleBottomNavItem.PLOTS -> {
            ChroniclePlotsTab(
                uiState = uiState,
                onCreatePlotArc = onCreatePlotArc,
                onDeletePlotArc = onDeletePlotArc,
                onUpdatePlotArc = onUpdatePlotArc,
                onCreateNote = onCreateNote,
                onUpdateNote = onUpdateNote,
                onDeleteNote = onDeleteNote,
                onCreateCharacterNote = onCreateCharacterNote,
                onUpdateCharacterNote = onUpdateCharacterNote,
                onDeleteCharacterNote = onDeleteCharacterNote,
                onLinkClick = onLinkClick,
                modifier = modifier
            )
        }
        ChronicleBottomNavItem.MEDIA -> {
            LaunchedEffect(Unit) {
                onOpenMediaLibrary(chronicle?.id ?: "")
            }
        }
        ChronicleBottomNavItem.AUDIO -> {
            if (audioViewModel != null && chronicle != null) {
                AudioMixContent(
                    chronicleId = chronicle.id,
                    audioViewModel = audioViewModel,
                    modifier = modifier
                )
            }
        }
        ChronicleBottomNavItem.MORE -> {
            ChronicleMoreTab(
                uiState = uiState,
                onNavigateToDice = onNavigateToDice,
                onUpdateChronicle = onUpdateChronicle,
                onCreateLocation = onCreateLocation,
                onDeleteLocation = onDeleteLocation,
                onUpdateLocation = onUpdateLocation,
                onLocationImageClick = onLocationImageClick,
                onCreateFaction = onCreateFaction,
                onDeleteFaction = onDeleteFaction,
                onUpdateFaction = onUpdateFaction,
                onCreateSecret = onCreateSecret,
                onDeleteSecret = onDeleteSecret,
                onUpdateSecret = onUpdateSecret,
                onCreateClue = onCreateClue,
                onDeleteClue = onDeleteClue,
                onUpdateClue = onUpdateClue,
                onCreateEvent = onCreateEvent,
                onDeleteEvent = onDeleteEvent,
                onUpdateEvent = onUpdateEvent,
                onCreateSession = onCreateSession,
                onUpdateSession = onUpdateSession,
                onDeleteSession = onDeleteSession,
                onViewRecap = onViewRecap,
                onCloneSession = onCloneSession,
                modifier = modifier
            )
        }
    }
}
