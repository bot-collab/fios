package com.fios.app.presentation.wallet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.fios.app.FiosApplication
import com.fios.app.data.local.entities.LogType
import com.fios.app.data.preferences.UserPreferencesRepository
import com.fios.app.domain.model.SystemLog
import com.fios.app.domain.model.VaultAsset
import com.fios.app.domain.repository.SystemLogRepository
import com.fios.app.domain.repository.VaultRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class WalletUiState(
    val username: String = "OPERADOR",
    val logs: List<SystemLog> = emptyList(),
    val assets: List<VaultAsset> = emptyList(),
    val isLoading: Boolean = false
)

class WalletViewModel(
    private val logRepository: SystemLogRepository,
    private val vaultRepository: VaultRepository,
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val uiState: StateFlow<WalletUiState> = combine(
        logRepository.getLogs(),
        vaultRepository.getAssets(),
        preferencesRepository.userPreferencesFlow
    ) { logs, assets, prefs ->
        WalletUiState(
            username = prefs.username,
            logs = logs,
            assets = assets,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = WalletUiState(isLoading = true)
    )

    fun addLog(
        name: String,
        amount: Double,
        type: LogType,
        category: String,
        metadata: String? = null
    ) {
        viewModelScope.launch {
            val newLog = SystemLog(
                timestamp = System.currentTimeMillis(),
                operationName = name,
                dataVolume = amount,
                category = category,
                type = type,
                metadata = metadata
            )
            logRepository.insertLog(newLog)
        }
    }

    fun transferToVault(asset: VaultAsset?, newName: String, amount: Double) {
        viewModelScope.launch {
            // 1. Create Outbound Log
            val logName = "TRANSFER: ${asset?.name ?: newName}"
            addLog(logName, amount, LogType.OUTBOUND, "VAULT", "Packet Transfer to Secure Storage")

            // 2. Update or Create Asset
            if (asset != null) {
                vaultRepository.updateAsset(asset.copy(
                    balance = asset.balance + amount,
                    lastSync = System.currentTimeMillis()
                ))
            } else {
                vaultRepository.insertAsset(VaultAsset(
                    name = newName,
                    balance = amount,
                    lastSync = System.currentTimeMillis(),
                    encryptionLevel = (64..256).random()
                ))
            }
        }
    }

    fun deleteLog(log: SystemLog) {
        viewModelScope.launch {
            logRepository.deleteLog(log)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as FiosApplication)
                WalletViewModel(
                    application.container.systemLogRepository,
                    application.container.vaultRepository,
                    application.container.userPreferencesRepository
                )
            }
        }
    }
}
