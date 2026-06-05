package com.fios.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Representa activos en "Almacenamiento Cifrado" (Ahorros congelados).
 */
@Entity(tableName = "vault_assets")
data class VaultEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val assetName: String,   // Nombre del activo (ej. "Fondo de Emergencia")
    val balance: Double,     // Monto guardado
    val lastSync: Long,      // Timestamp de última actualización
    val encryptionLevel: Int // Nivel de "seguridad" (estético, 1-128 bits)
)
