package com.v20charactermanager.ui.liveroom

import android.app.Application
import android.net.wifi.p2p.WifiP2pDevice
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.v20charactermanager.R
import com.v20charactermanager.data.network.TableDiscoveryManager
import com.v20charactermanager.data.network.WifiDirectManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "FindTableViewModel"

class FindTableViewModel(
    private val application: Application
) : ViewModel() {

    private val discoveryManager = TableDiscoveryManager(application)
    private val wifiDirectManager = WifiDirectManager(application)

    private val _uiState = MutableStateFlow(FindTableUiState())
    val uiState: StateFlow<FindTableUiState> = _uiState.asStateFlow()

    private var p2pScanJob: kotlinx.coroutines.Job? = null

    init {
        wifiDirectManager.initialize(
            onGroupFormed = {
                val host = wifiDirectManager.getGroupOwnerAddress()
                _uiState.update { it.copy(isP2pConnecting = false, p2pConnectHost = host) }
            }
        )
        viewModelScope.launch {
            wifiDirectManager.state.collect { p2p ->
                _uiState.update {
                    it.copy(
                        p2pPeers = p2p.peers,
                        p2pError = p2p.error ?: it.p2pError
                    )
                }
            }
        }
    }

    fun startScan() {
        _uiState.update { it.copy(isScanning = true, discoveredTables = emptyList()) }
        discoveryManager.scanForTables { tables ->
            _uiState.update { it.copy(discoveredTables = tables, isScanning = false) }
        }
    }

    fun stopScan() {
        discoveryManager.stopScan()
        _uiState.update { it.copy(isScanning = false) }
    }

    // --- WiFi Direct (P2P, no router) ---

    fun startP2pScan() {
        wifiDirectManager.clearError()
        _uiState.update { it.copy(p2pError = null) }

        val wifi = application.getSystemService(android.content.Context.WIFI_SERVICE) as? android.net.wifi.WifiManager
        if (wifi != null && !wifi.isWifiEnabled) {
            _uiState.update { it.copy(p2pError = application.getString(R.string.live_p2p_wifi_off)) }
            return
        }
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.TIRAMISU && !isLocationEnabled()) {
            _uiState.update { it.copy(p2pError = application.getString(R.string.live_p2p_location_off)) }
            return
        }

        p2pScanJob?.cancel()
        _uiState.update { it.copy(p2pPeers = emptyList(), isP2pScanning = true) }
        p2pScanJob = viewModelScope.launch {
            try {
                repeat(4) {
                    if (_uiState.value.p2pPeers.isNotEmpty()) return@launch
                    if (wifiDirectManager.state.value.error != null) return@launch
                    wifiDirectManager.discoverPeers { peers ->
                        _uiState.update { it.copy(p2pPeers = peers) }
                    }
                    delay(4_500)
                }
                Log.d(TAG, "P2P scan finished: ${_uiState.value.p2pPeers.size} peer(s)")
            } finally {
                _uiState.update { it.copy(isP2pScanning = false) }
            }
        }
    }

    private fun isLocationEnabled(): Boolean {
        val lm = application.getSystemService(android.content.Context.LOCATION_SERVICE)
            as? android.location.LocationManager ?: return true
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            lm.isLocationEnabled
        } else {
            lm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER) ||
                lm.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER)
        }
    }

    fun connectP2p(peer: WifiP2pDevice) {
        wifiDirectManager.clearError()
        _uiState.update { it.copy(isP2pConnecting = true, p2pError = null) }
        wifiDirectManager.connectToPeer(
            device = peer,
            onConnected = {
                // The TCP join happens when the group is formed (onGroupFormed)
            },
            onError = { error ->
                _uiState.update { it.copy(isP2pConnecting = false, p2pError = error) }
            }
        )
        viewModelScope.launch {
            delay(15_000)
            if (_uiState.value.isP2pConnecting && _uiState.value.p2pConnectHost == null) {
                _uiState.update {
                    it.copy(
                        isP2pConnecting = false,
                        p2pError = application.getString(R.string.live_p2p_timeout)
                    )
                }
            }
        }
    }

    fun clearP2pConnect() {
        _uiState.update { it.copy(p2pConnectHost = null) }
    }

    fun clearP2pError() {
        wifiDirectManager.clearError()
        _uiState.update { it.copy(p2pError = null) }
    }

    override fun onCleared() {
        super.onCleared()
        discoveryManager.destroy()
        wifiDirectManager.destroy()
    }
}

data class FindTableUiState(
    val isScanning: Boolean = false,
    val discoveredTables: List<DiscoveredTable> = emptyList(),
    val p2pPeers: List<WifiP2pDevice> = emptyList(),
    val isP2pScanning: Boolean = false,
    val isP2pConnecting: Boolean = false,
    val p2pConnectHost: String? = null,
    val p2pError: String? = null
)

class FindTableViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FindTableViewModel::class.java)) {
            return FindTableViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
