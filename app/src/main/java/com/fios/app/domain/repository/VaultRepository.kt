package com.fios.app.domain.repository

import com.fios.app.domain.model.VaultAsset
import kotlinx.coroutines.flow.Flow

interface VaultRepository {
    fun getAssets(): Flow<List<VaultAsset>>
    suspend fun insertAsset(asset: VaultAsset)
    suspend fun updateAsset(asset: VaultAsset)
    suspend fun deleteAsset(asset: VaultAsset)
}
