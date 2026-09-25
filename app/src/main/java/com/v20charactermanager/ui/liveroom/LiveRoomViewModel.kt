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
    private val mediaRepository: MediaRepository,
    private val characterRepository: com.v20charactermanager.domain.repository.CharacterRepository? = null
) : ViewModel() {

    companion object {
        private const val TAG = "LiveRoomViewModel"
    }

    private val stylePrefs = application.getSharedPreferences("live_room_style", android.content.Context.MODE_PRIVATE)

    private val _uiState = MutableStateFlow(
        LiveRoomState(
            tablePack = stylePrefs.getString("table", "medievale") ?: "medievale",
            chairPack = stylePrefs.getString("chair", "medievale") ?: "medievale"
        )
    )
    val uiState: StateFlow<LiveRoomState> = _uiState.asStateFlow()

    // Full character sheets shared by players (master only; shown read-only)
    private val _sharedCharacters = MutableStateFlow<Map<String, Character>>(emptyMap())
    val sharedCharacters: StateFlow<Map<String, Character>> = _sharedCharacters.asStateFlow()
    private val requestedSheets = mutableSetOf<String>()

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
                    onConnected = { id, name, charId ->
                        Log.d(TAG, "Player connected: $name (charId=$charId)")
                        _uiState.update { state ->
                            if (state.connectedPlayers.none { it.id == id }) {
                                state.copy(
                                    connectedPlayers = state.connectedPlayers + ConnectedPlayer(
                                        id = id,
                                        name = name,
                                        characterId = charId
                                    )
                                )
                            } else state
                        }
                        // Push current table style to the new player
                        val style = _uiState.value
                        server?.sendToPlayer(id, LiveRoomMessage.TableStyle(style.tablePack, style.chairPack))
                        // Share character portrait from master's DB (re-encoded so other devices can read it)
                        if (charId != null && characterRepository != null) {
                            viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
                                try {
                                    characterRepository.getCharacterByIdOnce(charId)?.let { char ->
                                        val uri = char.portraitUri
                                        if (!uri.isNullOrBlank() && java.io.File(uri).exists()) {
                                            // Local path is valid on this device only
                                            _uiState.update { state ->
                                                if (charId in state.characterPortraits) state
                                                else state.copy(
                                                    characterPortraits = state.characterPortraits + (charId to uri)
                                                )
                                            }
                                            encodePortraitThumb(uri)?.let { b64 ->
                                                server?.cacheAndBroadcastPortrait(charId, b64)
                                            }
                                        }
                                    }
                                } catch (e: Exception) {
                                    Log.w(TAG, "Failed to load portrait for $charId", e)
                                }
                            }
                        }
                    },
                    onDisconnected = { id, name ->
                        _uiState.update { state ->
                            state.copy(connectedPlayers = state.connectedPlayers.filter { it.id != id })
                        }
                    }
                )

                val hostIp = discoveryManager.getLocalIpAddress()
                Log.d(TAG, "Detected local IP: $hostIp")

                wifiDirectManager.createGroup(
                    onSuccess = {
                        val port = server!!.start()
                        val roomId = UUID.randomUUID().toString().take(8)

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
                        discoveryManager.startBroadcasting(roomName, masterName, chronicleId, port)
                        Log.d(TAG, "Room created: $roomName on $hostIp:$port")
                    },
                    onError = { error ->
                        Log.w(TAG, "WiFi Direct group failed, trying direct: $error")
                        try {
                            val port = server!!.start()
                            val roomId = UUID.randomUUID().toString().take(8)

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
            is LiveRoomMessage.PortraitData -> {
                // Server already cached + broadcast it; just refresh master's own view
                viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
                    val path = savePortraitThumb(message.characterId, message.base64Thumb)
                    if (path != null) {
                        _uiState.update {
                            it.copy(characterPortraits = it.characterPortraits + (message.characterId to path))
                        }
                    }
                }
            }
            is LiveRoomMessage.CharacterData -> {
                // Player shared its sheet with the master: keep it in memory (read-only)
                viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
                    try {
                        val result = com.v20charactermanager.domain.engine.CharacterImporter.import(message.characterJson)
                        result.character?.let { parsed ->
                            val char = parsed.copy(
                                id = message.characterId,
                                portraitUri = _uiState.value.characterPortraits[message.characterId] ?: parsed.portraitUri
                            )
                            _sharedCharacters.update { it + (message.characterId to char) }
                            Log.d(TAG, "Received shared sheet for ${message.characterId}")
                        }
                    } catch (e: Exception) {
                        Log.w(TAG, "Failed to import shared character sheet", e)
                    }
                }
            }
            else -> {}
        }
    }

    fun presentAsset(assetId: String, fileName: String, mimeType: String) {
        val asset = _uiState.value.chronicleAssets.find { it.id == assetId } ?: return
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val file = java.io.File(asset.originalFilePath)
                if (!file.exists()) return@launch

                val isImage = mimeType.startsWith("image/") && !mimeType.contains("gif") && !mimeType.contains("svg")
                val maxBytes = 4 * 1024 * 1024L // 4MB limit for Base64 transfer

                val bytes = if (isImage && file.length() > maxBytes) {
                    compressImage(file, maxBytes.toInt())
                } else if (file.length() > maxBytes * 2) {
                    Log.w(TAG, "File too large for TCP transfer: ${file.length()} bytes")
                    _uiState.update { it.copy(error = "File troppo grande (${file.length() / 1024 / 1024}MB). Massimo 8MB.") }
                    return@launch
                } else {
                    file.readBytes()
                }

                val b64 = android.util.Base64.encodeToString(bytes, android.util.Base64.NO_WRAP)

                // Don't store raw bytes in state for large files to avoid OOM
                val presented = if (bytes.size < 2 * 1024 * 1024) {
                    PresentedFile(
                        id = UUID.randomUUID().toString(),
                        name = fileName,
                        mimeType = mimeType,
                        data = bytes
                    )
                } else {
                    PresentedFile(
                        id = UUID.randomUUID().toString(),
                        name = fileName,
                        mimeType = mimeType,
                        data = byteArrayOf()
                    )
                }
                _uiState.update { it.copy(presentedFile = presented) }
                server?.broadcast(LiveRoomMessage.PresentFile(fileName, mimeType, b64))
                Log.d(TAG, "Presented asset: $fileName (${bytes.size} bytes, b64=${b64.length})")
            } catch (e: OutOfMemoryError) {
                Log.e(TAG, "OOM presenting asset", e)
                _uiState.update { it.copy(error = "File troppo grande per la memoria. Prova con un'immagine più piccola.") }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to present asset", e)
            }
        }
    }

    private fun compressImage(file: java.io.File, maxBytes: Int): ByteArray {
        val bitmap = android.graphics.BitmapFactory.decodeFile(file.absolutePath)
            ?: return file.readBytes()

        var quality = 85
        var scaledBitmap = bitmap
        var bytes: ByteArray

        // Scale down if bitmap is very large
        val maxDimension = 2048
        if (bitmap.width > maxDimension || bitmap.height > maxDimension) {
            val scale = maxDimension.toFloat() / maxOf(bitmap.width, bitmap.height)
            scaledBitmap = android.graphics.Bitmap.createScaledBitmap(
                bitmap,
                (bitmap.width * scale).toInt(),
                (bitmap.height * scale).toInt(),
                true
            )
            if (scaledBitmap !== bitmap) bitmap.recycle()
        }

        do {
            val stream = java.io.ByteArrayOutputStream()
            scaledBitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, quality, stream)
            bytes = stream.toByteArray()
            quality -= 10
        } while (bytes.size > maxBytes && quality > 10)

        if (scaledBitmap !== bitmap) scaledBitmap.recycle()
        bitmap.recycle()

        Log.d(TAG, "Compressed image: ${file.length()} -> ${bytes.size} bytes (quality=$quality)")
        return bytes
    }

    fun dismissFile() {
        _uiState.update { it.copy(presentedFile = null, isFileFullscreen = false) }
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                server?.broadcast(LiveRoomMessage.DismissFile())
            } catch (e: Exception) {
                Log.e(TAG, "Failed to broadcast DismissFile", e)
            }
        }
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
                        _uiState.update { it.copy(connectionStatus = "Ricevuto: ${message::class.simpleName}") }
                        handleClientMessage(message)
                    },
                    onDisconnected = {
                        Log.d(TAG, "Client disconnected callback")
                        _uiState.update { state ->
                            if (state.isConnected) {
                                state.copy(isConnected = false, error = "Connessione persa", connectionStatus = "")
                            } else {
                                state
                            }
                        }
                    },
                    onError = { errorMsg ->
                        Log.e(TAG, "Client error: $errorMsg")
                        _uiState.update { it.copy(error = errorMsg, connectionStatus = "") }
                    },
                    onStatus = { status ->
                        Log.d(TAG, "Client status: $status")
                        _uiState.update { it.copy(connectionStatus = status) }
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
                        localPlayer = it.localPlayer?.copy(id = message.playerId),
                        connectedPlayers = message.players.map {
                            ConnectedPlayer(it.id, it.name, it.characterId)
                        },
                        error = null
                    )
                }
                // Load own portrait locally and share its thumbnail with the table
                val localCharId = _uiState.value.localPlayer?.characterId
                if (localCharId != null && characterRepository != null) {
                    viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
                        try {
                            characterRepository.getCharacterByIdOnce(localCharId)?.let { char ->
                                val localPath = char.portraitUri
                                if (!localPath.isNullOrBlank() && java.io.File(localPath).exists()) {
                                    _uiState.update { state ->
                                        state.copy(
                                            characterPortraits = state.characterPortraits + (localCharId to localPath)
                                        )
                                    }
                                    encodePortraitThumb(localPath)?.let { b64 ->
                                        client?.sendMessage(LiveRoomMessage.PortraitData(localCharId, b64))
                                        Log.d(TAG, "Shared own portrait (${b64.length} b64 chars)")
                                    }
                                }
                                // Share full sheet so the Master can read it
                                try {
                                    val jsonStr = com.v20charactermanager.domain.engine.CharacterExporter.export(char)
                                    client?.sendMessage(
                                        LiveRoomMessage.CharacterData(
                                            characterId = localCharId,
                                            playerName = _uiState.value.localPlayer?.name ?: "",
                                            characterJson = jsonStr
                                        )
                                    )
                                    Log.d(TAG, "Shared own sheet (${jsonStr.length} chars)")
                                } catch (e: Exception) {
                                    Log.w(TAG, "Failed to share own sheet", e)
                                }
                            }
                        } catch (e: Exception) {
                            Log.w(TAG, "Failed to load local portrait", e)
                        }
                    }
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
            is LiveRoomMessage.PortraitUpdate -> {
                _uiState.update { state ->
                    state.copy(
                        characterPortraits = state.characterPortraits + (message.characterId to message.portraitUri)
                    )
                }
            }
            is LiveRoomMessage.PortraitData -> {
                // Never overwrite the local character's own (fresh, device-local) portrait
                if (message.characterId != _uiState.value.localPlayer?.characterId) {
                    viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
                        val path = savePortraitThumb(message.characterId, message.base64Thumb)
                        if (path != null) {
                            _uiState.update {
                                it.copy(characterPortraits = it.characterPortraits + (message.characterId to path))
                            }
                        }
                    }
                }
            }
            is LiveRoomMessage.PresentFile -> {
                val bytes = try {
                    if (message.base64Data.length > 6 * 1024 * 1024) {
                        Log.w(TAG, "Base64 payload too large: ${message.base64Data.length}")
                        byteArrayOf()
                    } else {
                        android.util.Base64.decode(message.base64Data, android.util.Base64.NO_WRAP)
                    }
                } catch (e: OutOfMemoryError) {
                    Log.e(TAG, "OOM decoding PresentFile", e)
                    byteArrayOf()
                } catch (_: Exception) { byteArrayOf() }
                val presented = PresentedFile(
                    id = UUID.randomUUID().toString(),
                    name = message.fileName,
                    mimeType = message.mimeType,
                    data = bytes
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
            is LiveRoomMessage.RoomClosed -> {
                _uiState.update { state ->
                    state.copy(
                        isConnected = false,
                        room = null,
                        localPlayer = null,
                        connectedPlayers = emptyList(),
                        presentedFile = null,
                        isFileFullscreen = false,
                        connectionStatus = "",
                        error = application.getString(com.v20charactermanager.R.string.live_room_closed_by_master)
                    )
                }
                client?.disconnect()
                client = null
            }
            is LiveRoomMessage.TableStyle -> {
                _uiState.update {
                    it.copy(tablePack = message.tablePack, chairPack = message.chairPack)
                }
            }
            is LiveRoomMessage.RequestCharacter -> {
                // The Master asked for our sheet: send the full character JSON back
                val reqCharId = message.characterId
                if (reqCharId.isNotBlank() && characterRepository != null) {
                    viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
                        try {
                            characterRepository.getCharacterByIdOnce(reqCharId)?.let { char ->
                                val jsonStr = com.v20charactermanager.domain.engine.CharacterExporter.export(char)
                                client?.sendMessage(
                                    LiveRoomMessage.CharacterData(
                                        characterId = reqCharId,
                                        playerName = _uiState.value.localPlayer?.name ?: "",
                                        characterJson = jsonStr
                                    )
                                )
                                Log.d(TAG, "Sent sheet data for $reqCharId (${jsonStr.length} chars)")
                            }
                        } catch (e: Exception) {
                            Log.w(TAG, "Failed to send sheet data", e)
                        }
                    }
                }
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

    /** Master: ask the player holding this character for its sheet (deduped, fire-and-forget). */
    fun requestSharedCharacter(characterId: String) {
        if (characterId.isBlank()) return
        if (_sharedCharacters.value.containsKey(characterId)) return
        val playerId = _uiState.value.connectedPlayers
            .firstOrNull { it.characterId == characterId }?.id ?: return
        synchronized(requestedSheets) {
            if (!requestedSheets.add(characterId)) return
        }
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                server?.sendToPlayer(playerId, LiveRoomMessage.RequestCharacter(characterId))
                Log.d(TAG, "Requested sheet for $characterId from $playerId")
            } catch (e: Exception) {
                Log.w(TAG, "Failed to request sheet", e)
            }
        }
    }

    fun closeRoom() {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                server?.broadcast(LiveRoomMessage.RoomClosed)
                Log.d(TAG, "RoomClosed broadcast sent")
            } catch (e: Exception) {
                Log.w(TAG, "Failed to broadcast RoomClosed", e)
            }
            kotlinx.coroutines.delay(400)
            disconnect()
        }
    }

    fun setTableStyle(tablePack: String, chairPack: String) {
        _uiState.update { it.copy(tablePack = tablePack, chairPack = chairPack) }
        stylePrefs.edit().putString("table", tablePack).putString("chair", chairPack).apply()
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                server?.broadcast(LiveRoomMessage.TableStyle(tablePack, chairPack))
            } catch (e: Exception) {
                Log.w(TAG, "Failed to broadcast TableStyle", e)
            }
        }
    }

    fun disconnect() {
        server?.stop()
        server = null
        client?.disconnect()
        client = null
        wifiDirectManager.removeGroup()
        discoveryManager.stopBroadcasting()
        val style = _uiState.value
        _uiState.value = LiveRoomState(tablePack = style.tablePack, chairPack = style.chairPack)
        _sharedCharacters.value = emptyMap()
        synchronized(requestedSheets) { requestedSheets.clear() }
        Log.d(TAG, "Disconnected")
    }

    // --- Character portrait sharing (thumbnails over TCP) ---

    private fun encodePortraitThumb(path: String): String? {
        return try {
            val bounds = android.graphics.BitmapFactory.Options().apply { inJustDecodeBounds = true }
            android.graphics.BitmapFactory.decodeFile(path, bounds)
            if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return null
            var sample = 1
            while (bounds.outWidth / sample > 512 || bounds.outHeight / sample > 512) sample *= 2
            val opts = android.graphics.BitmapFactory.Options().apply { inSampleSize = sample }
            val bmp = android.graphics.BitmapFactory.decodeFile(path, opts) ?: return null
            val stream = java.io.ByteArrayOutputStream()
            bmp.compress(android.graphics.Bitmap.CompressFormat.JPEG, 80, stream)
            bmp.recycle()
            android.util.Base64.encodeToString(stream.toByteArray(), android.util.Base64.NO_WRAP)
        } catch (e: Exception) {
            Log.w(TAG, "Failed to encode portrait thumbnail", e)
            null
        }
    }

    private fun savePortraitThumb(characterId: String, base64Thumb: String): String? {
        return try {
            if (base64Thumb.isEmpty() || base64Thumb.length > 1_500_000) return null
            val bytes = android.util.Base64.decode(base64Thumb, android.util.Base64.NO_WRAP)
            if (bytes.isEmpty()) return null
            val dir = java.io.File(application.cacheDir, "live_portraits")
            if (!dir.exists()) dir.mkdirs()
            val safeId = characterId
                .filter { it.isLetterOrDigit() || it == '-' || it == '_' }
                .take(40)
                .ifBlank { "unknown" }
            val file = java.io.File(dir, "char_$safeId.jpg")
            file.writeBytes(bytes)
            file.absolutePath
        } catch (e: Exception) {
            Log.w(TAG, "Failed to save portrait thumbnail", e)
            null
        }
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
    private val mediaRepository: MediaRepository,
    private val characterRepository: com.v20charactermanager.domain.repository.CharacterRepository? = null
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LiveRoomViewModel::class.java)) {
            return LiveRoomViewModel(application, mediaRepository, characterRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
