package com.fios.app.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.fios.app.presentation.analytics.AnalyticsScreen
import com.fios.app.presentation.commandcenter.CommandCenterScreen
import com.fios.app.presentation.missions.MissionsScreen
import com.fios.app.presentation.settings.SettingsScreen
import com.fios.app.presentation.splash.SplashScreen
import com.fios.app.presentation.vault.VaultScreen
import com.fios.app.presentation.wallet.WalletScreen

@Composable
fun SetupNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        modifier = modifier
    ) {
        composable(route = Screen.Splash.route) {
            SplashScreen(onLoadingComplete = {
                navController.navigate(Screen.CommandCenter.route) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            })
        }
        composable(route = Screen.CommandCenter.route) {
            CommandCenterScreen(
                onSettingsClick = { navController.navigate(Screen.Settings.route) }
            )
        }
        composable(route = Screen.Wallet.route) {
            WalletScreen()
        }
        composable(route = Screen.Analytics.route) {
            AnalyticsScreen()
        }
        composable(route = Screen.Missions.route) {
            MissionsScreen()
        }
        composable(route = Screen.Vault.route) {
            VaultScreen()
        }
        composable(route = Screen.Settings.route) {
            SettingsScreen()
        }
    }
}
