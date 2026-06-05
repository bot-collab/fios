package com.fios.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.fios.app.navigation.BottomNavigationBar
import com.fios.app.navigation.Screen
import com.fios.app.navigation.SetupNavGraph
import com.fios.app.ui.theme.FIOSTheme
import com.fios.app.ui.theme.FIOSThemeType

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ -> }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        checkNotificationPermission()
        
        enableEdgeToEdge()
        setContent {
            val appContainer = (application as FiosApplication).container
            val preferences by appContainer.userPreferencesRepository.userPreferencesFlow
                .collectAsState(initial = null)

            FIOSTheme(themeType = preferences?.theme ?: FIOSThemeType.HACKER_GREEN) {
                MainScreen()
            }
        }
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != 
                PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = { 
            if (currentRoute != Screen.Splash.route) {
                BottomNavigationBar(navController = navController)
            }
        }
    ) { innerPadding ->
        SetupNavGraph(
            navController = navController,
            modifier = Modifier.padding(if (currentRoute == Screen.Splash.route) PaddingValues(0.dp) else innerPadding)
        )
    }
}
