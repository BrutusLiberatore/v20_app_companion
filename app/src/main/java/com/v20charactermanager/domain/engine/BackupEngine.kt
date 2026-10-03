package com.v20charactermanager.domain.engine

import com.v20charactermanager.domain.model.AudioPreset
import com.v20charactermanager.domain.model.AudioTrack
import com.v20charactermanager.domain.model.BoonRecord
import com.v20charactermanager.domain.model.Character
import com.v20charactermanager.domain.model.Chronicle
import com.v20charactermanager.domain.model.ChronicleCharacterNote
import com.v20charactermanager.domain.model.ChronicleEvent
import com.v20charactermanager.domain.model.ChronicleLocation
import com.v20charactermanager.domain.model.ChronicleMember
import com.v20charactermanager.domain.model.ChronicleNote
import com.v20charactermanager.domain.model.ChronicleScene
import com.v20charactermanager.domain.model.Clue
import com.v20charactermanager.domain.model.Faction
import com.v20charactermanager.domain.model.HouseRules
import com.v20charactermanager.domain.model.ImageAnnotation
import com.v20charactermanager.domain.model.ImageDocument
import com.v20charactermanager.domain.model.ImageLayer
import com.v20charactermanager.domain.model.ImageRevision
import com.v20charactermanager.domain.model.MediaAsset
import com.v20charactermanager.domain.model.NpcEntry
import com.v20charactermanager.domain.model.PlotArc
import com.v20charactermanager.domain.model.QuickNote
import com.v20charactermanager.domain.model.Relationship
import com.v20charactermanager.domain.model.SceneVariant
import com.v20charactermanager.domain.model.Secret
import com.v20charactermanager.domain.model.Session
import com.v20charactermanager.domain.model.SessionEvent
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

/** All data of a single chronicle, flattened for backup purposes. */
@Serializable
data class ChronicleBackup(
    val chronicle: Chronicle,
    val members: List<ChronicleMember> = emptyList(),
    val sessions: List<Session> = emptyList(),
    val sessionEvents: List<SessionEvent> = emptyList(),
    val notes: List<ChronicleNote> = emptyList(),
    val characterNotes: List<ChronicleCharacterNote> = emptyList(),
    val npcs: List<NpcEntry> = emptyList(),
    val locations: List<ChronicleLocation> = emptyList(),
    val factions: List<Faction> = emptyList(),
    val relationships: List<Relationship> = emptyList(),
    val plotArcs: List<PlotArc> = emptyList(),
    val scenes: List<ChronicleScene> = emptyList(),
    val sceneVariants: List<SceneVariant> = emptyList(),
    val secrets: List<Secret> = emptyList(),
    val clues: List<Clue> = emptyList(),
    val events: List<ChronicleEvent> = emptyList(),
    val boons: List<BoonRecord> = emptyList(),
    val quickNotes: List<QuickNote> = emptyList(),
    val mediaAssets: List<MediaAsset> = emptyList(),
    val imageDocuments: List<ImageDocument> = emptyList(),
    val imageLayers: List<ImageLayer> = emptyList(),
    val imageAnnotations: List<ImageAnnotation> = emptyList(),
    val imageRevisions: List<ImageRevision> = emptyList(),
    val audioTracks: List<AudioTrack> = emptyList(),
    val audioPresets: List<AudioPreset> = emptyList(),
    val houseRules: HouseRules? = null
)

/** Root document of a .v20backup archive. `fileMap` maps original absolute paths to zip entries. */
@Serializable
data class BackupEnvelope(
    val schemaVersion: Int = BackupEngine.SCHEMA_VERSION,
    val createdAt: Long = 0L,
    val characters: List<Character> = emptyList(),
    val chronicles: List<ChronicleBackup> = emptyList(),
    val fileMap: Map<String, String> = emptyMap()
)

class BackupFormatException(message: String, cause: Throwable? = null) : Exception(message, cause)
class BackupVersionException(message: String) : Exception(message)

object BackupEngine {

    const val SCHEMA_VERSION = 1
    const val ENTRY_JSON = "backup.json"
    const val FILE_PREFIX = "files/"

    private val json = Json { ignoreUnknownKeys = true }

    /** Every file path referenced by the envelope (portraits, media, audio), deduplicated. */
    fun collectFilePaths(envelope: BackupEnvelope): List<String> {
        val paths = linkedSetOf<String>()
        envelope.characters.forEach { character ->
            character.portraitUri?.let { paths += it }
        }
        envelope.chronicles.forEach { bundle ->
            bundle.mediaAssets.forEach { asset ->
                paths += asset.originalFilePath
                asset.thumbnailFilePath?.let { paths += it }
            }
            bundle.audioTracks.forEach { track -> paths += track.filePath }
        }
        return paths.toList()
    }

    /** original path -> zip entry name (`files/0000_name.ext`), deterministic and sanitized. */
    fun buildFileMap(paths: List<String>): Map<String, String> =
        paths.distinct().sorted().mapIndexed { index, path ->
            val name = path
                .substringAfterLast('/')
                .substringAfterLast('\\')
                .ifEmpty { "file" }
                .replace(Regex("[^A-Za-z0-9._-]"), "_")
            path to "$FILE_PREFIX%04d_%s".format(index, name)
        }.toMap()

    fun encode(envelope: BackupEnvelope): String = json.encodeToString(envelope)

    fun decode(text: String): BackupEnvelope {
        val envelope = try {
            json.decodeFromString<BackupEnvelope>(text)
        } catch (e: Exception) {
            throw BackupFormatException("invalid backup json: ${e.message}", e)
        }
        if (envelope.schemaVersion > SCHEMA_VERSION) {
            throw BackupVersionException("backup schema ${envelope.schemaVersion} > $SCHEMA_VERSION")
        }
        return envelope
    }

    /**
     * Writes `backup.json` first, then streams every file in `fileMap`.
     * Missing files (openFile returns null) are skipped.
     */
    fun writeZip(envelope: BackupEnvelope, output: OutputStream, openFile: (String) -> InputStream?) {
        ZipOutputStream(output.buffered()).use { zip ->
            zip.putNextEntry(ZipEntry(ENTRY_JSON))
            zip.write(encode(envelope).toByteArray(Charsets.UTF_8))
            zip.closeEntry()
            envelope.fileMap.forEach { (originalPath, entryName) ->
                val stream = openFile(originalPath) ?: return@forEach
                stream.use {
                    zip.putNextEntry(ZipEntry(entryName))
                    it.copyTo(zip)
                    zip.closeEntry()
                }
            }
        }
    }

    /**
     * Streams the archive: parses `backup.json` (must be the first entry), extracts every
     * `files/` entry through [extract] (which returns the new absolute path) and rewrites
     * all model path fields to the extracted locations.
     */
    fun readZip(input: InputStream, extract: (entryName: String, stream: InputStream) -> String): BackupEnvelope {
        val zip = ZipInputStream(input.buffered())
        var envelope: BackupEnvelope? = null
        val entryToNewPath = HashMap<String, String>()
        var entry = zip.nextEntry
        var guard = 0
        while (entry != null) {
            when {
                entry.name == ENTRY_JSON -> {
                    val text = zip.readBytes().toString(Charsets.UTF_8)
                    envelope = decode(text)
                }
                entry.name.startsWith(FILE_PREFIX) && !entry.isDirectory -> {
                    entryToNewPath[entry.name] = extract(entry.name, zip)
                }
            }
            zip.closeEntry()
            entry = zip.nextEntry
            if (++guard > 200_000) throw BackupFormatException("zip entry limit exceeded")
        }
        val env = envelope ?: throw BackupFormatException("missing $ENTRY_JSON entry")
        return remapPaths(env) { original ->
            env.fileMap[original]?.let { entryToNewPath[it] }
        }
    }

    private fun remapPaths(env: BackupEnvelope, resolve: (String) -> String?): BackupEnvelope = env.copy(
        characters = env.characters.map { character ->
            character.copy(portraitUri = character.portraitUri?.let { resolve(it) ?: it })
        },
        chronicles = env.chronicles.map { bundle ->
            bundle.copy(
                mediaAssets = bundle.mediaAssets.map { asset ->
                    asset.copy(
                        originalFilePath = resolve(asset.originalFilePath) ?: asset.originalFilePath,
                        thumbnailFilePath = asset.thumbnailFilePath?.let { resolve(it) ?: it }
                    )
                },
                audioTracks = bundle.audioTracks.map { track ->
                    track.copy(filePath = resolve(track.filePath) ?: track.filePath)
                }
            )
        }
    )
}
