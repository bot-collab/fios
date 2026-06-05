package com.fios.app.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.fios.app.FiosApplication
import com.fios.app.data.local.entities.LogType
import com.fios.app.data.preferences.UserPreferences
import com.fios.app.data.preferences.UserPreferencesRepository
import com.fios.app.domain.model.ScheduledPayment
import com.fios.app.domain.repository.MilestoneRepository
import com.fios.app.domain.repository.ScheduledPaymentRepository
import com.fios.app.domain.repository.SystemLogRepository
import com.fios.app.domain.repository.VaultRepository
import com.fios.app.ui.theme.FIOSThemeType
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SettingsUiState(
    val preferences: UserPreferences? = null,
    val scheduledPayments: List<ScheduledPayment> = emptyList()
)

class SettingsViewModel(
    private val preferencesRepository: UserPreferencesRepository,
    private val scheduledRepository: ScheduledPaymentRepository,
    private val logRepository: SystemLogRepository,
    private val milestoneRepository: MilestoneRepository,
    private val vaultRepository: VaultRepository
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = combine(
        preferencesRepository.userPreferencesFlow,
        scheduledRepository.getPayments()
    ) { prefs, payments ->
        SettingsUiState(preferences = prefs, scheduledPayments = payments)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsUiState()
    )

    fun updateTheme(theme: FIOSThemeType) {
        viewModelScope.launch {
            preferencesRepository.updateTheme(theme)
        }
    }

    fun updateUsername(username: String) {
        viewModelScope.launch {
            preferencesRepository.updateUsername(username)
        }
    }

    fun addScheduledPayment(name: String, amount: Double, day: Int, type: LogType, category: String) {
        viewModelScope.launch {
            scheduledRepository.insertPayment(
                ScheduledPayment(
                    name = name,
                    amount = amount,
                    dayOfMonth = day,
                    type = type,
                    category = category,
                    lastExecutedMonth = 0,
                    lastExecutedYear = 0
                )
            )
        }
    }

    fun deleteScheduledPayment(payment: ScheduledPayment) {
        viewModelScope.launch {
            scheduledRepository.deletePayment(payment)
        }
    }

    suspend fun exportDataToJson(): String {
        val logs = logRepository.getLogs().first()
        val milestones = milestoneRepository.getMilestones().first()
        val assets = vaultRepository.getAssets().first()
        
        // Manual JSON generation to avoid dependencies for now
        val sb = StringBuilder()
        sb.append("{\n")
        sb.append("  \"system_logs\": [\n")
        logs.forEachIndexed { i, log ->
            sb.append("    {\"op\": \"${log.operationName}\", \"vol\": ${log.dataVolume}, \"type\": \"${log.type}\"}${if (i < logs.size - 1) "," else ""}\n")
        }
        sb.append("  ],\n")
        sb.append("  \"milestones\": [\n")
        milestones.forEachIndexed { i, m ->
            sb.append("    {\"name\": \"${m.projectName}\", \"target\": ${m.targetCapacity}}${if (i < milestones.size - 1) "," else ""}\n")
        }
        sb.append("  ]\n")
        sb.append("}")
        return sb.toString()
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as FiosApplication)
                SettingsViewModel(
                    application.container.userPreferencesRepository,
                    application.container.scheduledPaymentRepository,
                    application.container.systemLogRepository,
                    application.container.milestoneRepository,
                    application.container.vaultRepository
                )
            }
        }
    }
}
