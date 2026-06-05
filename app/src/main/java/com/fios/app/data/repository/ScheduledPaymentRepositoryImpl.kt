package com.fios.app.data.repository

import com.fios.app.core.notifications.FiosNotificationHelper
import com.fios.app.data.local.dao.ScheduledPaymentDao
import com.fios.app.data.local.dao.SystemLogDao
import com.fios.app.data.local.entities.ScheduledPaymentEntity
import com.fios.app.data.local.entities.SystemLogEntity
import com.fios.app.domain.model.ScheduledPayment
import com.fios.app.domain.repository.ScheduledPaymentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Calendar

class ScheduledPaymentRepositoryImpl(
    private val paymentDao: ScheduledPaymentDao,
    private val logDao: SystemLogDao,
    private val notificationHelper: FiosNotificationHelper
) : ScheduledPaymentRepository {

    override fun getPayments(): Flow<List<ScheduledPayment>> {
        return paymentDao.getAllPayments().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insertPayment(payment: ScheduledPayment) {
        paymentDao.insertPayment(payment.toEntity())
    }

    override suspend fun updatePayment(payment: ScheduledPayment) {
        paymentDao.updatePayment(payment.toEntity())
    }

    override suspend fun deletePayment(payment: ScheduledPayment) {
        paymentDao.deletePayment(payment.toEntity())
    }

    override suspend fun checkAndExecutePayments() {
        val now = Calendar.getInstance()
        val currentDay = now.get(Calendar.DAY_OF_MONTH)
        val currentMonth = now.get(Calendar.MONTH) + 1
        val currentYear = now.get(Calendar.YEAR)

        val duePayments = paymentDao.getPaymentsDue(currentDay)
        
        duePayments.forEach { payment ->
            if (payment.lastExecutedMonth != currentMonth || payment.lastExecutedYear != currentYear) {
                // Execute Automation: Create a System Log
                val log = SystemLogEntity(
                    timestamp = System.currentTimeMillis(),
                    operationName = "[CRON] ${payment.name}",
                    dataVolume = payment.amount,
                    category = payment.category,
                    type = payment.type,
                    metadata = "Automated Monthly Execution"
                )
                logDao.insertLog(log)

                // Notify User
                notificationHelper.sendSystemNotification(
                    title = "CRON JOB EJECUTADO",
                    message = "Se ha inyectado el pago programado: ${payment.name}"
                )

                // Update payment record to prevent duplicate in same month
                paymentDao.updatePayment(payment.copy(
                    lastExecutedMonth = currentMonth,
                    lastExecutedYear = currentYear
                ))
            }
        }
    }
}

fun ScheduledPaymentEntity.toDomain() = ScheduledPayment(
    id = id,
    name = name,
    amount = amount,
    dayOfMonth = dayOfMonth,
    category = category,
    type = type,
    lastExecutedMonth = lastExecutedMonth,
    lastExecutedYear = lastExecutedYear
)

fun ScheduledPayment.toEntity() = ScheduledPaymentEntity(
    id = id,
    name = name,
    amount = amount,
    dayOfMonth = dayOfMonth,
    category = category,
    type = type,
    lastExecutedMonth = lastExecutedMonth,
    lastExecutedYear = lastExecutedYear
)
