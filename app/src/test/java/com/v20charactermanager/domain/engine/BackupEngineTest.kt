package com.v20charactermanager.domain.engine

import com.v20charactermanager.domain.definition.ClanId
import com.v20charactermanager.domain.model.AudioTrack
import com.v20charactermanager.domain.model.Character
import com.v20charactermanager.domain.model.CharacterIdentity
import com.v20charactermanager.domain.model.Chronicle
import com.v20charactermanager.domain.model.MediaAsset
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

class BackupEngineTest {

    private val portraitPath = "/data/user/0/app/files/portraits/me.png"
    private val mapPath = "/data/user/0/app/files/media/map.png"
    private val thumbPath = "/data/user/0/app/files/media/map_thumb.png"
    private val audioPath = "/data/user/0/app/files/audio/rain.mp3"

    private val character = Character(
        id = "char-1",
        identity = CharacterIdentity(name = "Test One", clan = ClanId.BRUAH, generation = 13),
        portraitUri = portraitPath
    )

    private val bundle = ChronicleBackup(
        chronicle = Chronicle(id = "chron-1", name = "Test Chronicle"),
        mediaAssets = listOf(
            MediaAsset(
                id = "asset-1",
                chronicleId = "chron-1",
                title = "Castle Map",
                originalFilePath = mapPath,
                thumbnailFilePath = thumbPath
            )
        ),
        audioTracks = listOf(
            AudioTrack(id = "track-1", chronicleId = "chron-1", title = "Rain", filePath = audioPath)
        )
    )

    private fun envelope(): BackupEnvelope {
        val base = BackupEnvelope(
            createdAt = 1234567890L,
            characters = listOf(character),
            chronicles = listOf(bundle)
        )
        return base.copy(fileMap = BackupEngine.buildFileMap(BackupEngine.collectFilePaths(base)))
    }

    @Test
    fun `roundtrip preserves structured data and remaps file paths`() {
        val original = envelope()
        val bytes = ByteArrayOutputStream()
        val fileContent = "FAKE-PNG-BYTES".toByteArray()
        BackupEngine.writeZip(original, bytes) { _ -> ByteArrayInputStream(fileContent) }

        val extracted = HashMap<String, ByteArray>()
        val restored = BackupEngine.readZip(ByteArrayInputStream(bytes.toByteArray())) { entryName, stream ->
            val data = stream.readBytes()
            extracted[entryName] = data
            "/restored/$entryName"
        }

        assertEquals(1234567890L, restored.createdAt)
        assertEquals("Test One", restored.characters.single().identity.name)
        assertEquals("Test Chronicle", restored.chronicles.single().chronicle.name)

        val restoredBundle = restored.chronicles.single()
        assertEquals("/restored/files/0003_me.png", restored.characters.single().portraitUri)
        assertEquals("/restored/files/0001_map.png", restoredBundle.mediaAssets.single().originalFilePath)
        assertEquals("/restored/files/0002_map_thumb.png", restoredBundle.mediaAssets.single().thumbnailFilePath)
        assertEquals("/restored/files/0000_rain.mp3", restoredBundle.audioTracks.single().filePath)

        assertEquals(4, extracted.size)
        extracted.values.forEach { assertArrayEquals(fileContent, it) }
    }

    @Test
    fun `backup json is the first zip entry`() {
        val bytes = ByteArrayOutputStream()
        BackupEngine.writeZip(envelope(), bytes) { null }

        val zip = ZipInputStream(ByteArrayInputStream(bytes.toByteArray()))
        val first = zip.nextEntry
        assertEquals(BackupEngine.ENTRY_JSON, first?.name)
        zip.close()
    }

    @Test
    fun `missing files are skipped and paths stay untouched`() {
        val original = envelope()
        val bytes = ByteArrayOutputStream()
        BackupEngine.writeZip(original, bytes) { null }

        val restored = BackupEngine.readZip(ByteArrayInputStream(bytes.toByteArray())) { entryName, _ ->
            fail("no entry should be extracted: $entryName")
            entryName
        }

        assertEquals(portraitPath, restored.characters.single().portraitUri)
        assertEquals(mapPath, restored.chronicles.single().mediaAssets.single().originalFilePath)
        assertEquals(audioPath, restored.chronicles.single().audioTracks.single().filePath)
        assertTrue(restored.fileMap.isNotEmpty())
    }

    @Test
    fun `buildFileMap sanitizes names and keeps entries unique`() {
        val paths = listOf(
            "/storage/emulated/0/My Photos/cool map!!.png",
            "C:\\weird\\name with spaces.mp3",
            "/data/portraits/me.png"
        )
        val map = BackupEngine.buildFileMap(paths)

        assertEquals(3, map.size)
        map.values.forEach { entry ->
            assertTrue(entry.startsWith(BackupEngine.FILE_PREFIX))
            assertTrue(entry.substringAfterLast('/').matches(Regex("[0-9]{4}_[A-Za-z0-9._-]+")))
        }
        assertEquals(map.size, map.values.toSet().size)
        assertTrue(map.keys.containsAll(paths))
    }

    @Test
    fun `decode rejects invalid json`() {
        try {
            BackupEngine.decode("this is not json")
            fail("expected BackupFormatException")
        } catch (e: BackupFormatException) {
            assertTrue(e.message!!.contains("invalid backup json"))
        }
    }

    @Test
    fun `decode rejects future schema version`() {
        try {
            BackupEngine.decode("""{"schemaVersion":99}""")
            fail("expected BackupVersionException")
        } catch (e: BackupVersionException) {
            assertTrue(e.message!!.contains("99"))
        }
    }

    @Test
    fun `readZip rejects archive without backup json`() {
        val bytes = ByteArrayOutputStream()
        ZipOutputStream(bytes).use { zip ->
            zip.putNextEntry(ZipEntry("files/0000_fake.png"))
            zip.write(byteArrayOf(1, 2, 3))
            zip.closeEntry()
        }

        try {
            BackupEngine.readZip(ByteArrayInputStream(bytes.toByteArray())) { name, stream ->
                stream.readBytes()
                name
            }
            fail("expected BackupFormatException")
        } catch (e: BackupFormatException) {
            assertTrue(e.message!!.contains(BackupEngine.ENTRY_JSON))
        }
    }

    @Test
    fun `encode decode is lossless for empty backup`() {
        val empty = BackupEnvelope(createdAt = 42L)
        assertEquals(empty, BackupEngine.decode(BackupEngine.encode(empty)))
    }
}
