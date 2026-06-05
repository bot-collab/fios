package com.fios.app.presentation.wallet

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fios.app.data.local.entities.LogType
import com.fios.app.domain.model.SystemLog
import com.fios.app.presentation.commandcenter.formatCurrency
import com.fios.app.ui.components.AddLogDialog
import com.fios.app.ui.components.ConfirmDeleteDialog
import com.fios.app.ui.components.FinancialCard
import com.fios.app.ui.components.TransferToVaultDialog
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun WalletScreen(
    viewModel: WalletViewModel = viewModel(factory = WalletViewModel.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var showTransferDialog by remember { mutableStateOf(false) }
    var logToDelete by remember { mutableStateOf<SystemLog?>(null) }

    Scaffold(
        floatingActionButton = {
            Column(horizontalAlignment = Alignment.End) {
                SmallFloatingActionButton(
                    onClick = { showTransferDialog = true },
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Icon(Icons.Default.LockReset, contentDescription = "Transferencia a Bóveda")
                }
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Añadir Log")
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(
                text = "${uiState.username.uppercase()}@FIOS:~$ ls -historial",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )

            val totalNet = uiState.logs.sumOf { 
                if (it.type == LogType.INBOUND) it.dataVolume else -it.dataVolume 
            }

            FinancialCard(
                title = "Liquidez Total del Sistema",
                amount = formatCurrency(totalNet)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "LOGS DE TELEMETRÍA",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.logs.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "NO SE ENCONTRARON LOGS ACTIVOS",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(uiState.logs) { log ->
                        TransactionItem(
                            log = log,
                            onDelete = { logToDelete = log }
                        )
                    }
                }
            }
        }

        if (showAddDialog) {
            AddLogDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { name, amount, type, category ->
                    viewModel.addLog(name, amount, type, category)
                    showAddDialog = false
                }
            )
        }

        if (showTransferDialog) {
            TransferToVaultDialog(
                assets = uiState.assets,
                onDismiss = { showTransferDialog = false },
                onConfirm = { asset, newName, amount ->
                    viewModel.transferToVault(asset, newName, amount)
                    showTransferDialog = false
                }
            )
        }

        logToDelete?.let { log ->
            ConfirmDeleteDialog(
                itemName = log.operationName,
                onDismiss = { logToDelete = null },
                onConfirm = {
                    viewModel.deleteLog(log)
                    logToDelete = null
                }
            )
        }
    }
}

@Composable
fun TransactionItem(
    log: SystemLog,
    onDelete: () -> Unit
) {
    val dateFormatter = remember { SimpleDateFormat("dd/MM HH:mm", Locale.getDefault()) }
    val dateString = dateFormatter.format(Date(log.timestamp))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = log.operationName.uppercase(),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "TS: $dateString | TIPO: ${log.type.name} | SEC: ${log.category}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Text(
                text = if (log.type == LogType.INBOUND) "+ ${formatCurrency(log.dataVolume)}" else "- ${formatCurrency(log.dataVolume)}",
                style = MaterialTheme.typography.bodyMedium,
                color = if (log.type == LogType.INBOUND) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete, 
                    contentDescription = "Eliminar Log",
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f)
                )
            }
        }
    }
}
