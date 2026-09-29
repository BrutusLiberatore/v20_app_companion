package com.v20charactermanager.ui.liveroom

import android.app.Application
import android.net.wifi.p2p.WifiP2pDevice
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

class FindTableViewModel(
    private val application: Application
) : ViewModel() {

    private val discoveryManager = TableDiscoveryManager(application)
    private val wifiDirectManager = WifiDirectManager(application)

    private val _uiState = MutableStateFlow(FindTableUiState())
    val uiState: StateFlow<FindTableUiState> = _uiState.asStateFlow()

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
                        isP2pScanning = p2p.isDiscovering
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
        wifiDirectManager.discoverPeers { peers ->
            _uiState.update { it.copy(p2pPeers = peers) }
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
