package com.v20charactermanager.ui.chronicle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.v20charactermanager.domain.model.*
import com.v20charactermanager.domain.repository.ChronicleRepository
import com.v20charactermanager.domain.repository.CharacterRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

data class ChronicleListUiState(
    val chronicles: List<Chronicle> = emptyList(),
    val isLoading: Boolean = true
)

data class ChronicleDetailUiState(
    val chronicle: Chronicle? = null,
    val members: List<ChronicleMember> = emptyList(),
    val sessions: List<Session> = emptyList(),
    val notes: List<ChronicleNote> = emptyList(),
    val characterNotes: List<ChronicleCharacterNote> = emptyList(),
    val npcs: List<NpcEntry> = emptyList(),
    val locations: List<ChronicleLocation> = emptyList(),
    val factions: List<Faction> = emptyList(),
    val relationships: List<Relationship> = emptyList(),
    val plotArcs: List<PlotArc> = emptyList(),
    val scenes: List<ChronicleScene> = emptyList(),
    val secrets: List<Secret> = emptyList(),
    val clues: List<Clue> = emptyList(),
    val events: List<ChronicleEvent> = emptyList(),
    val boons: List<BoonRecord> = emptyList(),
    val availableCharacters: List<Character> = emptyList(),
    val selectedTab: Int = 0,
    val isLoading: Boolean = true
)

class ChronicleViewModel(
    private val chronicleRepository: ChronicleRepository,
    private val characterRepository: CharacterRepository
) : ViewModel() {

    private val _listUiState = MutableStateFlow(ChronicleListUiState())
    val listUiState: StateFlow<ChronicleListUiState> = _listUiState.asStateFlow()

    private val _detailUiState = MutableStateFlow(ChronicleDetailUiState())
    val detailUiState: StateFlow<ChronicleDetailUiState> = _detailUiState.asStateFlow()

    init {
        viewModelScope.launch {
            chronicleRepository.getAllChronicles().collect { chronicles ->
                _listUiState.update { it.copy(chronicles = chronicles, isLoading = false) }
            }
        }
    }

    fun createChronicle(name: String, description: String, storytellerName: String, userRole: ChronicleUserRole) {
        viewModelScope.launch {
            val chronicle = Chronicle(
                id = UUID.randomUUID().toString(),
                name = name,
                description = description,
                storytellerName = storytellerName,
                userRole = userRole
            )
            chronicleRepository.insertChronicle(chronicle)
        }
    }

    fun deleteChronicle(id: String) {
        viewModelScope.launch {
            chronicleRepository.deleteChronicle(id)
        }
    }

    fun loadChronicleDetail(chronicleId: String) {
        viewModelScope.launch {
            chronicleRepository.getChronicleById(chronicleId).collect { chronicle ->
                _detailUiState.update { it.copy(chronicle = chronicle, isLoading = false) }
            }
        }
        viewModelScope.launch {
            chronicleRepository.getMembers(chronicleId).collect { members ->
                _detailUiState.update { it.copy(members = members) }
            }
        }
        viewModelScope.launch {
            chronicleRepository.getSessions(chronicleId).collect { sessions ->
                _detailUiState.update { it.copy(sessions = sessions) }
            }
        }
        viewModelScope.launch {
            chronicleRepository.getChronicleNotes(chronicleId).collect { notes ->
                _detailUiState.update { it.copy(notes = notes) }
            }
        }
        viewModelScope.launch {
            chronicleRepository.getAllCharacterNotes(chronicleId).collect { notes ->
                _detailUiState.update { it.copy(characterNotes = notes) }
            }
        }
        viewModelScope.launch {
            characterRepository.getAllCharacters().collect { characters ->
                _detailUiState.update { it.copy(availableCharacters = characters) }
            }
        }
        viewModelScope.launch {
            chronicleRepository.getNpcs(chronicleId).collect { npcs ->
                _detailUiState.update { it.copy(npcs = npcs) }
            }
        }
        viewModelScope.launch {
            chronicleRepository.getLocations(chronicleId).collect { locations ->
                _detailUiState.update { it.copy(locations = locations) }
            }
        }
        viewModelScope.launch {
            chronicleRepository.getFactions(chronicleId).collect { factions ->
                _detailUiState.update { it.copy(factions = factions) }
            }
        }
        viewModelScope.launch {
            chronicleRepository.getRelationships(chronicleId).collect { relationships ->
                _detailUiState.update { it.copy(relationships = relationships) }
            }
        }
        viewModelScope.launch {
            chronicleRepository.getPlotArcs(chronicleId).collect { plotArcs ->
                _detailUiState.update { it.copy(plotArcs = plotArcs) }
            }
        }
        viewModelScope.launch {
            chronicleRepository.getScenes(chronicleId).collect { scenes ->
                _detailUiState.update { it.copy(scenes = scenes) }
            }
        }
        viewModelScope.launch {
            chronicleRepository.getSecrets(chronicleId).collect { secrets ->
                _detailUiState.update { it.copy(secrets = secrets) }
            }
        }
        viewModelScope.launch {
            chronicleRepository.getClues(chronicleId).collect { clues ->
                _detailUiState.update { it.copy(clues = clues) }
            }
        }
        viewModelScope.launch {
            chronicleRepository.getEvents(chronicleId).collect { events ->
                _detailUiState.update { it.copy(events = events) }
            }
        }
        viewModelScope.launch {
            chronicleRepository.getBoons(chronicleId).collect { boons ->
                _detailUiState.update { it.copy(boons = boons) }
            }
        }
    }

    fun selectTab(tab: Int) {
        _detailUiState.update { it.copy(selectedTab = tab) }
    }

    fun addCharacterToChronicle(chronicleId: String, characterId: String, role: ChronicleMemberRole) {
        viewModelScope.launch {
            chronicleRepository.addCharacterToChronicle(chronicleId, characterId, role)
        }
    }

    fun removeCharacterFromChronicle(chronicleId: String, characterId: String) {
        viewModelScope.launch {
            chronicleRepository.removeCharacterFromChronicle(chronicleId, characterId)
        }
    }

    fun createSession(chronicleId: String, title: String) {
        viewModelScope.launch {
            val nextNumber = chronicleRepository.getNextSessionNumber(chronicleId)
            val session = Session(
                id = UUID.randomUUID().toString(),
                chronicleId = chronicleId,
                number = nextNumber,
                title = title
            )
            chronicleRepository.insertSession(session)
        }
    }

    fun updateSession(session: Session) {
        viewModelScope.launch {
            chronicleRepository.updateSession(session)
        }
    }

    fun deleteSession(id: String) {
        viewModelScope.launch {
            chronicleRepository.deleteSession(id)
        }
    }

    fun createChronicleNote(chronicleId: String, text: String) {
        viewModelScope.launch {
            val note = ChronicleNote(
                id = UUID.randomUUID().toString(),
                chronicleId = chronicleId,
                text = text
            )
            chronicleRepository.insertChronicleNote(note)
        }
    }

    fun updateChronicleNote(note: ChronicleNote) {
        viewModelScope.launch {
            chronicleRepository.updateChronicleNote(note)
        }
    }

    fun deleteChronicleNote(id: String) {
        viewModelScope.launch {
            chronicleRepository.deleteChronicleNote(id)
        }
    }

    fun createCharacterNote(chronicleId: String, characterId: String, text: String) {
        viewModelScope.launch {
            val note = ChronicleCharacterNote(
                id = UUID.randomUUID().toString(),
                chronicleId = chronicleId,
                characterId = characterId,
                text = text
            )
            chronicleRepository.insertCharacterNote(note)
        }
    }

    fun updateCharacterNote(note: ChronicleCharacterNote) {
        viewModelScope.launch {
            chronicleRepository.updateCharacterNote(note)
        }
    }

    fun deleteCharacterNote(id: String) {
        viewModelScope.launch {
            chronicleRepository.deleteCharacterNote(id)
        }
    }

    fun updateChronicle(chronicle: Chronicle) {
        viewModelScope.launch {
            chronicleRepository.updateChronicle(chronicle)
        }
    }

    // NPCs
    fun createNpc(chronicleId: String, name: String, creatureType: CreatureType = CreatureType.MORTAL, role: String = "") {
        viewModelScope.launch {
            val npc = NpcEntry(id = UUID.randomUUID().toString(), chronicleId = chronicleId, name = name, creatureType = creatureType, role = role)
            chronicleRepository.insertNpc(npc)
        }
    }
    fun updateNpc(npc: NpcEntry) { viewModelScope.launch { chronicleRepository.updateNpc(npc) } }
    fun deleteNpc(id: String) { viewModelScope.launch { chronicleRepository.deleteNpc(id) } }

    // Locations
    fun createLocation(chronicleId: String, name: String, typeId: String = "Generic Location") {
        viewModelScope.launch {
            val location = ChronicleLocation(id = UUID.randomUUID().toString(), chronicleId = chronicleId, name = name, typeId = typeId)
            chronicleRepository.insertLocation(location)
        }
    }
    fun updateLocation(location: ChronicleLocation) { viewModelScope.launch { chronicleRepository.updateLocation(location) } }
    fun deleteLocation(id: String) { viewModelScope.launch { chronicleRepository.deleteLocation(id) } }

    // Factions
    fun createFaction(chronicleId: String, name: String) {
        viewModelScope.launch {
            val faction = Faction(id = UUID.randomUUID().toString(), chronicleId = chronicleId, name = name)
            chronicleRepository.insertFaction(faction)
        }
    }
    fun updateFaction(faction: Faction) { viewModelScope.launch { chronicleRepository.updateFaction(faction) } }
    fun deleteFaction(id: String) { viewModelScope.launch { chronicleRepository.deleteFaction(id) } }

    // Relationships
    fun createRelationship(chronicleId: String, fromId: String, fromType: String, toId: String, toType: String, typeId: String = "") {
        viewModelScope.launch {
            val rel = Relationship(id = UUID.randomUUID().toString(), chronicleId = chronicleId, fromEntityId = fromId, fromEntityType = fromType, toEntityId = toId, toEntityType = toType, typeId = typeId)
            chronicleRepository.insertRelationship(rel)
        }
    }
    fun deleteRelationship(id: String) { viewModelScope.launch { chronicleRepository.deleteRelationship(id) } }

    // Plot Arcs
    fun createPlotArc(chronicleId: String, title: String, type: PlotType = PlotType.MAIN) {
        viewModelScope.launch {
            val plot = PlotArc(id = UUID.randomUUID().toString(), chronicleId = chronicleId, title = title, type = type)
            chronicleRepository.insertPlotArc(plot)
        }
    }
    fun updatePlotArc(plotArc: PlotArc) { viewModelScope.launch { chronicleRepository.updatePlotArc(plotArc) } }
    fun deletePlotArc(id: String) { viewModelScope.launch { chronicleRepository.deletePlotArc(id) } }

    // Scenes
    fun createScene(chronicleId: String, title: String) {
        viewModelScope.launch {
            val scene = ChronicleScene(id = UUID.randomUUID().toString(), chronicleId = chronicleId, title = title)
            chronicleRepository.insertScene(scene)
        }
    }
    fun updateScene(scene: ChronicleScene) { viewModelScope.launch { chronicleRepository.updateScene(scene) } }
    fun deleteScene(id: String) { viewModelScope.launch { chronicleRepository.deleteScene(id) } }

    // Secrets
    fun createSecret(chronicleId: String, title: String, content: String = "") {
        viewModelScope.launch {
            val secret = Secret(id = UUID.randomUUID().toString(), chronicleId = chronicleId, title = title, content = content)
            chronicleRepository.insertSecret(secret)
        }
    }
    fun updateSecret(secret: Secret) { viewModelScope.launch { chronicleRepository.updateSecret(secret) } }
    fun deleteSecret(id: String) { viewModelScope.launch { chronicleRepository.deleteSecret(id) } }

    // Clues
    fun createClue(chronicleId: String, title: String, content: String? = null) {
        viewModelScope.launch {
            val clue = Clue(id = UUID.randomUUID().toString(), chronicleId = chronicleId, title = title, content = content)
            chronicleRepository.insertClue(clue)
        }
    }
    fun updateClue(clue: Clue) { viewModelScope.launch { chronicleRepository.updateClue(clue) } }
    fun deleteClue(id: String) { viewModelScope.launch { chronicleRepository.deleteClue(id) } }

    // Events
    fun createEvent(chronicleId: String, title: String, typeId: String = "GENERAL") {
        viewModelScope.launch {
            val event = ChronicleEvent(id = UUID.randomUUID().toString(), chronicleId = chronicleId, title = title, typeId = typeId)
            chronicleRepository.insertEvent(event)
        }
    }
    fun updateEvent(event: ChronicleEvent) { viewModelScope.launch { chronicleRepository.updateEvent(event) } }
    fun deleteEvent(id: String) { viewModelScope.launch { chronicleRepository.deleteEvent(id) } }

    // Boons
    fun createBoon(chronicleId: String, creditorId: String, debtorId: String, description: String) {
        viewModelScope.launch {
            val boon = BoonRecord(id = UUID.randomUUID().toString(), chronicleId = chronicleId, creditorEntityId = creditorId, debtorEntityId = debtorId, description = description)
            chronicleRepository.insertBoon(boon)
        }
    }
    fun updateBoon(boon: BoonRecord) { viewModelScope.launch { chronicleRepository.updateBoon(boon) } }
    fun deleteBoon(id: String) { viewModelScope.launch { chronicleRepository.deleteBoon(id) } }
}

class ChronicleViewModelFactory(
    private val chronicleRepository: ChronicleRepository,
    private val characterRepository: CharacterRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChronicleViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChronicleViewModel(chronicleRepository, characterRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
