package com.fios.app.data.local.dao

import androidx.room.*
import com.fios.app.data.local.entities.ScheduledPaymentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ScheduledPaymentDao {
    @Query("SELECT * FROM scheduled_payments")
    fun getAllPayments(): Flow<List<ScheduledPaymentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: ScheduledPaymentEntity)

    @Update
    suspend fun updatePayment(payment: ScheduledPaymentEntity)

    @Delete
    suspend fun deletePayment(payment: ScheduledPaymentEntity)
    
    @Query("SELECT * FROM scheduled_payments WHERE dayOfMonth <= :currentDay")
    suspend fun getPaymentsDue(currentDay: Int): List<ScheduledPaymentEntity>
}
