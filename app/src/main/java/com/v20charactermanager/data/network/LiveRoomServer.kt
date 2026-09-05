package com.v20charactermanager.data.network

import android.util.Log
import com.v20charactermanager.domain.model.LiveRoomMessage
import com.v20charactermanager.ui.liveroom.DiscoveredTable
import kotlinx.coroutines.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.*
import java.net.ServerSocket
import java.net.Socket
import java.util.UUID

class LiveRoomServer(
    private val roomName: String,
    private val masterName: String,
    private val chronicleId: String
) {
    companion object {
        private const val TAG = "LiveRoomServer"
        const val TABLE_PORT = 39641
    }

    private var serverSocket: ServerSocket? = null
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val json = Json { ignoreUnknownKeys = true; prettyPrint = false }

    private val _connections = mutableMapOf<String, ClientConnection>()
    val connections: Map<String, ClientConnection> get() = _connections.toMap()

    private var onClientMessage: ((clientId: String, message: LiveRoomMessage) -> Unit)? = null
    private var onClientConnected: ((clientId: String, playerName: String) -> Unit)? = null
    private var onClientDisconnected: ((clientId: String, playerName: String) -> Unit)? = null

    data class ClientConnection(
        val id: String,
        val playerName: String,
        val characterId: String?,
        val writer: BufferedWriter,
        val socket: Socket
    )

    fun setCallbacks(
        onMessage: (String, LiveRoomMessage) -> Unit,
        onConnected: (String, String) -> Unit,
        onDisconnected: (String, String) -> Unit
    ) {
        onClientMessage = onMessage
        onClientConnected = onConnected
        onClientDisconnected = onDisconnected
    }

    fun start(): Int {
        try {
            serverSocket = ServerSocket(TABLE_PORT)
        } catch (e: Exception) {
            Log.w(TAG, "Port $TABLE_PORT busy, trying random port")
            serverSocket = ServerSocket(0)
        }
        val port = serverSocket!!.localPort
        Log.d(TAG, "Server started on port $port")

        // Accept connections
        scope.launch {
            while (isActive) {
                try {
                    val clientSocket = serverSocket?.accept() ?: break
                    handleNewClient(clientSocket)
                } catch (e: Exception) {
                    if (isActive) Log.e(TAG, "Error accepting connection", e)
                }
            }
        }

        return port
    }

    private fun handleNewClient(socket: Socket) {
        scope.launch {
            val clientId = UUID.randomUUID().toString().take(8)
            val clientAddr = socket.inetAddress?.hostAddress ?: "?"
            try {
                Log.d(TAG, "New connection from $clientAddr")
                val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
                val writer = BufferedWriter(OutputStreamWriter(socket.getOutputStream()))
                writer.flush()

                // Read first line - could be QUERY or JOIN
                val firstLine = reader.readLine() ?: run {
                    Log.w(TAG, "Client $clientAddr sent no data, closing")
                    socket.close()
                    return@launch
                }

                Log.d(TAG, "Client $clientAddr first line: ${firstLine.take(200)}")

                // Handle QUERY request (for table discovery)
                if (firstLine.trim() == "QUERY") {
                    val serverHost = getServerHostIp(socket)
                    Log.d(TAG, "QUERY from $clientAddr, responding with $serverHost:${serverSocket?.localPort}")
                    val response = json.encodeToString(
                        DiscoveredTable.serializer(),
                        DiscoveredTable(
                            host = serverHost,
                            port = serverSocket?.localPort ?: socket.localPort,
                            roomName = roomName,
                            masterName = masterName,
                            chronicleId = chronicleId
                        )
                    )
                    Log.d(TAG, "QUERY response: $response")
                    writer.write(response)
                    writer.newLine()
                    writer.flush()
                    Thread.sleep(50)
                    socket.close()
                    return@launch
                }

                // Otherwise treat as JOIN
                Log.d(TAG, "Attempting to parse JOIN from $clientAddr: ${firstLine.take(200)}")
                val joinMsg = json.decodeFromString<LiveRoomMessage.Join>(firstLine)
                Log.d(TAG, "JOIN parsed: playerName=${joinMsg.playerName}, characterId=${joinMsg.characterId}")

                val connection = ClientConnection(
                    id = clientId,
                    playerName = joinMsg.playerName,
                    characterId = joinMsg.characterId,
                    writer = writer,
                    socket = socket
                )
                _connections[clientId] = connection
                Log.d(TAG, "Client joined: ${joinMsg.playerName} ($clientId)")

                // Send WELCOME with current players
                val playerInfos = _connections.values.map {
                    LiveRoomMessage.PlayerInfo(it.id, it.playerName, it.characterId)
                }
                val welcome = LiveRoomMessage.Welcome(
                    playerId = clientId,
                    roomName = roomName,
                    masterName = masterName,
                    players = playerInfos
                )
                sendToClient(writer, welcome)
                Log.d(TAG, "WELCOME sent to ${joinMsg.playerName} ($clientId), total players: ${_connections.size}")

                // Notify others
                broadcast(
                    LiveRoomMessage.PlayerJoined(
                        playerName = joinMsg.playerName,
                        playerId = clientId,
                        characterId = joinMsg.characterId
                    ),
                    excludeId = clientId
                )

                onClientConnected?.invoke(clientId, joinMsg.playerName)

                // Listen for messages
                Log.d(TAG, "Entering message loop for ${joinMsg.playerName} ($clientId)")
                while (socket.isConnected && !socket.isClosed) {
                    val line = reader.readLine()
                    if (line == null) {
                        Log.d(TAG, "Client ${joinMsg.playerName} read returned null (disconnected)")
                        break
                    }
                    if (line.isBlank()) continue
                    try {
                        val message = json.decodeFromString<LiveRoomMessage>(line)
                        onClientMessage?.invoke(clientId, message)
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed to parse message: $line", e)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Client error: $clientId ($clientAddr)", e)
            } finally {
                val playerName = _connections[clientId]?.playerName ?: clientId
                _connections.remove(clientId)
                try { socket.close() } catch (_: Exception) {}
                onClientDisconnected?.invoke(clientId, playerName)
                broadcast(LiveRoomMessage.PlayerLeft(playerName, clientId))
                Log.d(TAG, "Client disconnected: $playerName ($clientId)")
            }
        }
    }

    fun sendToClient(writer: BufferedWriter, message: LiveRoomMessage) {
        try {
            val jsonStr = json.encodeToString(message)
            writer.write(jsonStr)
            writer.newLine()
            writer.flush()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to send message", e)
        }
    }

    fun broadcast(message: LiveRoomMessage, excludeId: String? = null) {
        _connections.forEach { (id, conn) ->
            if (id != excludeId) {
                sendToClient(conn.writer, message)
            }
        }
    }

    fun sendToPlayer(playerId: String, message: LiveRoomMessage) {
        _connections[playerId]?.let { sendToClient(it.writer, message) }
    }

    private fun getServerHostIp(clientSocket: Socket): String {
        val clientAddr = clientSocket.inetAddress?.hostAddress ?: return getFallbackHostIp()
        try {
            val interfaces = java.net.NetworkInterface.getNetworkInterfaces()
            while (interfaces.hasMoreElements()) {
                val nif = interfaces.nextElement()
                if (nif.isLoopback || !nif.isUp) continue
                val addrs = nif.inetAddresses
                while (addrs.hasMoreElements()) {
                    val addr = addrs.nextElement()
                    if (addr is java.net.Inet4Address && !addr.isLoopbackAddress) {
                        val subnet = addr.hostAddress?.substringBeforeLast(".") ?: continue
                        if (clientAddr.startsWith("$subnet.") || clientAddr == addr.hostAddress) {
                            Log.d(TAG, "Matched interface ${nif.name} -> ${addr.hostAddress}")
                            return addr.hostAddress ?: clientAddr
                        }
                    }
                }
            }
        } catch (_: Exception) {}
        return clientAddr
    }

    private fun getFallbackHostIp(): String {
        try {
            val interfaces = java.net.NetworkInterface.getNetworkInterfaces()
            while (interfaces.hasMoreElements()) {
                val nif = interfaces.nextElement()
                if (nif.isLoopback || !nif.isUp) continue
                val addrs = nif.inetAddresses
                while (addrs.hasMoreElements()) {
                    val addr = addrs.nextElement()
                    if (addr is java.net.Inet4Address && !addr.isLoopbackAddress) {
                        val ip = addr.hostAddress ?: continue
                        if (ip.startsWith("192.168.") || ip.startsWith("10.") || ip.startsWith("172.")) {
                            return ip
                        }
                    }
                }
            }
        } catch (_: Exception) {}
        return "0.0.0.0"
    }

    fun stop() {
        scope.cancel()
        _connections.values.forEach { try { it.socket.close() } catch (_: Exception) {} }
        _connections.clear()
        try { serverSocket?.close() } catch (_: Exception) {}
        Log.d(TAG, "Server stopped")
    }
}
