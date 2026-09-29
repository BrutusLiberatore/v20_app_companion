package com.v20charactermanager.ui.liveroom

import com.v20charactermanager.R
import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.v20charactermanager.data.network.LiveRoomClient
import com.v20charactermanager.data.network.LiveRoomServer
import com.v20charactermanager.data.network.TableDiscoveryManager
import com.v20charactermanager.data.network.WifiDirectManager
import com.v20charactermanager.domain.definition.DamageType
import com.v20charactermanager.domain.model.*
import com.v20charactermanager.domain.repository.MediaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.util.UUID

class LiveRoomViewModel(
    private val application: Application,
    private val mediaRepository: MediaRepository,
    private val characterRepository: com.v20charactermanager.domain.repository.CharacterRepository? = null,
    private val chronicleRepository: com.v20charactermanager.domain.repository.ChronicleRepository? = null
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

    // Local player's own character (reactive, DB-driven) for the quick status panel
    private val _localCharacter = MutableStateFlow<Character?>(null)
    val localCharacter: StateFlow<Character?> = _localCharacter.asStateFlow()
    private var localCharJob: kotlinx.coroutines.Job? = null

    init {
        viewModelScope.launch {
            _uiState.map { it.localPlayer?.characterId }
                .distinctUntilChanged()
                .collect { id ->
                    localCharJob?.cancel()
                    _localCharacter.value = null
                    val repo = characterRepository
                    if (id.isNullOrBlank() || repo == null) return@collect
                    localCharJob = viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
                        try {
                            repo.getCharacterById(id).collect { char ->
                                _localCharacter.value = char
                            }
                        } catch (e: Exception) {
                            Log.w(TAG, "Failed to observe local character $id", e)
                        }
                    }
                }
        }
    }

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
                        // Push the combat tracker if a fight is running
                        if (style.combat.active) {
                            server?.sendToPlayer(id, LiveRoomMessage.CombatUpdate(style.combat))
                        }
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
                            _uiState.update { it.copy(error = application.getString(com.v20charactermanager.R.string.live_create_room_error, e.message ?: "")) }
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
                applyStatUpdate(message)
                server?.broadcast(message, excludeId = clientId)
            }
            is LiveRoomMessage.DiceRoll -> {
                server?.broadcast(message, excludeId = clientId)
                appendDiceRoll(message)
            }
            is LiveRoomMessage.RollRequest -> {
                // Only the Master originates requests; relay to everybody else
                server?.broadcast(message, excludeId = clientId)
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
                            if (_uiState.value.isMaster) {
                                showToast(
                                    application.getString(
                                        com.v20charactermanager.R.string.live_sheet_received,
                                        message.playerName.ifBlank { char.identity.name }
                                    )
                                )
                            }
                        }
                    } catch (e: Exception) {
                        Log.w(TAG, "Failed to import shared character sheet", e)
                    }
                }
            }
            is LiveRoomMessage.InitiativeRoll -> {
                // Master: merge the player's initiative into the tracker and re-broadcast
                val playerId = _uiState.value.connectedPlayers
                    .firstOrNull { it.id == clientId }?.id
                val combatantId = message.characterId.ifBlank { "player:$clientId" }
                val updated = CombatEngine.upsert(
                    _uiState.value.combat,
                    Combatant(
                        id = combatantId,
                        name = message.playerName,
                        initiative = message.initiative,
                        characterId = message.characterId.ifBlank { null },
                        playerId = playerId ?: clientId
                    )
                )
                _uiState.update { it.copy(combat = updated) }
                server?.broadcast(LiveRoomMessage.CombatUpdate(updated))
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
                    _uiState.update { it.copy(error = application.getString(com.v20charactermanager.R.string.live_file_too_large, file.length() / 1024 / 1024)) }
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
                _uiState.update { it.copy(error = application.getString(com.v20charactermanager.R.string.live_file_too_large_mem)) }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to present asset", e)
            }
        }
    }

    /** Master: sends a chronicle file to specific players, who save it into their chronicle. */
    fun shareAsset(assetId: String, fileName: String, mimeType: String, targetIds: List<String>) {
        if (targetIds.isEmpty()) return
        val asset = _uiState.value.chronicleAssets.find { it.id == assetId } ?: return
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val file = java.io.File(asset.originalFilePath)
                if (!file.exists()) return@launch

                val isImage = mimeType.startsWith("image/") && !mimeType.contains("gif") && !mimeType.contains("svg")
                val maxBytes = 4 * 1024 * 1024L // 4MB limit for Base64 transfer

                var outName = fileName
                var outMime = mimeType
                val bytes = if (isImage && file.length() > maxBytes) {
                    outName = fileName.substringBeforeLast('.') + ".jpg"
                    outMime = "image/jpeg"
                    compressImage(file, maxBytes.toInt())
                } else if (file.length() > maxBytes * 2) {
                    Log.w(TAG, "File too large for TCP transfer: ${file.length()} bytes")
                    _uiState.update { it.copy(error = application.getString(com.v20charactermanager.R.string.live_file_too_large, file.length() / 1024 / 1024)) }
                    return@launch
                } else {
                    file.readBytes()
                }

                val b64 = android.util.Base64.encodeToString(bytes, android.util.Base64.NO_WRAP)
                val message = LiveRoomMessage.SharedFile(outName, outMime, b64)
                targetIds.forEach { server?.sendToPlayer(it, message) }
                Log.d(TAG, "Shared asset $outName to ${targetIds.size} player(s) (${bytes.size} bytes)")
            } catch (e: OutOfMemoryError) {
                Log.e(TAG, "OOM sharing asset", e)
                _uiState.update { it.copy(error = application.getString(com.v20charactermanager.R.string.live_file_too_large_mem)) }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to share asset", e)
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

    fun rollDice(pool: Int, difficulty: Int, isPrivate: Boolean = false, label: String = "") {
        rollWith(
            com.v20charactermanager.domain.model.RollSpec(
                pool = pool,
                difficulty = difficulty,
                isPrivate = isPrivate,
                reason = label
            )
        )
    }

    fun rollWith(spec: com.v20charactermanager.domain.model.RollSpec) {
        val result = com.v20charactermanager.domain.engine.DiceEngine.roll(
            pool = spec.pool,
            difficulty = spec.difficulty,
            diceModifier = spec.diceModifier,
            willpowerUsed = spec.willpowerUsed,
            explodingTens = spec.explodingTens
        )
        val summary = when {
            result.isBotch -> application.getString(com.v20charactermanager.R.string.live_roll_result_botch)
            result.isSuccess -> application.getString(com.v20charactermanager.R.string.live_roll_result_successes, result.netSuccesses)
            else -> application.getString(com.v20charactermanager.R.string.live_roll_result_failure)
        }
        val state = _uiState.value
        val privateRoll = spec.isPrivate && state.isMaster
        val finalPool = (spec.pool + spec.diceModifier + if (spec.willpowerUsed) 1 else 0).coerceAtLeast(1)
        val roll = LiveRoomMessage.DiceRoll(
            characterId = state.localPlayer?.characterId ?: "",
            playerName = state.localPlayer?.name ?: state.room?.masterName ?: "Master",
            pool = finalPool.toString(),
            result = summary,
            dice = result.individualResults,
            difficulty = spec.difficulty,
            label = composeRollLabel(spec),
            isPrivate = privateRoll,
            timestamp = System.currentTimeMillis(),
            isBotch = result.isBotch,
            netSuccesses = result.netSuccesses
        )
        // Local copy keeps full details (the Master sees his own private rolls)
        appendDiceRoll(roll)
        // Wire copy is redacted for private rolls: nobody else learns pool, dice or result
        val outgoing = if (privateRoll) {
            roll.copy(
                dice = emptyList(),
                pool = "",
                label = "",
                result = application.getString(com.v20charactermanager.R.string.live_roll_private_result),
                isBotch = null,
                netSuccesses = null
            )
        } else {
            roll
        }
        if (state.isMaster) {
            server?.broadcast(outgoing)
        } else {
            client?.sendMessage(outgoing)
        }
    }

    private fun composeRollLabel(spec: com.v20charactermanager.domain.model.RollSpec): String {
        val flags = mutableListOf<String>()
        if (spec.diceModifier != 0) {
            flags += if (spec.diceModifier > 0) "+${spec.diceModifier}" else spec.diceModifier.toString()
        }
        if (spec.willpowerUsed) flags += application.getString(com.v20charactermanager.R.string.dice_willpower)
        if (spec.explodingTens) flags += application.getString(com.v20charactermanager.R.string.dice_exploding_tens)
        val flagText = flags.joinToString(" · ")
        return when {
            spec.reason.isNotBlank() && flagText.isNotBlank() -> "${spec.reason} · $flagText"
            else -> spec.reason.ifBlank { flagText }
        }
    }

    /** Master: send a roll request to one player (or everyone when targetPlayerId is blank). */
    fun requestRoll(spec: com.v20charactermanager.domain.model.RollSpec, targetPlayerId: String) {
        val state = _uiState.value
        if (!state.isMaster) return
        val request = LiveRoomMessage.RollRequest(
            targetPlayerId = targetPlayerId,
            requesterName = state.localPlayer?.name ?: state.room?.masterName ?: "Master",
            pool = spec.pool,
            difficulty = spec.difficulty,
            diceModifier = spec.diceModifier,
            willpowerUsed = spec.willpowerUsed,
            explodingTens = spec.explodingTens,
            reason = spec.reason
        )
        server?.broadcast(request)
    }

    /** Player: execute (or decline) a roll request received from the Master. */
    fun answerRollRequest(accept: Boolean) {
        val request = _uiState.value.rollRequest ?: return
        _uiState.update { it.copy(rollRequest = null) }
        if (!accept) return
        rollWith(
            com.v20charactermanager.domain.model.RollSpec(
                pool = request.pool,
                difficulty = request.difficulty,
                diceModifier = request.diceModifier,
                willpowerUsed = request.willpowerUsed,
                explodingTens = request.explodingTens,
                reason = request.reason
            )
        )
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
                client = LiveRoomClient(application)
                client!!.setCallbacks(
                    onMessage = { message ->
                        Log.d(TAG, "Client message: ${message::class.simpleName}")
                        _uiState.update { it.copy(connectionStatus = application.getString(com.v20charactermanager.R.string.live_received_format, message::class.simpleName ?: "")) }
                        handleClientMessage(message)
                    },
                    onDisconnected = {
                        Log.d(TAG, "Client disconnected callback")
                        _uiState.update { state ->
                            if (state.isConnected) {
                                state.copy(isConnected = false, error = application.getString(com.v20charactermanager.R.string.live_connection_lost), connectionStatus = "")
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
                _uiState.update { it.copy(error = e.message ?: application.getString(R.string.unknown_error)) }
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
                        Log.d(TAG, "WiFi Direct connected to peer, joining TCP")
                        // Wait for the group to form, then join the master's server
                        viewModelScope.launch {
                            kotlinx.coroutines.delay(2000)
                            joinRoom(
                                wifiDirectManager.getGroupOwnerAddress(),
                                com.v20charactermanager.data.network.LiveRoomServer.TABLE_PORT,
                                playerName,
                                characterId
                            )
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
            is LiveRoomMessage.SharedFile -> {
                receiveSharedFile(message)
            }
            is LiveRoomMessage.FullscreenFile -> {
                _uiState.update { it.copy(isFileFullscreen = message.isFullscreen) }
            }
            is LiveRoomMessage.RevealHandout -> {
                _uiState.update { it.copy(revealedHandout = message) }
                showToast(application.getString(R.string.live_reveal_received, message.title))
            }
            is LiveRoomMessage.StatUpdate -> {
                applyStatUpdate(message)
                persistOwnStat(message)
            }
            is LiveRoomMessage.DiceRoll -> {
                appendDiceRoll(message)
            }
            is LiveRoomMessage.RollRequest -> {
                val localId = _uiState.value.localPlayer?.id
                if (message.targetPlayerId.isBlank() || message.targetPlayerId == localId) {
                    _uiState.update { it.copy(rollRequest = message) }
                }
            }
            is LiveRoomMessage.CombatUpdate -> {
                _uiState.update { it.copy(combat = message.state) }
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
                        rollRequest = null,
                        revealedHandout = null,
                        combat = CombatState(),
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

    fun sendStatUpdate(characterId: String, field: String, intValue: Int?, stringValue: String? = null) {
        val update = LiveRoomMessage.StatUpdate(characterId, field, intValue, stringValue)
        if (_uiState.value.isMaster && server != null) {
            applyStatUpdate(update)
            server?.broadcast(update)
        } else {
            client?.sendMessage(update)
        }
    }

    /** Client: persists a status change received for the local player's own character. */
    private fun persistOwnStat(message: LiveRoomMessage.StatUpdate) {
        val repo = characterRepository ?: return
        val ownId = _uiState.value.localPlayer?.characterId ?: return
        if (message.characterId != ownId) return
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val char = repo.getCharacterByIdOnce(ownId) ?: return@launch
                val updated = char.applyStatUpdate(message.field, message.intValue, message.stringValue)
                    ?: return@launch
                repo.updateCharacter(updated)
            } catch (e: Exception) {
                Log.w(TAG, "Failed to persist stat update", e)
            }
        }
    }

    // --- QUICK STATUS (player edits own character; persisted locally + broadcast) ---

    private fun editOwnStatus(
        field: String,
        intValue: Int?,
        stringValue: String?,
        transform: (Character) -> Character
    ) {
        val char = _localCharacter.value ?: return
        val updated = transform(char)
        if (updated == char) return
        val stamped = updated.copy(updatedAt = System.currentTimeMillis())
        _localCharacter.value = stamped
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                characterRepository?.updateCharacter(stamped)
            } catch (e: Exception) {
                Log.w(TAG, "Failed to save status change", e)
            }
        }
        sendStatUpdate(char.id, field, intValue, stringValue)
    }

    fun tableSpendBlood() {
        val c = _localCharacter.value ?: return
        val v = (c.bloodPool.current - 1).coerceIn(0, c.bloodPool.maximum)
        editOwnStatus("blood", v, null) { it.copy(bloodPool = it.bloodPool.copy(current = v)) }
    }

    fun tableRefillBlood() {
        val c = _localCharacter.value ?: return
        val v = (c.bloodPool.current + 1).coerceIn(0, c.bloodPool.maximum)
        editOwnStatus("blood", v, null) { it.copy(bloodPool = it.bloodPool.copy(current = v)) }
    }

    fun tableSpendWillpower() {
        val c = _localCharacter.value ?: return
        val v = (c.willpower.current - 1).coerceIn(0, c.willpower.permanent)
        editOwnStatus("willpower", v, null) { it.copy(willpower = it.willpower.copy(current = v)) }
    }

    fun tableRecoverWillpower() {
        val c = _localCharacter.value ?: return
        val v = (c.willpower.current + 1).coerceIn(0, c.willpower.permanent)
        editOwnStatus("willpower", v, null) { it.copy(willpower = it.willpower.copy(current = v)) }
    }

    fun tableApplyDamage(index: Int, type: DamageType) {
        val c = _localCharacter.value ?: return
        if (index !in c.health.levels.indices) return
        editOwnStatus("health", null, "$index:${type.name}") {
            it.copy(health = it.health.withDamage(index, type))
        }
    }

    fun tableHealDamage(index: Int) {
        val c = _localCharacter.value ?: return
        if (index !in c.health.levels.indices) return
        editOwnStatus("health", null, "$index:HEAL") {
            it.copy(health = it.health.heal(index))
        }
    }

    /** Master: corrects any player's status directly (shared map + broadcast). */
    fun masterEditStatus(characterId: String, field: String, intValue: Int?, stringValue: String?) {
        if (!_uiState.value.isMaster) return
        val update = LiveRoomMessage.StatUpdate(characterId, field, intValue, stringValue)
        applyStatUpdate(update)
        server?.broadcast(update)
    }

    /**
     * Player: receives a file shared by the Master and saves it into a chronicle.
     * Matching chronicles (by campaign name, or "campaign — master") are reused;
     * otherwise a new chronicle named "campaign — master" is created.
     */
    private fun receiveSharedFile(message: LiveRoomMessage.SharedFile) {
        val repo = chronicleRepository
        val room = _uiState.value.room
        if (repo == null || room == null) {
            Log.e(TAG, "Cannot save SharedFile: repo=${repo != null}, room=${room != null}")
            showToast(application.getString(com.v20charactermanager.R.string.live_share_failed))
            return
        }
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val bytes = android.util.Base64.decode(message.base64Data, android.util.Base64.NO_WRAP)
                val allChronicles = repo.getAllChronicles().first()
                val compositeName = "${room.name} — ${room.masterName}"
                val chronicle = allChronicles.find { it.name == room.name }
                    ?: allChronicles.find { it.name == compositeName }
                    ?: Chronicle(
                        id = UUID.randomUUID().toString(),
                        name = compositeName,
                        storytellerName = room.masterName,
                        userRole = ChronicleUserRole.PLAYER
                    ).also { repo.insertChronicle(it) }

                val dir = java.io.File(application.filesDir, "chronicle_documents")
                if (!dir.exists()) dir.mkdirs()
                val safeName = message.fileName.replace(Regex("[^A-Za-z0-9._ -]"), "_")
                val outFile = java.io.File(dir, "${UUID.randomUUID()}_$safeName")
                outFile.writeBytes(bytes)

                val lower = message.fileName.lowercase()
                val type = when {
                    message.mimeType == "application/pdf" || lower.endsWith(".pdf") -> MediaAssetType.DOCUMENT
                    message.mimeType.startsWith("video/") ||
                        lower.endsWith(".mp4") || lower.endsWith(".webm") || lower.endsWith(".mkv") -> MediaAssetType.VIDEO
                    message.mimeType.startsWith("image/") ||
                        lower.endsWith(".png") || lower.endsWith(".jpg") || lower.endsWith(".jpeg") ||
                        lower.endsWith(".gif") || lower.endsWith(".webp") -> MediaAssetType.PHOTO
                    else -> MediaAssetType.OTHER
                }
                mediaRepository.insertAsset(
                    MediaAsset(
                        id = UUID.randomUUID().toString(),
                        chronicleId = chronicle.id,
                        type = type,
                        title = message.fileName,
                        originalFilePath = outFile.absolutePath
                    )
                )
                Log.d(TAG, "Saved shared file ${message.fileName} -> chronicle '${chronicle.name}' (${bytes.size} bytes)")
                showToast(
                    application.getString(
                        com.v20charactermanager.R.string.live_share_saved,
                        message.fileName,
                        chronicle.name
                    )
                )
            } catch (e: OutOfMemoryError) {
                Log.e(TAG, "OOM decoding SharedFile", e)
                showToast(application.getString(com.v20charactermanager.R.string.live_share_failed))
            } catch (e: Exception) {
                Log.e(TAG, "Failed to save SharedFile", e)
                showToast(application.getString(com.v20charactermanager.R.string.live_share_failed))
            }
        }
    }

    private fun showToast(text: String) {
        android.os.Handler(application.mainLooper).post {
            android.widget.Toast.makeText(application, text, android.widget.Toast.LENGTH_LONG).show()
        }
    }

    private fun appendDiceRoll(roll: LiveRoomMessage.DiceRoll) {
        _uiState.update {
            it.copy(
                diceRolls = (it.diceRolls + roll).takeLast(8),
                rollLog = (it.rollLog + roll).takeLast(200)
            )
        }
    }

    private fun applyStatUpdate(message: LiveRoomMessage.StatUpdate) {
        _sharedCharacters.update { map ->
            val char = map[message.characterId] ?: return@update map
            val updated = char.applyStatUpdate(message.field, message.intValue, message.stringValue)
                ?: return@update map
            map + (message.characterId to updated)
        }
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

    /**
     * Master: reveal a handout (clue/secret) to every player at the table.
     * Clues are marked SHARED and a CLUE_REVEALED session event is logged when a
     * session is active (Addendum section 63).
     */
    fun revealHandout(clue: Clue? = null, secret: Secret? = null) {
        if (!_uiState.value.isMaster) return
        val id = clue?.id ?: secret?.id ?: return
        val title = clue?.title ?: secret?.title ?: return
        val content = clue?.content ?: secret?.content ?: ""
        val kind = if (clue != null) "clue" else "secret"
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val repo = chronicleRepository
                val chronicleId = _uiState.value.room?.chronicleId ?: ""
                if (clue != null && repo != null && chronicleId.isNotBlank() && clue.status != ClueStatus.SHARED) {
                    repo.updateClue(
                        clue.copy(status = ClueStatus.SHARED, updatedAt = System.currentTimeMillis())
                    )
                    val activeSession = repo.getActiveSession(chronicleId).first()
                    if (activeSession != null && activeSession.chronicleId == chronicleId) {
                        repo.insertSessionEvent(
                            SessionEvent(
                                id = UUID.randomUUID().toString(),
                                chronicleId = chronicleId,
                                sessionId = activeSession.id,
                                sceneId = activeSession.activeSceneId,
                                timestamp = System.currentTimeMillis(),
                                type = SessionEventType.CLUE_REVEALED,
                                title = application.getString(R.string.event_clue_revealed, clue.title),
                                entityRefs = listOf(clue.id),
                                visibility = Visibility.GM_ONLY,
                                origin = "AUTO"
                            )
                        )
                    }
                }
                server?.broadcast(
                    LiveRoomMessage.RevealHandout(id = id, title = title, content = content, kind = kind)
                )
                showToast(application.getString(R.string.live_reveal_sent))
            } catch (e: Exception) {
                Log.w(TAG, "Failed to reveal handout", e)
            }
        }
    }

    fun dismissReveal() {
        _uiState.update { it.copy(revealedHandout = null) }
    }

    // --- COMBAT TRACKER (V20: initiative = 1d10 + Dexterity + Wits) ---

    private fun publishCombat() {
        server?.broadcast(LiveRoomMessage.CombatUpdate(_uiState.value.combat))
    }

    /** Master: start the combat keeping anyone who already rolled initiative. */
    fun startCombat(reRollEachRound: Boolean = true) {
        if (!_uiState.value.isMaster) return
        _uiState.update { it.copy(combat = CombatEngine.start(it.combat.combatants, reRollEachRound)) }
        publishCombat()
        showToast(application.getString(R.string.combat_started))
    }

    fun addCombatant(name: String, initiative: Int) {
        if (!_uiState.value.isMaster) return
        val trimmed = name.trim().ifBlank { return }
        _uiState.update {
            it.copy(
                combat = CombatEngine.upsert(
                    it.combat,
                    Combatant(
                        id = UUID.randomUUID().toString(),
                        name = trimmed,
                        initiative = initiative
                    )
                )
            )
        }
        publishCombat()
    }

    fun removeCombatant(id: String) {
        if (!_uiState.value.isMaster) return
        _uiState.update { it.copy(combat = CombatEngine.remove(it.combat, id)) }
        publishCombat()
    }

    fun advanceCombatTurn() {
        if (!_uiState.value.isMaster) return
        val before = _uiState.value.combat
        val after = CombatEngine.advance(before)
        _uiState.update { it.copy(combat = after) }
        publishCombat()
        if (after.round > before.round && after.reRollEachRound) {
            showToast(application.getString(R.string.combat_round_new, after.round))
        }
    }

    fun setCombatReroll(enabled: Boolean) {
        if (!_uiState.value.isMaster) return
        _uiState.update { it.copy(combat = CombatEngine.setReroll(it.combat, enabled)) }
        publishCombat()
    }

    fun endCombat() {
        if (!_uiState.value.isMaster) return
        _uiState.update { it.copy(combat = CombatEngine.end()) }
        publishCombat()
    }

    /** Player: roll initiative (1d10 + Dexterity + Wits) and send it to the master. */
    fun rollInitiative() {
        val state = _uiState.value
        if (state.isMaster) return
        val charId = state.localPlayer?.characterId
        if (charId.isNullOrBlank()) {
            showToast(application.getString(R.string.combat_no_character))
            return
        }
        val playerName = state.localPlayer?.name ?: ""
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val character = characterRepository?.getCharacterByIdOnce(charId)
                if (character == null) {
                    showToast(application.getString(R.string.combat_no_character))
                    return@launch
                }
                val dex = character.getAttributeValue(com.v20charactermanager.domain.definition.AttributeId.DEXTERITY)
                val wits = character.getAttributeValue(com.v20charactermanager.domain.definition.AttributeId.WITS)
                val d10 = kotlin.random.Random.nextInt(1, 11)
                val initiative = d10 + dex + wits
                client?.sendMessage(
                    LiveRoomMessage.InitiativeRoll(
                        characterId = charId,
                        playerName = playerName,
                        initiative = initiative
                    )
                )
                showToast(
                    application.getString(R.string.combat_roll_result, initiative, d10, dex, wits)
                )
            } catch (e: Exception) {
                Log.w(TAG, "Failed to roll initiative", e)
            }
        }
    }

    /** Player: voluntarily push the current sheet to the Master. */
    fun sendSheetToMaster(character: Character) {
        if (_uiState.value.isMaster || client == null) return
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val jsonStr = com.v20charactermanager.domain.engine.CharacterExporter.export(character)
                client?.sendMessage(
                    LiveRoomMessage.CharacterData(
                        characterId = character.id,
                        playerName = _uiState.value.localPlayer?.name ?: "",
                        characterJson = jsonStr
                    )
                )
                Log.d(TAG, "Sent own sheet to master (${jsonStr.length} chars)")
                showToast(application.getString(com.v20charactermanager.R.string.live_sheet_sent_to_master))
            } catch (e: Exception) {
                Log.w(TAG, "Failed to send sheet to master", e)
            }
        }
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
    private val characterRepository: com.v20charactermanager.domain.repository.CharacterRepository? = null,
    private val chronicleRepository: com.v20charactermanager.domain.repository.ChronicleRepository? = null
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LiveRoomViewModel::class.java)) {
            return LiveRoomViewModel(application, mediaRepository, characterRepository, chronicleRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
