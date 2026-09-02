package com.v20charactermanager.data.repository

import com.v20charactermanager.data.local.dao.AudioPresetDao
import com.v20charactermanager.data.local.dao.AudioTrackDao
import com.v20charactermanager.data.local.entity.AudioPresetEntity
import com.v20charactermanager.data.local.entity.AudioTrackEntity
import com.v20charactermanager.domain.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class AudioRepositoryImpl(
    private val audioTrackDao: AudioTrackDao,
    private val audioPresetDao: AudioPresetDao
) {
    private val _tracks = MutableStateFlow<List<AudioTrack>>(emptyList())
    val tracks: Flow<List<AudioTrack>> = _tracks

    private val _presets = MutableStateFlow<List<AudioPreset>>(emptyList())
    val presets: Flow<List<AudioPreset>> = _presets

    private val json = Json { ignoreUnknownKeys = true }

    suspend fun loadTracks(chronicleId: String) {
        val entities = audioTrackDao.getByChronicleId(chronicleId)
        _tracks.value = entities.map { it.toDomain() }
    }

    suspend fun loadPresets(chronicleId: String) {
        val entities = audioPresetDao.getByChronicleId(chronicleId)
        _presets.value = entities.map { it.toDomain() }
    }

    suspend fun insertTrack(track: AudioTrack) {
        audioTrackDao.insert(track.toEntity())
        loadTracks(track.chronicleId)
    }

    suspend fun updateTrack(track: AudioTrack) {
        audioTrackDao.update(track.toEntity())
        loadTracks(track.chronicleId)
    }

    suspend fun deleteTrack(id: String, chronicleId: String) {
        audioTrackDao.deleteById(id)
        loadTracks(chronicleId)
    }

    suspend fun insertPreset(preset: AudioPreset) {
        audioPresetDao.insert(preset.toEntity())
        loadPresets(preset.chronicleId)
    }

    suspend fun deletePreset(id: String, chronicleId: String) {
        audioPresetDao.deleteById(id)
        loadPresets(chronicleId)
    }

    private fun AudioTrackEntity.toDomain() = AudioTrack(
        id = id,
        chronicleId = chronicleId,
        title = title,
        filePath = filePath,
        category = try { AudioTrackCategory.valueOf(category) } catch (_: Exception) { AudioTrackCategory.CUSTOM },
        isLooping = isLooping,
        volume = volume,
        isActive = isActive,
        createdAt = createdAt
    )

    private fun AudioTrack.toEntity() = AudioTrackEntity(
        id = id,
        chronicleId = chronicleId,
        title = title,
        filePath = filePath,
        category = category.name,
        isLooping = isLooping,
        volume = volume,
        isActive = isActive,
        createdAt = createdAt
    )

    private fun AudioPresetEntity.toDomain() = AudioPreset(
        id = id,
        chronicleId = chronicleId,
        name = name,
        tracks = try { json.decodeFromString(tracksJson) } catch (_: Exception) { emptyList() },
        createdAt = createdAt
    )

    private fun AudioPreset.toEntity() = AudioPresetEntity(
        id = id,
        chronicleId = chronicleId,
        name = name,
        tracksJson = json.encodeToString(tracks),
        createdAt = createdAt
    )
}
