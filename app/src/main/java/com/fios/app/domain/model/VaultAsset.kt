package com.fios.app.domain.model

data class VaultAsset(
    val id: Long = 0,
    val name: String,
    val balance: Double,
    val lastSync: Long,
    val encryptionLevel: Int
)
