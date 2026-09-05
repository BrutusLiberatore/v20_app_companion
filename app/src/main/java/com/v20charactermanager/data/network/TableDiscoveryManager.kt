package com.v20charactermanager.data.network

import android.content.Context
import android.net.wifi.WifiManager
import android.util.Log
import com.v20charactermanager.ui.liveroom.DiscoveredTable
import kotlinx.coroutines.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.NetworkInterface
import java.net.Socket

class TableDiscoveryManager(private val context: Context? = null) {

    companion object {
        private const val TAG = "TableDiscovery"
        const val BROADCAST_PORT = 39642
        const val SERVER_PORT = 39641
        private const val BROADCAST_INTERVAL_MS = 2000L
        private const val SCAN_TIMEOUT_MS = 10000L
        private const val PROBE_TIMEOUT_MS = 300
    }

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val json = Json { ignoreUnknownKeys = true }
    private var broadcastJob: Job? = null
    private var scanJob: Job? = null
    private var scanSocket: DatagramSocket? = null

    fun startBroadcasting(roomName: String, masterName: String, chronicleId: String, port: Int = SERVER_PORT) {
        broadcastJob?.cancel()
        broadcastJob = scope.launch {
            val host = getLocalIpAddress()
            val isEmulator = isEmulatorIp(host)
            if (isEmulator) {
                Log.w(TAG, "WARNING: Detected emulator IP $host - other devices may not be able to connect!")
            }
            Log.d(TAG, "Local IP: $host (emulator=$isEmulator), broadcasting room: $roomName on port $port")
            val data = json.encodeToString(
                DiscoveredTable.serializer(),
                DiscoveredTable(
                    host = host,
                    port = port,
                    roomName = roomName,
                    masterName = masterName,
                    chronicleId = chronicleId
                )
            )
            val buffer = data.toByteArray()

            val socket = DatagramSocket()
            socket.broadcast = true
            socket.reuseAddress = true

            while (isActive) {
                try {
                    val packet = DatagramPacket(
                        buffer, buffer.size,
                        InetAddress.getByName("255.255.255.255"),
                        BROADCAST_PORT
                    )
                    socket.send(packet)

                    val subnet = host.substringBeforeLast(".")
                    if (subnet.isNotEmpty() && subnet != host) {
                        val subPacket = DatagramPacket(
                            buffer, buffer.size,
                            InetAddress.getByName("$subnet.255"),
                            BROADCAST_PORT
                        )
                        socket.send(subPacket)
                    }

                    Log.d(TAG, "Broadcast sent: $data")
                } catch (e: Exception) {
                    Log.e(TAG, "Broadcast error", e)
                }
                delay(BROADCAST_INTERVAL_MS)
            }
            socket.close()
        }
    }

    fun stopBroadcasting() {
        broadcastJob?.cancel()
        broadcastJob = null
    }

    fun scanForTables(onResult: (List<DiscoveredTable>) -> Unit) {
        stopScan()

        scanJob = scope.launch {
            val found = mutableListOf<DiscoveredTable>()

            val socket = DatagramSocket(null)
            socket.reuseAddress = true
            socket.bind(java.net.InetSocketAddress(BROADCAST_PORT))
            socket.soTimeout = SCAN_TIMEOUT_MS.toInt()
            scanSocket = socket

            try {
                val buffer = ByteArray(1024)

                val localIp = getLocalIpAddress()
                val subnet = localIp.substringBeforeLast(".")
                Log.d(TAG, "Scanning subnet: $subnet.* (local IP: $localIp)")

                val probeJob = launch {
                    for (i in 1..254) {
                        launch {
                            try {
                                val ip = "$subnet.$i"
                                val info = requestRoomInfo(ip, SERVER_PORT)
                                if (info != null && found.none { it.host == info.host }) {
                                    synchronized(found) { found.add(info) }
                                    Log.d(TAG, "Found table via TCP: ${info.roomName} at ${info.host}:${info.port}")
                                    withContext(Dispatchers.Main) {
                                        onResult(found.toList())
                                    }
                                }
                            } catch (_: Exception) {}
                        }
                    }
                }

                try {
                    while (isActive) {
                        val packet = DatagramPacket(buffer, buffer.size)
                        socket.receive(packet)
                        val message = String(packet.data, 0, packet.length)
                        try {
                            val table = json.decodeFromString(DiscoveredTable.serializer(), message)
                            if (found.none { it.host == table.host && it.port == table.port }) {
                                synchronized(found) { found.add(table) }
                                Log.d(TAG, "Received broadcast: ${table.roomName} at ${table.host}:${table.port}")
                                withContext(Dispatchers.Main) {
                                    onResult(found.toList())
                                }
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "Failed to parse broadcast: $message", e)
                        }
                    }
                } catch (e: java.net.SocketTimeoutException) {
                    Log.d(TAG, "Scan timeout, found ${found.size} tables")
                } finally {
                    probeJob.cancel()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Scan error", e)
            } finally {
                scanSocket = null
                try { socket.close() } catch (_: Exception) {}
            }

            withContext(Dispatchers.Main) {
                onResult(found)
            }
        }
    }

    fun stopScan() {
        scanJob?.cancel()
        scanJob = null
        try { scanSocket?.close() } catch (_: Exception) {}
        scanSocket = null
    }

    private fun requestRoomInfo(host: String, port: Int): DiscoveredTable? {
        var socket: Socket? = null
        return try {
            socket = Socket()
            socket.connect(InetSocketAddress(host, port), PROBE_TIMEOUT_MS)
            socket.soTimeout = PROBE_TIMEOUT_MS

            val output = socket.getOutputStream()
            val input = socket.getInputStream()
            output.write("QUERY\n".toByteArray())
            output.flush()

            val buffer = ByteArray(2048)
            val read = input.read(buffer)

            if (read > 0) {
                val response = String(buffer, 0, read)
                Log.d(TAG, "QUERY response from $host:$port = $response")
                json.decodeFromString(DiscoveredTable.serializer(), response)
            } else null
        } catch (e: Exception) {
            null
        } finally {
            try { socket?.close() } catch (_: Exception) {}
        }
    }

    fun getLocalIpAddress(): String {
        // Method 1: UDP socket trick (works on all Android versions, no permissions)
        try {
            val udpSocket = DatagramSocket()
            udpSocket.connect(InetAddress.getByName("8.8.8.8"), 53)
            val ip = (udpSocket.localAddress as? java.net.Inet4Address)?.hostAddress
            udpSocket.close()
            if (!ip.isNullOrEmpty() && ip != "0.0.0.0") {
                if (!isEmulatorIp(ip)) {
                    Log.d(TAG, "IP from UDP socket trick: $ip")
                    return ip
                }
                Log.d(TAG, "UDP socket trick returned emulator IP: $ip, trying other methods")
            }
        } catch (e: Exception) {
            Log.w(TAG, "UDP socket trick failed: ${e.message}")
        }

        // Method 2: WifiManager
        try {
            val appContext = context?.applicationContext
            if (appContext != null) {
                @Suppress("DEPRECATION")
                val wm = appContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager
                if (wm != null) {
                    @Suppress("DEPRECATION")
                    val wifiInfo = wm.connectionInfo
                    val ipInt = wifiInfo?.ipAddress ?: 0
                    if (ipInt != 0) {
                        val ip = String.format(
                            "%d.%d.%d.%d",
                            ipInt and 0xff,
                            ipInt shr 8 and 0xff,
                            ipInt shr 16 and 0xff,
                            ipInt shr 24 and 0xff
                        )
                        if (ip != "0.0.0.0" && !isEmulatorIp(ip)) {
                            Log.d(TAG, "IP from WifiManager: $ip")
                            return ip
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "WifiManager failed: ${e.message}")
        }

        // Method 3: NetworkInterface - prefer non-emulator IPs
        try {
            val candidates = mutableListOf<Pair<String, String>>()
            val interfaces = NetworkInterface.getNetworkInterfaces()
            while (interfaces.hasMoreElements()) {
                val nif = interfaces.nextElement()
                if (nif.isLoopback || !nif.isUp) continue
                val addrs = nif.inetAddresses
                while (addrs.hasMoreElements()) {
                    val addr = addrs.nextElement()
                    if (!addr.isLoopbackAddress && addr is java.net.Inet4Address) {
                        val ip = addr.hostAddress ?: continue
                        if (ip.startsWith("192.168.") || ip.startsWith("10.") || ip.startsWith("172.")) {
                            candidates.add(ip to nif.name)
                        }
                    }
                }
            }
            val realIp = candidates.firstOrNull { !isEmulatorIp(it.first) }
            if (realIp != null) {
                Log.d(TAG, "IP from NetworkInterface '${realIp.second}': ${realIp.first}")
                return realIp.first
            }
            if (candidates.isNotEmpty()) {
                Log.d(TAG, "Only emulator IPs found: ${candidates.first().first}")
                return candidates.first().first
            }
        } catch (e: Exception) {
            Log.w(TAG, "NetworkInterface failed: ${e.message}")
        }

        // Method 4: Gateway route scanning for emulator scenarios
        try {
            val gatewayIp = readGatewayFromProcRoute()
            if (gatewayIp != null) {
                Log.d(TAG, "Gateway IP from route: $gatewayIp")
                val hostLanIp = findHostLanIpViaGateway(gatewayIp)
                if (hostLanIp != null) {
                    Log.d(TAG, "Host LAN IP via gateway: $hostLanIp")
                    return hostLanIp
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Gateway scan failed: ${e.message}")
        }

        Log.e(TAG, "Could not determine local IP, returning 0.0.0.0")
        return "0.0.0.0"
    }

    private fun isEmulatorIp(ip: String): Boolean {
        return ip.startsWith("10.0.2.") || ip.startsWith("10.0.3.") || ip == "10.0.2.15"
    }

    private fun readGatewayFromProcRoute(): String? {
        try {
            val routeFile = java.io.File("/proc/net/route")
            if (routeFile.exists()) {
                routeFile.readLines().drop(1).forEach { line ->
                    val parts = line.split("\\s+".toRegex())
                    if (parts.size >= 3) {
                        val gw = parts[2]
                        val gwIp = String.format(
                            "%d.%d.%d.%d",
                            gw.substring(6, 8).toInt(16),
                            gw.substring(4, 6).toInt(16),
                            gw.substring(2, 4).toInt(16),
                            gw.substring(0, 2).toInt(16)
                        )
                        if (gwIp != "0.0.0.0" && !gwIp.startsWith("0.0.")) {
                            return gwIp
                        }
                    }
                }
            }
        } catch (_: Exception) {}
        return null
    }

    private fun findHostLanIpViaGateway(gatewayIp: String): String? {
        try {
            val socket = Socket()
            socket.connect(InetSocketAddress(gatewayIp, 80), 500)
            val localAddr = (socket.localAddress as? java.net.Inet4Address)?.hostAddress
            socket.close()
            if (localAddr != null && !isEmulatorIp(localAddr)) {
                return localAddr
            }
        } catch (_: Exception) {}
        return null
    }

    fun destroy() {
        stopBroadcasting()
        stopScan()
        scope.cancel()
    }
}
