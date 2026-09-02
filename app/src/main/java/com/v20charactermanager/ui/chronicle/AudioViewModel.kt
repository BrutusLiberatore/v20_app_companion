package com.v20charactermanager.ui.chronicle

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.v20charactermanager.data.repository.AudioRepositoryImpl
import com.v20charactermanager.domain.model.AudioTrack
import com.v20charactermanager.domain.model.AudioTrackCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID

data class AudioMixUiState(
    val tracks: List<AudioTrack> = emptyList(),
    val isImporting: Boolean = false,
    val message: String? = null,
    val error: String? = null
)

class AudioViewModel(
    private val audioRepository: AudioRepositoryImpl,
    private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(AudioMixUiState())
    val uiState: StateFlow<AudioMixUiState> = _uiState.asStateFlow()

    private val mediaPlayers = mutableMapOf<String, MediaPlayer>()

    init {
        viewModelScope.launch {
            audioRepository.tracks.collect { tracks ->
                _uiState.update { it.copy(tracks = tracks) }
            }
        }
    }

    fun loadTracks(chronicleId: String) {
        viewModelScope.launch {
            audioRepository.loadTracks(chronicleId)
        }
    }

    fun importAudio(chronicleId: String, uri: Uri, title: String, category: AudioTrackCategory) {
        viewModelScope.launch {
            _uiState.update { it.copy(isImporting = true) }
            try {
                val assetId = UUID.randomUUID().toString()
                val mimeType = context.contentResolver.getType(uri)
                val ext = when {
                    mimeType?.contains("mp3") == true -> "mp3"
                    mimeType?.contains("wav") == true -> "wav"
                    mimeType?.contains("ogg") == true -> "ogg"
                    mimeType?.contains("flac") == true -> "flac"
                    mimeType?.contains("aac") == true -> "aac"
                    mimeType?.contains("m4a") == true -> "m4a"
                    else -> "mp3"
                }
                val fileName = "audio_${assetId}.$ext"
                val inputStream = context.contentResolver.openInputStream(uri)
                if (inputStream == null) {
                    _uiState.update { it.copy(isImporting = false, error = "Impossibile leggere il file") }
                    return@launch
                }
                val dir = File(context.filesDir, "chronicle_audio")
                dir.mkdirs()
                val file = File(dir, fileName)
                file.outputStream().use { output -> inputStream.use { input -> input.copyTo(output) } }

                val track = AudioTrack(
                    id = assetId,
                    chronicleId = chronicleId,
                    title = title,
                    filePath = file.absolutePath,
                    category = category
                )
                audioRepository.insertTrack(track)
                _uiState.update { it.copy(isImporting = false, message = "Audio importato") }
            } catch (e: Exception) {
                _uiState.update { it.copy(isImporting = false, error = e.message ?: "Errore sconosciuto") }
            }
        }
    }

    fun togglePlay(trackId: String) {
        val track = _uiState.value.tracks.find { it.id == trackId } ?: return
        val player = mediaPlayers[trackId]

        if (player != null && player.isPlaying) {
            player.pause()
            updateTrackActive(trackId, false)
        } else if (player != null) {
            player.start()
            updateTrackActive(trackId, true)
        } else {
            startTrack(track)
        }
    }

    private fun startTrack(track: AudioTrack) {
        try {
            val player = MediaPlayer().apply {
                setDataSource(context, Uri.fromFile(File(track.filePath)))
                isLooping = track.isLooping
                setVolume(track.volume, track.volume)
                prepare()
                start()
            }
            player.setOnCompletionListener {
                if (!track.isLooping) {
                    updateTrackActive(track.id, false)
                }
            }
            mediaPlayers[track.id] = player
            updateTrackActive(track.id, true)
        } catch (e: Exception) {
            _uiState.update { it.copy(error = "Errore riproduzione: ${e.message}") }
        }
    }

    fun stopTrack(trackId: String) {
        mediaPlayers[trackId]?.let { player ->
            if (player.isPlaying) player.stop()
            player.release()
        }
        mediaPlayers.remove(trackId)
        updateTrackActive(trackId, false)
    }

    fun stopAll() {
        mediaPlayers.forEach { (id, player) ->
            if (player.isPlaying) player.stop()
            player.release()
            updateTrackActive(id, false)
        }
        mediaPlayers.clear()
    }

    fun setVolume(trackId: String, volume: Float) {
        mediaPlayers[trackId]?.let { player ->
            player.setVolume(volume, volume)
        }
        viewModelScope.launch {
            val track = _uiState.value.tracks.find { it.id == trackId } ?: return@launch
            audioRepository.updateTrack(track.copy(volume = volume))
        }
    }

    fun setLooping(trackId: String, looping: Boolean) {
        mediaPlayers[trackId]?.let { player ->
            player.isLooping = looping
        }
        viewModelScope.launch {
            val track = _uiState.value.tracks.find { it.id == trackId } ?: return@launch
            audioRepository.updateTrack(track.copy(isLooping = looping))
        }
    }

    fun deleteTrack(trackId: String, chronicleId: String) {
        stopTrack(trackId)
        viewModelScope.launch {
            val dir = File(context.filesDir, "chronicle_audio")
            dir.listFiles()?.filter { it.name.contains(trackId) }?.forEach { it.delete() }
            audioRepository.deleteTrack(trackId, chronicleId)
        }
    }

    fun clearMessage() {
        _uiState.update { it.copy(message = null) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    private fun updateTrackActive(trackId: String, active: Boolean) {
        _uiState.update { state ->
            state.copy(
                tracks = state.tracks.map {
                    if (it.id == trackId) it.copy(isActive = active) else it
                }
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayers.values.forEach { player ->
            if (player.isPlaying) player.stop()
            player.release()
        }
        mediaPlayers.clear()
    }
}

class AudioViewModelFactory(
    private val audioRepository: AudioRepositoryImpl,
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AudioViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AudioViewModel(audioRepository, context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
