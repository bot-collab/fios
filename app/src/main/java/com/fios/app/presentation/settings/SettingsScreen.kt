package com.fios.app.presentation.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fios.app.presentation.commandcenter.formatCurrency
import com.fios.app.ui.components.AddScheduledPaymentDialog
import com.fios.app.ui.components.ConfirmDeleteDialog
import com.fios.app.ui.theme.FIOSThemeType
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = viewModel(factory = SettingsViewModel.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current
    var showAddCronDialog by remember { mutableStateOf(false) }
    var cronToDelete by remember { mutableStateOf<com.fios.app.domain.model.ScheduledPayment?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        Text(
            text = "CONFIGURACIÓN DEL NÚCLEO",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "Ajustes de interfaz y parámetros del sistema.",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Personalización de Usuario
        Text(text = "IDENTIDAD DEL OPERADOR", style = MaterialTheme.typography.titleLarge)
        
        // BUG FIX: El estado local del TextField debe ser independiente del ViewModel 
        // para evitar el salto de cursor y caracteres al escribir/borrar.
        var usernameInput by remember { mutableStateOf("") }
        
        // Sincronizar solo cuando el valor inicial cargue
        LaunchedEffect(uiState.preferences?.username) {
            if (usernameInput.isEmpty() && uiState.preferences?.username != null) {
                usernameInput = uiState.preferences!!.username
            }
        }
        
        OutlinedTextField(
            value = usernameInput,
            onValueChange = { 
                if (it.length <= 15) { // Limitar a 15 caracteres
                    usernameInput = it
                    viewModel.updateUsername(it)
                }
            },
            label = { Text("Nombre de Usuario (PROMPT)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
            supportingText = {
                Text(text = "${usernameInput.length}/15", style = MaterialTheme.typography.labelSmall)
            }
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Tareas Programadas (Cron Jobs)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "CRON JOBS (AUTOMATIZACIÓN)", style = MaterialTheme.typography.titleLarge)
            IconButton(onClick = { showAddCronDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Añadir Tarea")
            }
        }
        
        if (uiState.scheduledPayments.isEmpty()) {
            Text(
                text = "> NO HAY TAREAS PROGRAMADAS ACTIVAS",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            uiState.scheduledPayments.forEach { payment ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = payment.name.uppercase(), style = MaterialTheme.typography.labelLarge)
                        Text(
                            text = "DÍA: ${payment.dayOfMonth} | MONTO: ${formatCurrency(payment.amount)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = { cronToDelete = payment }) {
                        Icon(Icons.Default.Delete, contentDescription = "Borrar", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Selector de Temas
        Text(text = "KERNEL VISUAL (TEMAS)", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))

        FIOSThemeType.entries.forEach { theme ->
            ThemeOption(
                theme = theme,
                isSelected = uiState.preferences?.theme == theme,
                onClick = { viewModel.updateTheme(theme) }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Sección de Datos
        Text(text = "PROTOCOLO DE DATOS", style = MaterialTheme.typography.titleLarge)
        
        val scope = rememberCoroutineScope()

        Button(
            onClick = { 
                scope.launch {
                    val json = viewModel.exportDataToJson()
                    android.util.Log.d("FIOS_DUMP", json)
                }
            },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant, 
                contentColor = MaterialTheme.colorScheme.secondary
            )
        ) {
            Text("EXPORTAR DUMP A LOGCAT (DEBUG)")
        }

        Spacer(modifier = Modifier.height(48.dp))
    }

    if (showAddCronDialog) {
        AddScheduledPaymentDialog(
            onDismiss = { showAddCronDialog = false },
            onConfirm = { name, amount, day, type, category ->
                viewModel.addScheduledPayment(name, amount, day, type, category)
                showAddCronDialog = false
            }
        )
    }

    cronToDelete?.let { payment ->
        ConfirmDeleteDialog(
            itemName = "CRON JOB: ${payment.name}",
            onDismiss = { cronToDelete = null },
            onConfirm = {
                viewModel.deleteScheduledPayment(payment)
                cronToDelete = null
            }
        )
    }
}

@Composable
fun ThemeOption(
    theme: FIOSThemeType,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val color = when(theme) {
        FIOSThemeType.HACKER_GREEN -> Color(0xFF00FF88)
        FIOSThemeType.MATRIX -> Color(0xFF00FF41)
        FIOSThemeType.NORD -> Color(0xFF88C0D0)
        FIOSThemeType.DRACULA -> Color(0xFFFF79C6)
        FIOSThemeType.KALI_PURPLE -> Color(0xFFA020F0)
        FIOSThemeType.CYBER_BLUE -> Color(0xFF00D1FF)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = isSelected, onClick = onClick)
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(color, MaterialTheme.shapes.small)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = theme.name.replace("_", " "),
            style = MaterialTheme.typography.bodyLarge,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
    }
}
