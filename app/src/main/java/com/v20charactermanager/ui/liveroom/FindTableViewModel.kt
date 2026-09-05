package com.v20charactermanager.ui.liveroom

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.v20charactermanager.data.network.TableDiscoveryManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FindTableViewModel(
    private val application: Application
) : ViewModel() {

    private val discoveryManager = TableDiscoveryManager(application)

    private val _uiState = MutableStateFlow(FindTableUiState())
    val uiState: StateFlow<FindTableUiState> = _uiState.asStateFlow()

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

    override fun onCleared() {
        super.onCleared()
        discoveryManager.destroy()
    }
}

data class FindTableUiState(
    val isScanning: Boolean = false,
    val discoveredTables: List<DiscoveredTable> = emptyList()
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
