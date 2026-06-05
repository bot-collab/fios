package com.fios.app.data.repository

import com.fios.app.data.local.dao.VaultDao
import com.fios.app.data.local.entities.VaultEntity
import com.fios.app.domain.model.VaultAsset
import com.fios.app.domain.repository.VaultRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class VaultRepositoryImpl(
    private val dao: VaultDao
) : VaultRepository {

    override fun getAssets(): Flow<List<VaultAsset>> {
        return dao.getAllAssets().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insertAsset(asset: VaultAsset) {
        dao.insertAsset(asset.toEntity())
    }

    override suspend fun updateAsset(asset: VaultAsset) {
        dao.updateAsset(asset.toEntity())
    }

    override suspend fun deleteAsset(asset: VaultAsset) {
        dao.deleteAsset(asset.toEntity())
    }
}

fun VaultEntity.toDomain() = VaultAsset(
    id = id,
    name = assetName,
    balance = balance,
    lastSync = lastSync,
    encryptionLevel = encryptionLevel
)

fun VaultAsset.toEntity() = VaultEntity(
    id = id,
    assetName = name,
    balance = balance,
    lastSync = lastSync,
    encryptionLevel = encryptionLevel
)
