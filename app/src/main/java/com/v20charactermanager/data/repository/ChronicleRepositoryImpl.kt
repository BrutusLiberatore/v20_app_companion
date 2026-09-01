package com.v20charactermanager.data.repository

import com.v20charactermanager.data.local.dao.*
import com.v20charactermanager.data.local.entity.*
import com.v20charactermanager.domain.model.*
import com.v20charactermanager.domain.repository.ChronicleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ChronicleRepositoryImpl(
    private val chronicleDao: ChronicleDao,
    private val chronicleMemberDao: ChronicleMemberDao,
    private val sessionDao: SessionDao,
    private val chronicleNoteDao: ChronicleNoteDao,
    private val chronicleCharacterNoteDao: ChronicleCharacterNoteDao,
    private val npcDao: NpcDao,
    private val locationDao: LocationDao,
    private val factionDao: FactionDao,
    private val relationshipDao: RelationshipDao,
    private val plotArcDao: PlotArcDao,
    private val sceneDao: SceneDao,
    private val secretDao: SecretDao,
    private val clueDao: ClueDao,
    private val eventDao: EventDao,
    private val boonDao: BoonDao,
    private val quickNoteDao: QuickNoteDao,
    private val sessionEventDao: SessionEventDao
) : ChronicleRepository {

    // Chronicle
    override fun getAllChronicles(): Flow<List<Chronicle>> {
        return chronicleDao.getAllChronicles().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getChronicleById(id: String): Flow<Chronicle?> {
        return chronicleDao.getChronicleById(id).map { it?.toDomain() }
    }

    override suspend fun insertChronicle(chronicle: Chronicle) {
        chronicleDao.insertChronicle(chronicle.toEntity())
    }

    override suspend fun updateChronicle(chronicle: Chronicle) {
        chronicleDao.updateChronicle(chronicle.copy(updatedAt = System.currentTimeMillis()).toEntity())
    }

    override suspend fun deleteChronicle(id: String) {
        chronicleDao.deleteChronicle(id)
    }

    // Members
    override fun getMembers(chronicleId: String): Flow<List<ChronicleMember>> {
        return chronicleMemberDao.getMembersByChronicle(chronicleId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getMembersByRole(chronicleId: String, role: ChronicleMemberRole): Flow<List<ChronicleMember>> {
        return chronicleMemberDao.getMembersByRole(chronicleId, role.name).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getCharacterChronicles(characterId: String): Flow<List<ChronicleMember>> {
        return chronicleMemberDao.getChroniclesForCharacter(characterId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun addCharacterToChronicle(chronicleId: String, characterId: String, role: ChronicleMemberRole) {
        val existing = chronicleMemberDao.getMember(chronicleId, characterId)
        if (existing != null) return
        val member = ChronicleMemberEntity(
            id = "${chronicleId}_${characterId}",
            chronicleId = chronicleId,
            characterId = characterId,
            role = role.name,
            createdAt = System.currentTimeMillis()
        )
        chronicleMemberDao.insertMember(member)
    }

    override suspend fun removeCharacterFromChronicle(chronicleId: String, characterId: String) {
        chronicleMemberDao.removeCharacterFromChronicle(chronicleId, characterId)
    }

    override suspend fun getChronicleMember(chronicleId: String, characterId: String): ChronicleMember? {
        return chronicleMemberDao.getMember(chronicleId, characterId)?.toDomain()
    }

    // Sessions
    override fun getSessions(chronicleId: String): Flow<List<Session>> {
        return sessionDao.getSessionsByChronicle(chronicleId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getSessionById(id: String): Flow<Session?> {
        return sessionDao.getSessionById(id).map { it?.toDomain() }
    }

    override fun getActiveSession(chronicleId: String): Flow<Session?> {
        return sessionDao.getActiveSession(chronicleId).map { it?.toDomain() }
    }

    override suspend fun insertSession(session: Session) {
        sessionDao.insertSession(session.toEntity())
    }

    override suspend fun updateSession(session: Session) {
        sessionDao.updateSession(session.copy(updatedAt = System.currentTimeMillis()).toEntity())
    }

    override suspend fun deleteSession(id: String) {
        sessionDao.deleteSession(id)
    }

    override suspend fun getNextSessionNumber(chronicleId: String): Int {
        return (sessionDao.getMaxSessionNumber(chronicleId) ?: 0) + 1
    }

    // Notes
    override fun getChronicleNotes(chronicleId: String): Flow<List<ChronicleNote>> {
        return chronicleNoteDao.getNotesByChronicle(chronicleId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getChronicleNoteById(id: String): Flow<ChronicleNote?> {
        return chronicleNoteDao.getNoteById(id).map { it?.toDomain() }
    }

    override suspend fun insertChronicleNote(note: ChronicleNote) {
        chronicleNoteDao.insertNote(note.toEntity())
    }

    override suspend fun updateChronicleNote(note: ChronicleNote) {
        chronicleNoteDao.updateNote(note.copy(updatedAt = System.currentTimeMillis()).toEntity())
    }

    override suspend fun deleteChronicleNote(id: String) {
        chronicleNoteDao.deleteNote(id)
    }

    // Character Notes
    override fun getCharacterNotes(chronicleId: String, characterId: String): Flow<List<ChronicleCharacterNote>> {
        return chronicleCharacterNoteDao.getNotesForCharacter(chronicleId, characterId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getAllCharacterNotes(chronicleId: String): Flow<List<ChronicleCharacterNote>> {
        return chronicleCharacterNoteDao.getAllNotesByChronicle(chronicleId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getCharacterNoteById(id: String): Flow<ChronicleCharacterNote?> {
        return chronicleCharacterNoteDao.getNoteById(id).map { it?.toDomain() }
    }

    override suspend fun insertCharacterNote(note: ChronicleCharacterNote) {
        chronicleCharacterNoteDao.insertNote(note.toEntity())
    }

    override suspend fun updateCharacterNote(note: ChronicleCharacterNote) {
        chronicleCharacterNoteDao.updateNote(note.copy(updatedAt = System.currentTimeMillis()).toEntity())
    }

    override suspend fun deleteCharacterNote(id: String) {
        chronicleCharacterNoteDao.deleteNote(id)
    }

    // NPCs
    override fun getNpcs(chronicleId: String): Flow<List<NpcEntry>> {
        return npcDao.getNpcsByChronicle(chronicleId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insertNpc(npc: NpcEntry) {
        npcDao.insertNpc(npc.toEntity())
    }

    override suspend fun updateNpc(npc: NpcEntry) {
        npcDao.updateNpc(npc.copy(updatedAt = System.currentTimeMillis()).toEntity())
    }

    override suspend fun deleteNpc(id: String) {
        npcDao.deleteNpc(id)
    }

    // Locations
    override fun getLocations(chronicleId: String): Flow<List<ChronicleLocation>> {
        return locationDao.getLocationsByChronicle(chronicleId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insertLocation(location: ChronicleLocation) {
        locationDao.insertLocation(location.toEntity())
    }

    override suspend fun updateLocation(location: ChronicleLocation) {
        locationDao.updateLocation(location.copy(updatedAt = System.currentTimeMillis()).toEntity())
    }

    override suspend fun deleteLocation(id: String) {
        locationDao.deleteLocation(id)
    }

    // Factions
    override fun getFactions(chronicleId: String): Flow<List<Faction>> {
        return factionDao.getFactionsByChronicle(chronicleId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insertFaction(faction: Faction) {
        factionDao.insertFaction(faction.toEntity())
    }

    override suspend fun updateFaction(faction: Faction) {
        factionDao.updateFaction(faction.copy(updatedAt = System.currentTimeMillis()).toEntity())
    }

    override suspend fun deleteFaction(id: String) {
        factionDao.deleteFaction(id)
    }

    // Relationships
    override fun getRelationships(chronicleId: String): Flow<List<Relationship>> {
        return relationshipDao.getRelationshipsByChronicle(chronicleId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insertRelationship(relationship: Relationship) {
        relationshipDao.insertRelationship(relationship.toEntity())
    }

    override suspend fun updateRelationship(relationship: Relationship) {
        relationshipDao.updateRelationship(relationship.copy(updatedAt = System.currentTimeMillis()).toEntity())
    }

    override suspend fun deleteRelationship(id: String) {
        relationshipDao.deleteRelationship(id)
    }

    // Plot Arcs
    override fun getPlotArcs(chronicleId: String): Flow<List<PlotArc>> {
        return plotArcDao.getPlotArcsByChronicle(chronicleId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insertPlotArc(plotArc: PlotArc) {
        plotArcDao.insertPlotArc(plotArc.toEntity())
    }

    override suspend fun updatePlotArc(plotArc: PlotArc) {
        plotArcDao.updatePlotArc(plotArc.copy(updatedAt = System.currentTimeMillis()).toEntity())
    }

    override suspend fun deletePlotArc(id: String) {
        plotArcDao.deletePlotArc(id)
    }

    // Scenes
    override fun getScenes(chronicleId: String): Flow<List<ChronicleScene>> {
        return sceneDao.getScenesByChronicle(chronicleId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insertScene(scene: ChronicleScene) {
        sceneDao.insertScene(scene.toEntity())
    }

    override suspend fun updateScene(scene: ChronicleScene) {
        sceneDao.updateScene(scene.copy(updatedAt = System.currentTimeMillis()).toEntity())
    }

    override suspend fun deleteScene(id: String) {
        sceneDao.deleteScene(id)
    }

    // Secrets
    override fun getSecrets(chronicleId: String): Flow<List<Secret>> {
        return secretDao.getSecretsByChronicle(chronicleId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insertSecret(secret: Secret) {
        secretDao.insertSecret(secret.toEntity())
    }

    override suspend fun updateSecret(secret: Secret) {
        secretDao.updateSecret(secret.copy(updatedAt = System.currentTimeMillis()).toEntity())
    }

    override suspend fun deleteSecret(id: String) {
        secretDao.deleteSecret(id)
    }

    // Clues
    override fun getClues(chronicleId: String): Flow<List<Clue>> {
        return clueDao.getCluesByChronicle(chronicleId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insertClue(clue: Clue) {
        clueDao.insertClue(clue.toEntity())
    }

    override suspend fun updateClue(clue: Clue) {
        clueDao.updateClue(clue.copy(updatedAt = System.currentTimeMillis()).toEntity())
    }

    override suspend fun deleteClue(id: String) {
        clueDao.deleteClue(id)
    }

    // Events
    override fun getEvents(chronicleId: String): Flow<List<ChronicleEvent>> {
        return eventDao.getEventsByChronicle(chronicleId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insertEvent(event: ChronicleEvent) {
        eventDao.insertEvent(event.toEntity())
    }

    override suspend fun updateEvent(event: ChronicleEvent) {
        eventDao.updateEvent(event.copy().toEntity())
    }

    override suspend fun deleteEvent(id: String) {
        eventDao.deleteEvent(id)
    }

    // Boons
    override fun getBoons(chronicleId: String): Flow<List<BoonRecord>> {
        return boonDao.getBoonsByChronicle(chronicleId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insertBoon(boon: BoonRecord) {
        boonDao.insertBoon(boon.toEntity())
    }

    override suspend fun updateBoon(boon: BoonRecord) {
        boonDao.updateBoon(boon.copy(updatedAt = System.currentTimeMillis()).toEntity())
    }

    override suspend fun deleteBoon(id: String) {
        boonDao.deleteBoon(id)
    }

    // Quick Notes
    override fun getQuickNotes(chronicleId: String): Flow<List<QuickNote>> {
        return quickNoteDao.getNotesByChronicle(chronicleId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getQuickNotesByScope(chronicleId: String, scopeType: String): Flow<List<QuickNote>> {
        return quickNoteDao.getNotesByScope(chronicleId, scopeType).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insertQuickNote(note: QuickNote) {
        quickNoteDao.insertNote(note.toEntity())
    }

    override suspend fun updateQuickNote(note: QuickNote) {
        quickNoteDao.updateNote(note.copy(modifiedAt = System.currentTimeMillis()).toEntity())
    }

    override suspend fun deleteQuickNote(id: String) {
        quickNoteDao.deleteNote(id)
    }

    // Session Events
    override fun getSessionEvents(chronicleId: String): Flow<List<SessionEvent>> {
        return sessionEventDao.getEventsByChronicle(chronicleId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getSessionEventsBySession(sessionId: String): Flow<List<SessionEvent>> {
        return sessionEventDao.getEventsBySession(sessionId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insertSessionEvent(event: SessionEvent) {
        sessionEventDao.insertEvent(event.toEntity())
    }

    override suspend fun deleteSessionEvent(id: String) {
        sessionEventDao.deleteEvent(id)
    }
}
