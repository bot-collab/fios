package com.fios.app.domain.repository

import com.fios.app.domain.model.ScheduledPayment
import kotlinx.coroutines.flow.Flow

interface ScheduledPaymentRepository {
    fun getPayments(): Flow<List<ScheduledPayment>>
    suspend fun insertPayment(payment: ScheduledPayment)
    suspend fun updatePayment(payment: ScheduledPayment)
    suspend fun deletePayment(payment: ScheduledPayment)
    suspend fun checkAndExecutePayments()
}
