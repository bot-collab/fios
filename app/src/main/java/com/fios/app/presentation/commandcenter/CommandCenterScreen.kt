package com.fios.app.presentation.commandcenter

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fios.app.ui.components.FiosLogo
import java.util.Locale

@Composable
fun CommandCenterScreen(
    onSettingsClick: () -> Unit,
    viewModel: CommandCenterViewModel = viewModel(factory = CommandCenterViewModel.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()
    val primaryColor = MaterialTheme.colorScheme.primary

    val infiniteTransition = rememberInfiniteTransition(label = "overload")
    val warningAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        // TOP BAR
        CommandCenterTopBar(
            username = uiState.username,
            onSettingsClick = onSettingsClick,
            isIncognito = uiState.isIncognito,
            onIncognitoToggle = { viewModel.toggleIncognito() }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // BALANCE HEADER
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "BALANCE DISPONIBLE",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                letterSpacing = 2.sp
            )
            Text(
                text = formatCurrency(uiState.netStability, uiState.isIncognito),
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryColor
                ),
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // QUICK STATUS ROW
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickStatusCard(
                label = "Ingresos",
                value = formatCurrency(uiState.ramUsage, uiState.isIncognito),
                icon = Icons.Default.ArrowDownward,
                iconColor = primaryColor,
                modifier = Modifier.weight(1f)
            )
            QuickStatusCard(
                label = "Gastos",
                value = formatCurrency(uiState.cpuLoad, uiState.isIncognito),
                icon = Icons.Default.ArrowUpward,
                iconColor = MaterialTheme.colorScheme.error,
                modifier = Modifier.weight(1f)
            )
            QuickStatusCard(
                label = "Ahorros",
                value = formatCurrency(uiState.totalAssetsValue - uiState.netStability, uiState.isIncognito),
                icon = Icons.Default.Savings,
                iconColor = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // FINANCIAL RESOURCES SECTION
        FinancialResourcesSection(uiState)

        Spacer(modifier = Modifier.height(24.dp))

        // SMART INSIGHTS SECTION
        SmartInsightsSection(uiState, warningAlpha)

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun CommandCenterTopBar(
    username: String,
    onSettingsClick: () -> Unit,
    isIncognito: Boolean,
    onIncognitoToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ShowChart,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        
        // Dynamic Terminal Prompt RESTORED
        Text(
            text = "${username.uppercase()}@FIOS:~$",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onIncognitoToggle, modifier = Modifier.size(32.dp)) {
                Icon(
                    imageVector = if (isIncognito) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Ajustes",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(28.dp)
                    .clickable { onSettingsClick() }
            )
        }
    }
}

@Composable
fun QuickStatusCard(
    label: String,
    value: String,
    icon: ImageVector,
    iconColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(100.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            Text(text = value, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun FinancialResourcesSection(uiState: CommandCenterUiState) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Financial Resources",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(24.dp))

            val ramUsage = if (uiState.ramUsage > 0) (uiState.netStability / uiState.ramUsage).toFloat() else 0f
            ResourceProgressBar(label = "Disponible", badge = "RAM", progress = ramUsage)
            
            val cpuUsage = if (uiState.ramUsage > 0) (uiState.cpuLoad / uiState.ramUsage).toFloat() else 0f
            ResourceProgressBar(label = "Gastos del Día", badge = "CPU", progress = cpuUsage)
            
            // DSK (Ahorros) calculated as (Vault / Total Assets)
            val dskUsage = if (uiState.totalAssetsValue > 0) ((uiState.totalAssetsValue - uiState.netStability) / uiState.totalAssetsValue).toFloat() else 0f
            ResourceProgressBar(label = "Ahorros", badge = "DSK", progress = dskUsage)
            
            // NET (Flujo Mensual) calculated as Health Score
            ResourceProgressBar(label = "Flujo Mensual", badge = "NET", progress = uiState.healthScore / 100f)
        }
    }
}

@Composable
fun ResourceProgressBar(label: String, badge: String, progress: Float) {
    Column(modifier = Modifier.padding(vertical = 10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(2.dp))
                    .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
                Text(text = badge, fontSize = 8.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
            Text(text = "${(progress * 100).toInt()}%", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
        }
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { progress.coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(CircleShape),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
            strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
        )
    }
}

@Composable
fun SmartInsightsSection(uiState: CommandCenterUiState, warningAlpha: Float) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.AutoMirrored.Filled.ShowChart, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Smart Insights", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            val healthStatus = when {
                uiState.healthScore >= 90 -> "EXCELLENTE"
                uiState.healthScore >= 75 -> "BUENO"
                uiState.healthScore >= 50 -> "ADVERTENCIA"
                else -> "RIESGO CRÍTICO"
            }
            
            InsightItem(
                icon = Icons.Default.Shield,
                text = "Salud del Sistema: $healthStatus (${uiState.healthScore}%)"
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            uiState.autonomyDays?.let { days ->
                InsightItem(
                    icon = Icons.Default.Timer,
                    text = "Autonomía estimada: $days días de operación."
                )
            } ?: InsightItem(
                icon = Icons.Default.Info,
                text = "Recolectando telemetría para predicción..."
            )

            if (uiState.isCpuOverloaded) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error.copy(alpha = warningAlpha))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "ANOMALÍA: SOBRECARGA DETECTADA",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
fun InsightItem(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f), modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = text, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
    }
}

fun formatCurrency(amount: Double, isIncognito: Boolean = false): String {
    return if (isIncognito) {
        "S/ *****"
    } else {
        String.format(Locale.getDefault(), "S/ %,.2f", amount)
    }
}
