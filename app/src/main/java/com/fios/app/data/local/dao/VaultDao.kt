package com.fios.app.data.local.dao

import androidx.room.*
import com.fios.app.data.local.entities.VaultEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VaultDao {
    @Query("SELECT * FROM vault_assets ORDER BY assetName ASC")
    fun getAllAssets(): Flow<List<VaultEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAsset(asset: VaultEntity)

    @Update
    suspend fun updateAsset(asset: VaultEntity)

    @Delete
    suspend fun deleteAsset(asset: VaultEntity)
}
