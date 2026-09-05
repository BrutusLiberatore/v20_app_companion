package com.v20charactermanager.data.network

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WpsInfo
import android.net.wifi.p2p.*
import android.os.Build
import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class WifiDirectManager(private val context: Context) {

    companion object {
        private const val TAG = "WifiDirectManager"
        const val GROUP_OWNER_IP = "192.168.49.1"
    }

    private val manager: WifiP2pManager? by lazy {
        context.getSystemService(Context.WIFI_P2P_SERVICE) as? WifiP2pManager
    }
    private var channel: WifiP2pManager.Channel? = null
    private var receiver: BroadcastReceiver? = null
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private val _state = MutableStateFlow(WifiDirectState())
    val state: StateFlow<WifiDirectState> = _state.asStateFlow()

    private var onGroupFormed: ((isGroupOwner: Boolean) -> Unit)? = null
    private var onPeerConnected: ((host: String, port: Int) -> Unit)? = null

    data class WifiDirectState(
        val isWifiP2pEnabled: Boolean = false,
        val isGroupOwner: Boolean = false,
        val isConnected: Boolean = false,
        val groupOwnerAddress: String? = null,
        val peers: List<WifiP2pDevice> = emptyList(),
        val isCreatingGroup: Boolean = false,
        val isDiscovering: Boolean = false,
        val error: String? = null
    )

    @SuppressLint("MissingPermission")
    fun initialize(
        onGroupFormed: (isGroupOwner: Boolean) -> Unit = {},
        onPeerConnected: (host: String, port: Int) -> Unit = { _, _ -> }
    ) {
        this.onGroupFormed = onGroupFormed
        this.onPeerConnected = onPeerConnected

        channel = manager?.initialize(context, android.os.Looper.getMainLooper(), null)

        val intentFilter = IntentFilter().apply {
            addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
        }

        receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context, intent: Intent) {
                when (intent.action) {
                    WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION -> {
                        val state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)
                        _state.value = _state.value.copy(isWifiP2pEnabled = state == WifiP2pManager.WIFI_P2P_STATE_ENABLED)
                        Log.d(TAG, "WiFi P2P state: $state")
                    }
                    WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {
                        requestPeers()
                    }
                    WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {
                        requestConnectionInfo()
                    }
                    WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION -> {
                        // Device info changed
                    }
                }
            }
        }

        context.registerReceiver(receiver, intentFilter)
        Log.d(TAG, "WiFi Direct manager initialized")
    }

    @SuppressLint("MissingPermission")
    fun createGroup(onSuccess: () -> Unit = {}, onError: (String) -> Unit = {}) {
        val mgr = manager ?: run {
            onError("WiFi P2P non disponibile")
            return
        }
        val ch = channel ?: run {
            onError("Canale WiFi P2P non inizializzato")
            return
        }

        _state.value = _state.value.copy(isCreatingGroup = true)

        mgr.createGroup(ch, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                Log.d(TAG, "Group created successfully")
                _state.value = _state.value.copy(
                    isCreatingGroup = false,
                    isGroupOwner = true,
                    isConnected = true,
                    groupOwnerAddress = GROUP_OWNER_IP
                )
                onSuccess()
            }

            override fun onFailure(reason: Int) {
                val errorMsg = when (reason) {
                    WifiP2pManager.ERROR -> "Errore generico WiFi P2P"
                    WifiP2pManager.P2P_UNSUPPORTED -> "WiFi Direct non supportato"
                    WifiP2pManager.BUSY -> "WiFi P2P occupato"
                    else -> "Errore sconosciuto: $reason"
                }
                Log.e(TAG, "Failed to create group: $errorMsg")
                _state.value = _state.value.copy(isCreatingGroup = false, error = errorMsg)
                onError(errorMsg)
            }
        })
    }

    fun removeGroup(onSuccess: () -> Unit = {}) {
        val mgr = manager ?: return
        val ch = channel ?: return

        mgr.removeGroup(ch, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                Log.d(TAG, "Group removed")
                _state.value = WifiDirectState()
                onSuccess()
            }

            override fun onFailure(reason: Int) {
                Log.e(TAG, "Failed to remove group: $reason")
            }
        })
    }

    @SuppressLint("MissingPermission")
    fun discoverPeers(onFound: (List<WifiP2pDevice>) -> Unit = {}) {
        val mgr = manager ?: run {
            onFound(emptyList())
            return
        }
        val ch = channel ?: run {
            onFound(emptyList())
            return
        }

        _state.value = _state.value.copy(isDiscovering = true)

        mgr.discoverPeers(ch, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                Log.d(TAG, "Discovery started")
            }

            override fun onFailure(reason: Int) {
                Log.e(TAG, "Discovery failed: $reason")
                _state.value = _state.value.copy(isDiscovering = false, error = "Discovery fallita: $reason")
                onFound(emptyList())
            }
        })
    }

    @SuppressLint("MissingPermission")
    private fun requestPeers() {
        val mgr = manager ?: return
        val ch = channel ?: return

        mgr.requestPeers(ch) { peerList ->
            val peers = peerList.deviceList.toList()
            Log.d(TAG, "Found ${peers.size} peers")
            peers.forEach { Log.d(TAG, "Peer: ${it.deviceName} (${it.deviceAddress})") }
            _state.value = _state.value.copy(peers = peers, isDiscovering = false)
        }
    }

    @SuppressLint("MissingPermission")
    fun connectToPeer(device: WifiP2pDevice, onConnected: () -> Unit = {}, onError: (String) -> Unit = {}) {
        val mgr = manager ?: run {
            onError("WiFi P2P non disponibile")
            return
        }
        val ch = channel ?: run {
            onError("Canale non inizializzato")
            return
        }

        val config = WifiP2pConfig().apply {
            deviceAddress = device.deviceAddress
            wps.setup = WpsInfo.PBC
        }

        mgr.connect(ch, config, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                Log.d(TAG, "Connection request sent to ${device.deviceName}")
            }

            override fun onFailure(reason: Int) {
                val errorMsg = when (reason) {
                    WifiP2pManager.ERROR -> "Errore di connessione"
                    WifiP2pManager.P2P_UNSUPPORTED -> "WiFi Direct non supportato"
                    WifiP2pManager.BUSY -> "WiFi P2P occupato"
                    else -> "Errore: $reason"
                }
                Log.e(TAG, "Connection failed: $errorMsg")
                onError(errorMsg)
            }
        })
    }

    private fun requestConnectionInfo() {
        val mgr = manager ?: return
        val ch = channel ?: return

        mgr.requestConnectionInfo(ch) { info ->
            if (info != null && info.groupFormed) {
                val isGO = info.isGroupOwner
                val goAddress = info.groupOwnerAddress?.hostAddress ?: GROUP_OWNER_IP
                Log.d(TAG, "Group formed. GO: $isGO, GO Address: $goAddress")
                _state.value = _state.value.copy(
                    isConnected = true,
                    isGroupOwner = isGO,
                    groupOwnerAddress = goAddress
                )
                onGroupFormed?.invoke(isGO)
            } else {
                Log.d(TAG, "Not connected")
                _state.value = _state.value.copy(isConnected = false)
            }
        }
    }

    fun getGroupOwnerAddress(): String {
        return _state.value.groupOwnerAddress ?: GROUP_OWNER_IP
    }

    fun isGroupOwner(): Boolean {
        return _state.value.isGroupOwner
    }

    fun destroy() {
        removeGroup()
        try {
            receiver?.let { context.unregisterReceiver(it) }
        } catch (_: Exception) {}
        scope.cancel()
        Log.d(TAG, "WiFi Direct manager destroyed")
    }
}
