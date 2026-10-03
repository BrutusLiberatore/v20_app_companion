package com.v20charactermanager.data.backup

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.v20charactermanager.data.repository.AudioRepositoryImpl
import com.v20charactermanager.data.repository.HouseRuleRepositoryImpl
import com.v20charactermanager.domain.engine.BackupEnvelope
import com.v20charactermanager.domain.engine.BackupEngine
import com.v20charactermanager.domain.engine.ChronicleBackup
import com.v20charactermanager.domain.repository.CharacterRepository
import com.v20charactermanager.domain.repository.ChronicleRepository
import com.v20charactermanager.domain.repository.MediaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class RestoreSummary(val characters: Int, val chronicles: Int)

/**
 * Builds, writes and restores full app backups (.v20backup = zip with backup.json + files/).
 * Shared by the Import/Export screen (manual) and MainActivity (daily automatic copy).
 */
class BackupManager(
    private val context: Context,
    private val characterRepository: CharacterRepository,
    private val chronicleRepository: ChronicleRepository,
    private val mediaRepository: MediaRepository,
    private val audioRepository: AudioRepositoryImpl,
    private val houseRuleRepository: HouseRuleRepositoryImpl,
    private val clearAll: suspend () -> Unit
) {

    suspend fun collect(): BackupEnvelope = withContext(Dispatchers.IO) {
        val characters = characterRepository.getAllCharacters().first()
        val chronicles = chronicleRepository.getAllChronicles().first()
        val bundles = chronicles.map { chronicle ->
            val mediaAssets = mediaRepository.getAssetsByChronicle(chronicle.id).first()
            val documents = mediaAssets.mapNotNull { asset ->
                mediaRepository.getDocumentByAssetId(asset.id).first()
            }
            audioRepository.loadTracks(chronicle.id)
            val tracks = audioRepository.tracks.first()
            audioRepository.loadPresets(chronicle.id)
            val presets = audioRepository.presets.first()
            ChronicleBackup(
                chronicle = chronicle,
                members = chronicleRepository.getMembers(chronicle.id).first(),
                sessions = chronicleRepository.getSessions(chronicle.id).first(),
                sessionEvents = chronicleRepository.getSessionEvents(chronicle.id).first(),
                notes = chronicleRepository.getChronicleNotes(chronicle.id).first(),
                characterNotes = chronicleRepository.getAllCharacterNotes(chronicle.id).first(),
                npcs = chronicleRepository.getNpcs(chronicle.id).first(),
                locations = chronicleRepository.getLocations(chronicle.id).first(),
                factions = chronicleRepository.getFactions(chronicle.id).first(),
                relationships = chronicleRepository.getRelationships(chronicle.id).first(),
                plotArcs = chronicleRepository.getPlotArcs(chronicle.id).first(),
                scenes = chronicleRepository.getScenes(chronicle.id).first(),
                sceneVariants = chronicleRepository.getSceneVariants(chronicle.id).first(),
                secrets = chronicleRepository.getSecrets(chronicle.id).first(),
                clues = chronicleRepository.getClues(chronicle.id).first(),
                events = chronicleRepository.getEvents(chronicle.id).first(),
                boons = chronicleRepository.getBoons(chronicle.id).first(),
                quickNotes = chronicleRepository.getQuickNotes(chronicle.id).first(),
                mediaAssets = mediaAssets,
                imageDocuments = documents,
                imageLayers = documents.flatMap { mediaRepository.getLayersByDocument(it.id).first() },
                imageAnnotations = documents.flatMap { mediaRepository.getAnnotationsByDocument(it.id).first() },
                imageRevisions = documents.flatMap { mediaRepository.getRevisionsByDocument(it.id).first() },
                audioTracks = tracks,
                audioPresets = presets,
                houseRules = houseRuleRepository.getHouseRules(chronicle.id)
            )
        }
        BackupEnvelope(createdAt = System.currentTimeMillis(), characters = characters, chronicles = bundles)
    }

    suspend fun exportTo(uri: Uri) = withContext(Dispatchers.IO) {
        val envelope = prepare()
        val output = context.contentResolver.openOutputStream(uri)
            ?: throw IOException("cannot open output stream for $uri")
        output.use { BackupEngine.writeZip(envelope, it) { path -> openLocalFile(path) } }
    }

    /** Daily automatic copy: Download/V20Companion on API 29+, app-specific backup dir below. */
    suspend fun exportAutoToDownloads(): Boolean = withContext(Dispatchers.IO) {
        val envelope = prepare()
        if (envelope.characters.isEmpty() && envelope.chronicles.isEmpty()) return@withContext true
        val name = "v20_backup_auto_" +
            SimpleDateFormat("yyyyMMdd_HHmm", Locale.US).format(Date()) + ".v20backup"
        if (Build.VERSION.SDK_INT >= 29) {
            val values = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, name)
                put(MediaStore.MediaColumns.MIME_TYPE, "application/zip")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/V20Companion")
            }
            val collection = MediaStore.Downloads.EXTERNAL_CONTENT_URI
            val target = context.contentResolver.insert(collection, values) ?: return@withContext false
            try {
                val output = context.contentResolver.openOutputStream(target)
                    ?: throw IOException("cannot open MediaStore stream")
                output.use { BackupEngine.writeZip(envelope, it) { path -> openLocalFile(path) } }
                true
            } catch (e: Exception) {
                try { context.contentResolver.delete(target, null, null) } catch (_: Exception) {}
                throw e
            }
        } else {
            val dir = context.getExternalFilesDir("backup") ?: File(context.filesDir, "backup")
            dir.mkdirs()
            val file = File(dir, name)
            file.outputStream().use { BackupEngine.writeZip(envelope, it) { path -> openLocalFile(path) } }
            true
        }
    }

    suspend fun restoreFrom(uri: Uri): RestoreSummary = withContext(Dispatchers.IO) {
        val input = context.contentResolver.openInputStream(uri)
            ?: throw IOException("cannot open input stream for $uri")
        val envelope = input.use { stream ->
            BackupEngine.readZip(stream) { entryName, entryStream ->
                val target = File(File(context.filesDir, "backup_files"), File(entryName).name)
                target.parentFile?.mkdirs()
                target.outputStream().use { out -> entryStream.copyTo(out) }
                target.absolutePath
            }
        }

        clearAll()

        var characterCount = 0
        envelope.characters.forEach { character ->
            characterRepository.insertCharacter(character)
            characterCount++
        }
        var chronicleCount = 0
        envelope.chronicles.forEach { bundle ->
            chronicleRepository.insertChronicle(bundle.chronicle)
            chronicleCount++
            bundle.members.forEach { member ->
                chronicleRepository.addCharacterToChronicle(bundle.chronicle.id, member.characterId, member.role)
            }
            bundle.sessions.forEach { chronicleRepository.insertSession(it) }
            bundle.notes.forEach { chronicleRepository.insertChronicleNote(it) }
            bundle.characterNotes.forEach { chronicleRepository.insertCharacterNote(it) }
            bundle.npcs.forEach { chronicleRepository.insertNpc(it) }
            bundle.locations.forEach { chronicleRepository.insertLocation(it) }
            bundle.factions.forEach { chronicleRepository.insertFaction(it) }
            bundle.relationships.forEach { chronicleRepository.insertRelationship(it) }
            bundle.plotArcs.forEach { chronicleRepository.insertPlotArc(it) }
            bundle.scenes.forEach { chronicleRepository.insertScene(it) }
            bundle.sceneVariants.forEach { chronicleRepository.insertSceneVariant(it) }
            bundle.secrets.forEach { chronicleRepository.insertSecret(it) }
            bundle.clues.forEach { chronicleRepository.insertClue(it) }
            bundle.events.forEach { chronicleRepository.insertEvent(it) }
            bundle.boons.forEach { chronicleRepository.insertBoon(it) }
            bundle.quickNotes.forEach { chronicleRepository.insertQuickNote(it) }
            bundle.sessionEvents.forEach { chronicleRepository.insertSessionEvent(it) }
            bundle.mediaAssets.forEach { mediaRepository.insertAsset(it) }
            bundle.imageDocuments.forEach { mediaRepository.insertDocument(it) }
            bundle.imageLayers.forEach { mediaRepository.insertLayer(it) }
            bundle.imageAnnotations.forEach { mediaRepository.insertAnnotation(it) }
            bundle.imageRevisions.forEach { mediaRepository.insertRevision(it) }
            bundle.audioTracks.forEach { audioRepository.insertTrack(it) }
            bundle.audioPresets.forEach { audioRepository.insertPreset(it) }
            bundle.houseRules?.let { houseRuleRepository.saveHouseRules(it) }
        }
        RestoreSummary(characters = characterCount, chronicles = chronicleCount)
    }

    private suspend fun prepare(): BackupEnvelope {
        val envelope = collect()
        val existingPaths = BackupEngine.collectFilePaths(envelope).filter { File(it).exists() }
        return envelope.copy(
            createdAt = System.currentTimeMillis(),
            fileMap = BackupEngine.buildFileMap(existingPaths)
        )
    }

    private fun openLocalFile(path: String): java.io.InputStream? {
        val file = File(path)
        return if (file.exists()) file.inputStream() else null
    }
}
