package com.v20charactermanager.data.repository

import com.v20charactermanager.data.local.dao.AudioTrackDao
import com.v20charactermanager.data.local.entity.AudioTrackEntity
import com.v20charactermanager.domain.model.AudioTrack
import com.v20charactermanager.domain.model.AudioTrackCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class AudioRepositoryImpl(
    private val audioTrackDao: AudioTrackDao
) {
    private val _tracks = MutableStateFlow<List<AudioTrack>>(emptyList())
    val tracks: Flow<List<AudioTrack>> = _tracks

    suspend fun loadTracks(chronicleId: String) {
        val entities = audioTrackDao.getByChronicleId(chronicleId)
        _tracks.value = entities.map { it.toDomain() }
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
}
