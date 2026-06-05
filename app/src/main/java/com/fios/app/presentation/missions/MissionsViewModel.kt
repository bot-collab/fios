package com.fios.app.presentation.missions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.fios.app.FiosApplication
import com.fios.app.data.preferences.UserPreferencesRepository
import com.fios.app.domain.model.Achievement
import com.fios.app.domain.model.Milestone
import com.fios.app.domain.repository.AchievementRepository
import com.fios.app.domain.repository.MilestoneRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class MissionsUiState(
    val milestones: List<Milestone> = emptyList(),
    val achievements: List<Achievement> = emptyList(),
    val isLoading: Boolean = false,
    val isIncognito: Boolean = false
)

class MissionsViewModel(
    private val milestoneRepository: MilestoneRepository,
    private val achievementRepository: AchievementRepository,
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val uiState: StateFlow<MissionsUiState> = combine(
        milestoneRepository.getMilestones(),
        achievementRepository.getAchievements(),
        preferencesRepository.userPreferencesFlow
    ) { milestones, achievements, prefs ->
        MissionsUiState(
            milestones = milestones,
            achievements = achievements,
            isLoading = false,
            isIncognito = prefs.isIncognitoEnabled
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MissionsUiState(isLoading = true)
    )

    fun addMilestone(
        name: String,
        target: Double,
        priority: Int,
        sector: String
    ) {
        viewModelScope.launch {
            val newMilestone = Milestone(
                projectName = name,
                targetCapacity = target,
                currentCapacity = 0.0,
                priority = priority,
                sector = sector
            )
            milestoneRepository.insertMilestone(newMilestone)
        }
    }

    fun updateProgress(milestone: Milestone, amount: Double) {
        viewModelScope.launch {
            val updated = milestone.copy(
                currentCapacity = (milestone.currentCapacity + amount).coerceAtMost(milestone.targetCapacity),
                isCompleted = (milestone.currentCapacity + amount) >= milestone.targetCapacity
            )
            milestoneRepository.updateMilestone(updated)
            
            if (updated.isCompleted) {
                achievementRepository.unlockAchievement("AHORRADOR ROOT")
            }
        }
    }

    fun deleteMilestone(milestone: Milestone) {
        viewModelScope.launch {
            milestoneRepository.deleteMilestone(milestone)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as FiosApplication)
                MissionsViewModel(
                    application.container.milestoneRepository,
                    application.container.achievementRepository,
                    application.container.userPreferencesRepository
                )
            }
        }
    }
}
