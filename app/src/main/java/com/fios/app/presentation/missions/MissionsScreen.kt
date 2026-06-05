package com.fios.app.presentation.missions

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fios.app.domain.model.Milestone
import com.fios.app.presentation.commandcenter.formatCurrency
import com.fios.app.ui.components.AddMilestoneDialog
import com.fios.app.ui.components.ConfirmDeleteDialog
import com.fios.app.ui.components.MissionCard

@Composable
fun MissionsScreen(
    viewModel: MissionsViewModel = viewModel(factory = MissionsViewModel.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var milestoneToDelete by remember { mutableStateOf<Milestone?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nuevo Proyecto")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "MILESTONES DE PROYECTO",
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    text = "Gestiona tus objetivos financieros como proyectos de infraestructura.",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            if (uiState.milestones.isEmpty()) {
                item {
                    Text(
                        text = "> NO HAY PROYECTOS ACTIVOS",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                items(uiState.milestones) { milestone ->
                    Box(modifier = Modifier.fillMaxWidth()) {
                        MissionCard(
                            title = milestone.projectName.uppercase(),
                            progress = (milestone.currentCapacity / milestone.targetCapacity).toFloat().coerceIn(0f, 1f),
                            progressText = "${formatCurrency(milestone.currentCapacity, uiState.isIncognito)} / ${formatCurrency(milestone.targetCapacity, uiState.isIncognito)}",
                            modifier = Modifier.fillMaxWidth()
                        )
                        IconButton(
                            onClick = { milestoneToDelete = milestone },
                            modifier = Modifier.align(Alignment.TopEnd).padding(4.dp)
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Borrar",
                                tint = MaterialTheme.colorScheme.error.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "LOGROS DESBLOQUEADOS (ROOT)",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            if (uiState.achievements.isEmpty()) {
                item {
                    Text(text = "> SIN MEDALLAS DETECTADAS", style = MaterialTheme.typography.bodySmall)
                }
            } else {
                items(uiState.achievements) { ach ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = if (ach.unlocked) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface.copy(alpha = 0.2f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.EmojiEvents, 
                                contentDescription = null,
                                tint = if (ach.unlocked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = ach.title,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = if (ach.unlocked) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                )
                                Text(
                                    text = ach.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        if (showAddDialog) {
            AddMilestoneDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { name, target, priority, sector ->
                    viewModel.addMilestone(name, target, priority, sector)
                    showAddDialog = false
                }
            )
        }

        milestoneToDelete?.let { milestone ->
            ConfirmDeleteDialog(
                itemName = milestone.projectName,
                onDismiss = { milestoneToDelete = null },
                onConfirm = {
                    viewModel.deleteMilestone(milestone)
                    milestoneToDelete = null
                }
            )
        }
    }
}
