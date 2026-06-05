package com.fios.app.data.local.dao

import androidx.room.*
import com.fios.app.data.local.entities.SystemLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SystemLogDao {
    @Query("SELECT * FROM system_logs ORDER BY timestamp DESC")
    fun getAllLogs(): Flow<List<SystemLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: SystemLogEntity)

    @Delete
    suspend fun deleteLog(log: SystemLogEntity)

    @Query("SELECT SUM(dataVolume) FROM system_logs WHERE type = 'INBOUND'")
    fun getTotalInbound(): Flow<Double?>

    @Query("SELECT SUM(dataVolume) FROM system_logs WHERE type = 'OUTBOUND'")
    fun getTotalOutbound(): Flow<Double?>
}
