package com.fios.app.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Splash : Screen("splash", "Startup", Icons.Default.Home)
    object CommandCenter : Screen("command_center", "Ops", Icons.Default.Terminal)
    object Wallet : Screen("wallet", "Billetera", Icons.Default.AccountBalanceWallet)
    object Analytics : Screen("analytics", "Telemetría", Icons.Default.Assessment)
    object Missions : Screen("missions", "Misiones", Icons.Default.RocketLaunch)
    object Vault : Screen("vault", "Bóveda", Icons.Default.Lock)
    object Settings : Screen("settings", "Ajustes", Icons.Default.Settings)
}

val bottomNavItems = listOf(
    Screen.CommandCenter,
    Screen.Wallet,
    Screen.Analytics,
    Screen.Missions,
    Screen.Vault,
)
