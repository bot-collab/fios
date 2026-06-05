package com.fios.app.presentation.commandcenter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.fios.app.FiosApplication
import com.fios.app.data.local.entities.LogType
import com.fios.app.data.preferences.UserPreferencesRepository
import com.fios.app.domain.model.SystemLog
import com.fios.app.domain.repository.SystemLogRepository
import com.fios.app.domain.repository.VaultRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class CommandCenterUiState(
    val username: String = "OPERADOR",
    val recentLogs: List<SystemLog> = emptyList(),
    val ramUsage: Double = 0.0, // Inbound total
    val cpuLoad: Double = 0.0,   // Outbound total
    val netStability: Double = 0.0, // Balance in wallet
    val totalAssetsValue: Double = 0.0, // Wallet + Vault
    val isCpuOverloaded: Boolean = false,
    val healthScore: Int = 0,
    val autonomyDays: Int? = null,
    val isIncognito: Boolean = false
)

class CommandCenterViewModel(
    private val logRepository: SystemLogRepository,
    private val vaultRepository: VaultRepository,
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val uiState: StateFlow<CommandCenterUiState> = combine(
        logRepository.getLogs(),
        vaultRepository.getAssets(),
        preferencesRepository.userPreferencesFlow
    ) { logs, assets, prefs ->
        val inbound = logs.filter { it.type == LogType.INBOUND }.sumOf { it.dataVolume }
        val outbound = logs.filter { it.type == LogType.OUTBOUND }.sumOf { it.dataVolume }
        val vaultBalance = assets.sumOf { it.balance }
        val balance = inbound - outbound
        
        val cpuUsageRatio = if (inbound > 0) outbound / inbound else 0.0
        val score = if (inbound > 0) {
            ((1 - cpuUsageRatio) * 100).coerceIn(0.0, 100.0).toInt()
        } else 0

        // Prediction Logic: Autonomy Days
        val sevenDaysAgo = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000)
        val recentExpenses = logs.filter { it.type == LogType.OUTBOUND && it.timestamp >= sevenDaysAgo }
        val avgDailySpend = recentExpenses.sumOf { it.dataVolume } / 7.0
        
        val daysRemaining = if (avgDailySpend > 0) {
            (balance / avgDailySpend).toInt().coerceAtLeast(0)
        } else null
        
        CommandCenterUiState(
            username = prefs.username,
            recentLogs = logs.take(10),
            ramUsage = inbound,
            cpuLoad = outbound,
            netStability = balance,
            totalAssetsValue = balance + vaultBalance,
            isCpuOverloaded = cpuUsageRatio > prefs.cpuOverloadThreshold,
            healthScore = score,
            autonomyDays = daysRemaining,
            isIncognito = prefs.isIncognitoEnabled
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CommandCenterUiState()
    )

    fun toggleIncognito() {
        viewModelScope.launch {
            preferencesRepository.updateIncognito(!uiState.value.isIncognito)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as FiosApplication)
                CommandCenterViewModel(
                    application.container.systemLogRepository,
                    application.container.vaultRepository,
                    application.container.userPreferencesRepository
                )
            }
        }
    }
}
