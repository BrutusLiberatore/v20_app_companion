package com.v20charactermanager.ui.chronicle

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.v20charactermanager.R
import com.v20charactermanager.domain.model.*

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
    onNpcClick: (NpcEntry) -> Unit,
    onOpenScene: (ChronicleScene) -> Unit,
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
    onRemoveCharacter: (String, String) -> Unit,
    onNavigateToDice: () -> Unit,
    onLinkClick: (String, String) -> Unit
) {
    var selectedNavItem by remember { mutableStateOf(ChronicleBottomNavItem.LIVE) }
    var showSceneDeck by remember { mutableStateOf(false) }

    val chronicle = uiState.chronicle

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(chronicle?.name ?: "")
                },
                actions = {
                    if (uiState.activeSession != null) {
                        Text(
                            text = "LIVE",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                }
            )
        },
        bottomBar = {
            ChronicleBottomNavigation(
                selectedItem = selectedNavItem,
                onItemSelected = { selectedNavItem = it }
            )
        }
    ) { padding ->
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
                    onStartSession = onStartSession,
                    onEndSession = onEndSession,
                    onCharacterClick = onCharacterClick,
                    onCharacterBloodChange = onCharacterBloodChange,
                    onCharacterWillpowerChange = onCharacterWillpowerChange,
                    onNpcClick = onNpcClick,
                    onOpenScene = onOpenScene,
                    onChangeScene = { showSceneDeck = true },
                    onDiceClick = onDiceClick,
                    onQuickNote = onQuickNote,
                    onEventClick = {
                        uiState.activeSession?.let { session ->
                            onEventClick(session.chronicleId, session.id)
                        }
                    },
                    onMediaClick = onMediaClick,
                    modifier = Modifier.padding(padding)
                )
            }
            ChronicleBottomNavItem.PEOPLE -> {
                ChroniclePeopleTab(
                    uiState = uiState,
                    onCharacterClick = onCharacterClick,
                    onAddCharacter = onAddCharacter,
                    onRemoveCharacter = onRemoveCharacter,
                    onCreateNpc = onCreateNpc,
                    onDeleteNpc = onDeleteNpc,
                    onUpdateNpc = onUpdateNpc,
                    onLinkClick = onLinkClick,
                    modifier = Modifier.padding(padding)
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
                    modifier = Modifier.padding(padding)
                )
            }
            ChronicleBottomNavItem.MEDIA -> {
                LaunchedEffect(Unit) {
                    onOpenMediaLibrary(chronicle?.id ?: "")
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
                    modifier = Modifier.padding(padding)
                )
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
            onNewScene = { /* TODO */ },
            onDismiss = { showSceneDeck = false }
        )
    }
}
