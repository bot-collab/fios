package com.fios.app.presentation.vault

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.fios.app.FiosApplication
import com.fios.app.domain.model.VaultAsset
import com.fios.app.domain.repository.VaultRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class VaultUiState(
    val assets: List<VaultAsset> = emptyList(),
    val totalVaultBalance: Double = 0.0,
    val isLoading: Boolean = false
)

class VaultViewModel(
    private val repository: VaultRepository
) : ViewModel() {

    val uiState: StateFlow<VaultUiState> = repository.getAssets().map { assets ->
        VaultUiState(
            assets = assets,
            totalVaultBalance = assets.sumOf { it.balance }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = VaultUiState(isLoading = true)
    )

    fun addAsset(name: String, amount: Double) {
        viewModelScope.launch {
            val newAsset = VaultAsset(
                name = name,
                balance = amount,
                lastSync = System.currentTimeMillis(),
                encryptionLevel = (64..256).random()
            )
            repository.insertAsset(newAsset)
        }
    }

    fun deleteAsset(asset: VaultAsset) {
        viewModelScope.launch {
            repository.deleteAsset(asset)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as FiosApplication)
                VaultViewModel(application.container.vaultRepository)
            }
        }
    }
}
