package com.fios.app.presentation.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.fios.app.FiosApplication
import com.fios.app.data.local.entities.LogType
import com.fios.app.domain.repository.SystemLogRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class CategoryAnalysis(
    val name: String,
    val amount: Double,
    val percentage: Float
)

data class AnalyticsUiState(
    val categoryBreakdown: List<CategoryAnalysis> = emptyList(),
    val totalExpenses: Double = 0.0,
    val totalIncome: Double = 0.0,
    val isLoading: Boolean = false
)

class AnalyticsViewModel(
    private val repository: SystemLogRepository
) : ViewModel() {

    val uiState: StateFlow<AnalyticsUiState> = repository.getLogs().map { logs ->
        val expenses = logs.filter { it.type == LogType.OUTBOUND }
        val income = logs.filter { it.type == LogType.INBOUND }
        
        val totalExp = expenses.sumOf { it.dataVolume }
        val totalInc = income.sumOf { it.dataVolume }
        
        val breakdown = expenses.groupBy { it.category }
            .map { (category, categoryLogs) ->
                val categorySum = categoryLogs.sumOf { it.dataVolume }
                CategoryAnalysis(
                    name = category,
                    amount = categorySum,
                    percentage = if (totalExp > 0) (categorySum / totalExp).toFloat() else 0f
                )
            }.sortedByDescending { it.amount }

        AnalyticsUiState(
            categoryBreakdown = breakdown,
            totalExpenses = totalExp,
            totalIncome = totalInc
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AnalyticsUiState(isLoading = true)
    )

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as FiosApplication)
                AnalyticsViewModel(application.container.systemLogRepository)
            }
        }
    }
}
