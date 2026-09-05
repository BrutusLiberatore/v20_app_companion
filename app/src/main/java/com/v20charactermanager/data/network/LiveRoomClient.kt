package com.v20charactermanager.data.network

import android.util.Log
import com.v20charactermanager.domain.model.LiveRoomMessage
import kotlinx.coroutines.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.*
import java.net.InetSocketAddress
import java.net.Socket

class LiveRoomClient {
    companion object {
        private const val TAG = "LiveRoomClient"
        private const val CONNECT_TIMEOUT_MS = 8000
        private const val READ_TIMEOUT_MS = 30000
    }

    private var socket: Socket? = null
    private var reader: BufferedReader? = null
    private var writer: BufferedWriter? = null
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val json = Json { ignoreUnknownKeys = true; prettyPrint = false }

    @Volatile private var onMessage: ((LiveRoomMessage) -> Unit)? = null
    @Volatile private var onDisconnected: (() -> Unit)? = null
    @Volatile private var onError: ((String) -> Unit)? = null
    @Volatile private var isDisconnecting = false

    fun setCallbacks(
        onMessage: (LiveRoomMessage) -> Unit,
        onDisconnected: () -> Unit,
        onError: ((String) -> Unit)? = null
    ) {
        this.onMessage = onMessage
        this.onDisconnected = onDisconnected
        this.onError = onError
    }

    fun connect(host: String, port: Int, playerName: String, characterId: String?) {
        isDisconnecting = false
        scope.launch {
            var connected = false
            try {
                Log.d(TAG, "Connecting to $host:$port as $playerName")
                socket = Socket()
                socket!!.connect(InetSocketAddress(host, port), CONNECT_TIMEOUT_MS)
                socket!!.soTimeout = READ_TIMEOUT_MS
                reader = BufferedReader(InputStreamReader(socket!!.getInputStream()))
                writer = BufferedWriter(OutputStreamWriter(socket!!.getOutputStream()))
                writer!!.flush()
                connected = true
                Log.d(TAG, "TCP connected to $host:$port, sending JOIN")

                val join = LiveRoomMessage.Join(playerName, characterId)
                sendMessage(join)
                Log.d(TAG, "JOIN sent: $playerName (char=$characterId)")

                while (socket?.isConnected == true && !socket!!.isClosed) {
                    val line = reader?.readLine() ?: break
                    if (line.isBlank()) continue
                    try {
                        val message = json.decodeFromString<LiveRoomMessage>(line)
                        Log.d(TAG, "Received: ${message::class.simpleName}")
                        onMessage?.invoke(message)
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed to parse: $line", e)
                    }
                }
                Log.d(TAG, "Read loop ended, socket isConnected=${socket?.isConnected} isClosed=${socket?.isClosed}")
            } catch (e: CancellationException) {
                Log.d(TAG, "Connection cancelled (client disconnecting=$isDisconnecting)")
                if (!isDisconnecting) throw e
            } catch (e: java.net.ConnectException) {
                Log.e(TAG, "Connection refused: $host:$port", e)
                if (!isDisconnecting) onError?.invoke("Tavolo non raggiungibile ($host:$port). Verifica l'IP e riprova.")
            } catch (e: java.net.SocketTimeoutException) {
                Log.e(TAG, "Connection timeout: $host:$port", e)
                if (!connected && !isDisconnecting) {
                    onError?.invoke("Timeout di connessione a $host:$port. Il tavolo potrebbe essere su una rete diversa.")
                }
            } catch (e: java.net.UnknownHostException) {
                Log.e(TAG, "Unknown host: $host", e)
                if (!isDisconnecting) onError?.invoke("Indirizzo IP non valido: $host")
            } catch (e: IOException) {
                Log.e(TAG, "IO error: $host:$port (connected=$connected)", e)
                if (!isDisconnecting) {
                    if (connected) {
                        onDisconnected?.invoke()
                    } else {
                        onError?.invoke("Errore di rete: ${e.localizedMessage ?: "verifica la connessione"}")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Connection error: $host:$port", e)
                if (!isDisconnecting) onError?.invoke("Errore di connessione: ${e.localizedMessage ?: "sconosciuto"}")
            } finally {
                if (!isDisconnecting && connected) {
                    Log.d(TAG, "Notifying disconnection (connected was true)")
                    onDisconnected?.invoke()
                }
                try { socket?.close() } catch (_: Exception) {}
                socket = null
                reader = null
                writer = null
            }
        }
    }

    fun sendMessage(message: LiveRoomMessage) {
        try {
            val jsonStr = json.encodeToString(message)
            writer?.write(jsonStr)
            writer?.newLine()
            writer?.flush()
        } catch (e: Exception) {
            Log.e(TAG, "Send failed", e)
        }
    }

    fun disconnect() {
        isDisconnecting = true
        scope.cancel()
        try { socket?.close() } catch (_: Exception) {}
        socket = null
        reader = null
        writer = null
        Log.d(TAG, "Disconnected")
    }
}
