package com.fios.app.presentation.analytics

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fios.app.presentation.commandcenter.formatCurrency
import com.fios.app.ui.components.StatusCard

@Composable
fun AnalyticsScreen(
    viewModel: AnalyticsViewModel = viewModel(factory = AnalyticsViewModel.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "TELEMETRÍA DE RENDIMIENTO",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "Análisis ejecutivo de salud financiera y trayectorias.",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Row {
            val healthScore = if (uiState.totalIncome > 0) {
                ((1 - (uiState.totalExpenses / uiState.totalIncome)) * 100).coerceIn(0.0, 100.0).toInt()
            } else 0
            
            StatusCard(
                label = "Índice de Salud",
                value = "$healthScore%",
                icon = Icons.Default.Shield,
                modifier = Modifier.weight(1f)
            )
            
            val ratio = if (uiState.totalIncome > 0) {
                (uiState.totalExpenses / uiState.totalIncome * 100).toInt()
            } else 0
            
            StatusCard(
                label = "Carga de Sistema",
                value = "$ratio%",
                icon = Icons.AutoMirrored.Filled.TrendingUp,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "CARGA POR SUBSISTEMA (GASTOS)",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.categoryBreakdown.isEmpty()) {
            Text(
                text = "> NO SE HAN DETECTADO CARGAS ACTIVAS",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            uiState.categoryBreakdown.forEach { analysis ->
                CategoryLoadItem(analysis)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun CategoryLoadItem(analysis: CategoryAnalysis) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = analysis.name,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = formatCurrency(analysis.amount),
                style = MaterialTheme.typography.labelMedium
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        LinearProgressIndicator(
            progress = { analysis.percentage },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
            strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
        )
        
        Text(
            text = "USO: ${(analysis.percentage * 100).toInt()}% DEL TOTAL DE CARGA",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
