package com.fios.app

import android.content.Context
import androidx.room.Room
import com.fios.app.core.notifications.FiosNotificationHelper
import com.fios.app.data.local.FiosDatabase
import com.fios.app.data.preferences.UserPreferencesRepository
import com.fios.app.data.repository.AchievementRepositoryImpl
import com.fios.app.data.repository.MilestoneRepositoryImpl
import com.fios.app.data.repository.ScheduledPaymentRepositoryImpl
import com.fios.app.data.repository.SystemLogRepositoryImpl
import com.fios.app.data.repository.VaultRepositoryImpl
import com.fios.app.domain.repository.AchievementRepository
import com.fios.app.domain.repository.MilestoneRepository
import com.fios.app.domain.repository.ScheduledPaymentRepository
import com.fios.app.domain.repository.SystemLogRepository
import com.fios.app.domain.repository.VaultRepository

/**
 * Manual Dependency Injection container because Hilt is currently disabled due to AGP incompatibility.
 */
class AppContainer(private val context: Context) {

    val notificationHelper: FiosNotificationHelper by lazy {
        FiosNotificationHelper(context)
    }

    private val database: FiosDatabase by lazy {
        Room.databaseBuilder(
            context,
            FiosDatabase::class.java,
            FiosDatabase.DATABASE_NAME
        )
        .fallbackToDestructiveMigration()
        .build()
    }

    val userPreferencesRepository: UserPreferencesRepository by lazy {
        UserPreferencesRepository(context)
    }

    val systemLogRepository: SystemLogRepository by lazy {
        SystemLogRepositoryImpl(database.systemLogDao())
    }

    val milestoneRepository: MilestoneRepository by lazy {
        MilestoneRepositoryImpl(database.milestoneDao())
    }

    val vaultRepository: VaultRepository by lazy {
        VaultRepositoryImpl(database.vaultDao())
    }

    val scheduledPaymentRepository: ScheduledPaymentRepository by lazy {
        ScheduledPaymentRepositoryImpl(
            database.scheduledPaymentDao(), 
            database.systemLogDao(),
            notificationHelper
        )
    }

    val achievementRepository: AchievementRepository by lazy {
        AchievementRepositoryImpl(database.achievementDao())
    }
}
