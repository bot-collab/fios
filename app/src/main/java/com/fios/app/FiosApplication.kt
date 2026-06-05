package com.fios.app

import android.app.Application
import com.fios.app.data.local.entities.LogType
import com.fios.app.domain.model.Achievement
import com.fios.app.domain.model.SystemLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class FiosApplication : Application() {
    lateinit var container: AppContainer
    private val applicationScope = CoroutineScope(SupervisorJob())

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        
        // Seed data if empty
        seedDatabase()
        
        // Execute Cron Jobs (Scheduled Payments)
        applicationScope.launch {
            container.scheduledPaymentRepository.checkAndExecutePayments()
        }
    }

    private fun seedDatabase() {
        applicationScope.launch {
            val logs = container.systemLogRepository.getLogs().first()
            if (logs.isEmpty()) {
                val seedLogs = listOf(
                    SystemLog(
                        timestamp = System.currentTimeMillis() - 86400000,
                        operationName = "Core Salary Upload",
                        dataVolume = 3500.0,
                        category = "CORE",
                        type = LogType.INBOUND
                    ),
                    SystemLog(
                        timestamp = System.currentTimeMillis() - 43200000,
                        operationName = "Hardware Maintenance",
                        dataVolume = 1200.0,
                        category = "SUBSYSTEM",
                        type = LogType.OUTBOUND
                    ),
                    SystemLog(
                        timestamp = System.currentTimeMillis() - 3600000,
                        operationName = "Cloud Subscription",
                        dataVolume = 45.0,
                        category = "PERIPHERALS",
                        type = LogType.OUTBOUND
                    )
                )
                seedLogs.forEach { container.systemLogRepository.insertLog(it) }
            }

            val achievements = container.achievementRepository.getAchievements().first()
            if (achievements.isEmpty()) {
                val seedAchievements = listOf(
                    Achievement(title = "PRIMER CONTACTO", description = "Registra tu primera operación en el sistema.", iconCode = "CLI"),
                    Achievement(title = "AHORRADOR ROOT", description = "Completa tu primer proyecto de infraestructura.", iconCode = "ROOT"),
                    Achievement(title = "CENTINELA", description = "Mantén el sistema en estado EXCELLENT por 7 días.", iconCode = "SHIELD")
                )
                seedAchievements.forEach { container.achievementRepository.insertAchievement(it) }
            }
        }
    }
}
