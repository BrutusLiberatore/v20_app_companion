package com.v20charactermanager.ui.liveroom

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.v20charactermanager.data.network.LiveRoomClient
import com.v20charactermanager.data.network.LiveRoomServer
import com.v20charactermanager.data.network.TableDiscoveryManager
import com.v20charactermanager.data.network.WifiDirectManager
import com.v20charactermanager.domain.model.*
import com.v20charactermanager.domain.repository.MediaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.util.UUID

class LiveRoomViewModel(
    private val application: Application,
    private val mediaRepository: MediaRepository
) : ViewModel() {

    companion object {
        private const val TAG = "LiveRoomViewModel"
    }

    private val _uiState = MutableStateFlow(LiveRoomState())
    val uiState: StateFlow<LiveRoomState> = _uiState.asStateFlow()

    private var server: LiveRoomServer? = null
    private var client: LiveRoomClient? = null
    private val json = Json { ignoreUnknownKeys = true; prettyPrint = false }

    val wifiDirectManager = WifiDirectManager(application.applicationContext)
    private val discoveryManager = TableDiscoveryManager(application.applicationContext)

    fun loadChronicleAssets(chronicleId: String) {
        viewModelScope.launch {
            mediaRepository.getAssetsByChronicle(chronicleId).collect { assets ->
                _uiState.update { it.copy(chronicleAssets = assets) }
            }
        }
    }

    // --- MASTER (Server) ---

    fun createRoom(roomName: String, masterName: String, chronicleId: String) {
        viewModelScope.launch {
            try {
                wifiDirectManager.initialize(
                    onGroupFormed = { isGO ->
                        Log.d(TAG, "Group formed, isGroupOwner=$isGO")
                    }
                )

                server = LiveRoomServer(roomName, masterName, chronicleId)
                server!!.setCallbacks(
                    onMessage = { clientId, message -> handleServerMessage(clientId, message) },
                    onConnected = { id, name ->
                        Log.d(TAG, "Player connected: $name")
                    },
                    onDisconnected = { id, name ->
                        _uiState.update { state ->
                            state.copy(connectedPlayers = state.connectedPlayers.filter { it.id != id })
                        }
                    }
                )

                wifiDirectManager.createGroup(
                    onSuccess = {
                        val port = server!!.start()
                        val roomId = UUID.randomUUID().toString().take(8)
                        val hostIp = discoveryManager.getLocalIpAddress()

                        _uiState.update {
                            it.copy(
                                room = LiveRoom(
                                    id = roomId,
                                    name = roomName,
                                    masterName = masterName,
                                    chronicleId = chronicleId,
                                    port = port,
                                    host = hostIp
                                ),
                                isMaster = true,
                                isConnected = true,
                                connectedPlayers = emptyList()
                            )
                        }
                        // Start broadcasting for discovery
                        discoveryManager.startBroadcasting(roomName, masterName, chronicleId, port)
                        Log.d(TAG, "Room created: $roomName on $hostIp:$port")
                    },
                    onError = { error ->
                        // Fallback: try without WiFi Direct group (same network)
                        Log.w(TAG, "WiFi Direct group failed, trying direct: $error")
                        try {
                            val port = server!!.start()
                            val roomId = UUID.randomUUID().toString().take(8)
                            val hostIp = discoveryManager.getLocalIpAddress()

                            _uiState.update {
                                it.copy(
                                    room = LiveRoom(
                                        id = roomId,
                                        name = roomName,
                                        masterName = masterName,
                                        chronicleId = chronicleId,
                                        port = port,
                                        host = hostIp
                                    ),
                                    isMaster = true,
                                    isConnected = true,
                                    connectedPlayers = emptyList(),
                                    error = null
                                )
                            }
                            // Start broadcasting for discovery
                            discoveryManager.startBroadcasting(roomName, masterName, chronicleId, port)
                            Log.d(TAG, "Room created (direct): $roomName on $hostIp:$port")
                        } catch (e: Exception) {
                            _uiState.update { it.copy(error = "Impossibile creare la stanza: ${e.message}") }
                        }
                    }
                )
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
                Log.e(TAG, "Failed to create room", e)
            }
        }
    }

    private fun handleServerMessage(clientId: String, message: LiveRoomMessage) {
        when (message) {
            is LiveRoomMessage.RequestCharacter -> {
                // Master would need to load character data from DB and send it
            }
            is LiveRoomMessage.StatUpdate -> {
                server?.broadcast(message, excludeId = clientId)
            }
            is LiveRoomMessage.DiceRoll -> {
                server?.broadcast(message)
            }
            else -> {}
        }
    }

    fun presentFile(fileName: String, mimeType: String, data: ByteArray) {
        val presented = PresentedFile(
            id = UUID.randomUUID().toString(),
            name = fileName,
            mimeType = mimeType,
            data = data
        )
        _uiState.update { it.copy(presentedFile = presented) }
        server?.broadcast(LiveRoomMessage.PresentFile(fileName, mimeType, data))
    }

    fun dismissFile() {
        _uiState.update { it.copy(presentedFile = null, isFileFullscreen = false) }
        server?.broadcast(LiveRoomMessage.DismissFile(""))
    }

    fun toggleFileFullscreen() {
        val newFullscreen = !_uiState.value.isFileFullscreen
        _uiState.update { it.copy(isFileFullscreen = newFullscreen) }
        server?.broadcast(LiveRoomMessage.FullscreenFile(newFullscreen))
    }

    fun sendStatUpdateToAll(characterId: String, field: String, intValue: Int?) {
        val update = LiveRoomMessage.StatUpdate(characterId, field, intValue)
        server?.broadcast(update)
    }

    fun sendDiceRollToAll(characterId: String, playerName: String, pool: String, result: String, dice: List<Int>) {
        val roll = LiveRoomMessage.DiceRoll(characterId, playerName, pool, result, dice)
        server?.broadcast(roll)
    }

    // --- PLAYER (Client) ---

    private var lastJoinHost = ""
    private var lastJoinPort = 0
    private var lastJoinName = ""
    private var lastJoinCharId: String? = null

    fun joinRoom(host: String, port: Int, playerName: String, characterId: String?) {
        lastJoinHost = host
        lastJoinPort = port
        lastJoinName = playerName
        lastJoinCharId = characterId
        viewModelScope.launch {
            try {
                Log.d(TAG, "joinRoom: host=$host port=$port name=$playerName charId=$characterId")
                // Clear old client callbacks to prevent race conditions
                client?.setCallbacks(
                    onMessage = { },
                    onDisconnected = { },
                    onError = null
                )
                client?.disconnect()
                client = null
                client = LiveRoomClient()
                client!!.setCallbacks(
                    onMessage = { message ->
                        Log.d(TAG, "Client message: ${message::class.simpleName}")
                        handleClientMessage(message)
                    },
                    onDisconnected = {
                        Log.d(TAG, "Client disconnected callback")
                        _uiState.update { state ->
                            if (state.isConnected) {
                                state.copy(isConnected = false, error = "Connessione persa")
                            } else {
                                state
                            }
                        }
                    },
                    onError = { errorMsg ->
                        Log.e(TAG, "Client error: $errorMsg")
                        _uiState.update { it.copy(error = errorMsg) }
                    }
                )
                _uiState.update {
                    it.copy(
                        error = null,
                        localPlayer = ConnectedPlayer(
                            id = "",
                            name = playerName,
                            characterId = characterId
                        )
                    )
                }
                Log.d(TAG, "Calling client.connect($host, $port)")
                client!!.connect(host, port, playerName, characterId)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to join room", e)
                _uiState.update { it.copy(error = e.message ?: "Errore sconosciuto") }
            }
        }
    }

    fun retryJoin() {
        if (lastJoinHost.isNotBlank() && lastJoinPort > 0) {
            joinRoom(lastJoinHost, lastJoinPort, lastJoinName, lastJoinCharId)
        }
    }

    fun connectToPeer(peer: android.net.wifi.p2p.WifiP2pDevice, playerName: String, characterId: String?) {
        viewModelScope.launch {
            try {
                wifiDirectManager.initialize()
                _uiState.update {
                    it.copy(
                        localPlayer = ConnectedPlayer(
                            id = "",
                            name = playerName,
                            characterId = characterId
                        )
                    )
                }
                wifiDirectManager.connectToPeer(
                    device = peer,
                    onConnected = {
                        Log.d(TAG, "WiFi Direct connected to peer")
                        // Wait a moment for group to form, then connect TCP
                        viewModelScope.launch {
                            kotlinx.coroutines.delay(1500)
                            val goAddress = wifiDirectManager.getGroupOwnerAddress()
                            val port = _uiState.value.room?.port ?: 0
                            if (port > 0) {
                                client?.connect(goAddress, port, playerName, characterId)
                            }
                        }
                    },
                    onError = { error ->
                        _uiState.update { it.copy(error = error) }
                    }
                )
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
                Log.e(TAG, "Failed to connect to peer", e)
            }
        }
    }

    fun discoverPeers() {
        wifiDirectManager.discoverPeers()
    }

    private fun handleClientMessage(message: LiveRoomMessage) {
        when (message) {
            is LiveRoomMessage.Welcome -> {
                _uiState.update {
                    it.copy(
                        isConnected = true,
                        room = LiveRoom(
                            id = UUID.randomUUID().toString().take(8),
                            name = message.roomName,
                            masterName = message.masterName,
                            chronicleId = "",
                            port = 0
                        ),
                        connectedPlayers = message.players.map {
                            ConnectedPlayer(it.id, it.name, it.characterId)
                        },
                        error = null
                    )
                }
            }
            is LiveRoomMessage.PlayerJoined -> {
                _uiState.update { state ->
                    state.copy(
                        connectedPlayers = state.connectedPlayers + ConnectedPlayer(
                            id = message.playerId,
                            name = message.playerName,
                            characterId = message.characterId
                        )
                    )
                }
            }
            is LiveRoomMessage.PlayerLeft -> {
                _uiState.update { state ->
                    state.copy(
                        connectedPlayers = state.connectedPlayers.filter { it.id != message.playerId }
                    )
                }
            }
            is LiveRoomMessage.PresentFile -> {
                val presented = PresentedFile(
                    id = UUID.randomUUID().toString(),
                    name = message.fileName,
                    mimeType = message.mimeType,
                    data = message.data
                )
                _uiState.update { it.copy(presentedFile = presented) }
            }
            is LiveRoomMessage.DismissFile -> {
                _uiState.update { it.copy(presentedFile = null, isFileFullscreen = false) }
            }
            is LiveRoomMessage.FullscreenFile -> {
                _uiState.update { it.copy(isFileFullscreen = message.isFullscreen) }
            }
            is LiveRoomMessage.StatUpdate -> {
                // Handle stat update from other players
            }
            is LiveRoomMessage.DiceRoll -> {
                // Handle dice roll from other players
            }
            else -> {}
        }
    }

    fun sendStatUpdate(characterId: String, field: String, intValue: Int?) {
        val update = LiveRoomMessage.StatUpdate(characterId, field, intValue)
        client?.sendMessage(update)
    }

    fun toggleFullscreen() {
        val newFullscreen = !_uiState.value.isFileFullscreen
        _uiState.update { it.copy(isFileFullscreen = newFullscreen) }
        val msg = LiveRoomMessage.FullscreenFile(newFullscreen)
        server?.broadcast(msg)
        client?.sendMessage(msg)
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun disconnect() {
        server?.stop()
        server = null
        client?.disconnect()
        client = null
        wifiDirectManager.removeGroup()
        discoveryManager.stopBroadcasting()
        _uiState.value = LiveRoomState()
        Log.d(TAG, "Disconnected")
    }

    override fun onCleared() {
        super.onCleared()
        disconnect()
        wifiDirectManager.destroy()
        discoveryManager.destroy()
    }
}

class LiveRoomViewModelFactory(
    private val application: Application,
    private val mediaRepository: MediaRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LiveRoomViewModel::class.java)) {
            return LiveRoomViewModel(application, mediaRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
