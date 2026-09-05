package com.v20charactermanager.domain.repository

import com.v20charactermanager.domain.model.*
import kotlinx.coroutines.flow.Flow

interface ChronicleRepository {
    // Chronicle
    fun getAllChronicles(): Flow<List<Chronicle>>
    fun getChronicleById(id: String): Flow<Chronicle?>
    suspend fun insertChronicle(chronicle: Chronicle)
    suspend fun updateChronicle(chronicle: Chronicle)
    suspend fun deleteChronicle(id: String)

    // Members
    fun getMembers(chronicleId: String): Flow<List<ChronicleMember>>
    fun getMembersByRole(chronicleId: String, role: ChronicleMemberRole): Flow<List<ChronicleMember>>
    fun getCharacterChronicles(characterId: String): Flow<List<ChronicleMember>>
    suspend fun addCharacterToChronicle(chronicleId: String, characterId: String, role: ChronicleMemberRole)
    suspend fun removeCharacterFromChronicle(chronicleId: String, characterId: String)
    suspend fun getChronicleMember(chronicleId: String, characterId: String): ChronicleMember?

    // Sessions
    fun getSessions(chronicleId: String): Flow<List<Session>>
    fun getSessionById(id: String): Flow<Session?>
    fun getActiveSession(chronicleId: String): Flow<Session?>
    fun getLatestSession(chronicleId: String): Flow<Session?>
    suspend fun insertSession(session: Session)
    suspend fun updateSession(session: Session)
    suspend fun deleteSession(id: String)
    suspend fun getNextSessionNumber(chronicleId: String): Int

    // Notes
    fun getChronicleNotes(chronicleId: String): Flow<List<ChronicleNote>>
    fun getChronicleNoteById(id: String): Flow<ChronicleNote?>
    suspend fun insertChronicleNote(note: ChronicleNote)
    suspend fun updateChronicleNote(note: ChronicleNote)
    suspend fun deleteChronicleNote(id: String)

    // Character Notes
    fun getCharacterNotes(chronicleId: String, characterId: String): Flow<List<ChronicleCharacterNote>>
    fun getAllCharacterNotes(chronicleId: String): Flow<List<ChronicleCharacterNote>>
    fun getCharacterNoteById(id: String): Flow<ChronicleCharacterNote?>
    suspend fun insertCharacterNote(note: ChronicleCharacterNote)
    suspend fun updateCharacterNote(note: ChronicleCharacterNote)
    suspend fun deleteCharacterNote(id: String)

    // NPCs
    fun getNpcs(chronicleId: String): Flow<List<NpcEntry>>
    suspend fun insertNpc(npc: NpcEntry)
    suspend fun updateNpc(npc: NpcEntry)
    suspend fun deleteNpc(id: String)

    // Locations
    fun getLocations(chronicleId: String): Flow<List<ChronicleLocation>>
    suspend fun insertLocation(location: ChronicleLocation)
    suspend fun updateLocation(location: ChronicleLocation)
    suspend fun deleteLocation(id: String)

    // Factions
    fun getFactions(chronicleId: String): Flow<List<Faction>>
    suspend fun insertFaction(faction: Faction)
    suspend fun updateFaction(faction: Faction)
    suspend fun deleteFaction(id: String)

    // Relationships
    fun getRelationships(chronicleId: String): Flow<List<Relationship>>
    suspend fun insertRelationship(relationship: Relationship)
    suspend fun updateRelationship(relationship: Relationship)
    suspend fun deleteRelationship(id: String)

    // Plot Arcs
    fun getPlotArcs(chronicleId: String): Flow<List<PlotArc>>
    suspend fun insertPlotArc(plotArc: PlotArc)
    suspend fun updatePlotArc(plotArc: PlotArc)
    suspend fun deletePlotArc(id: String)

    // Scenes
    fun getScenes(chronicleId: String): Flow<List<ChronicleScene>>
    suspend fun insertScene(scene: ChronicleScene)
    suspend fun updateScene(scene: ChronicleScene)
    suspend fun deleteScene(id: String)

    // Secrets
    fun getSecrets(chronicleId: String): Flow<List<Secret>>
    suspend fun insertSecret(secret: Secret)
    suspend fun updateSecret(secret: Secret)
    suspend fun deleteSecret(id: String)

    // Clues
    fun getClues(chronicleId: String): Flow<List<Clue>>
    suspend fun insertClue(clue: Clue)
    suspend fun updateClue(clue: Clue)
    suspend fun deleteClue(id: String)

    // Events
    fun getEvents(chronicleId: String): Flow<List<ChronicleEvent>>
    suspend fun insertEvent(event: ChronicleEvent)
    suspend fun updateEvent(event: ChronicleEvent)
    suspend fun deleteEvent(id: String)

    // Boons
    fun getBoons(chronicleId: String): Flow<List<BoonRecord>>
    suspend fun insertBoon(boon: BoonRecord)
    suspend fun updateBoon(boon: BoonRecord)
    suspend fun deleteBoon(id: String)

    // Quick Notes
    fun getQuickNotes(chronicleId: String): Flow<List<QuickNote>>
    fun getQuickNotesByScope(chronicleId: String, scopeType: String): Flow<List<QuickNote>>
    suspend fun insertQuickNote(note: QuickNote)
    suspend fun updateQuickNote(note: QuickNote)
    suspend fun deleteQuickNote(id: String)

    // Session Events
    fun getSessionEvents(chronicleId: String): Flow<List<SessionEvent>>
    fun getSessionEventsBySession(sessionId: String): Flow<List<SessionEvent>>
    suspend fun insertSessionEvent(event: SessionEvent)
    suspend fun deleteSessionEvent(id: String)
}
