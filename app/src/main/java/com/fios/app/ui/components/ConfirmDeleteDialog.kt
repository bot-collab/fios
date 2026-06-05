package com.fios.app.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable

@Composable
fun ConfirmDeleteDialog(
    itemName: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Text(
                "ADVERTENCIA DE SEGURIDAD", 
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.error
            ) 
        },
        text = { 
            Text(
                "¿Confirmar desasignación de datos de '$itemName'? Esta operación no se puede deshacer.",
                style = MaterialTheme.typography.bodyMedium
            ) 
        },
        confirmButton = {
            Button(
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                onClick = onConfirm
            ) {
                Text("CONFIRMAR")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("ABORTAR")
            }
        }
    )
}
