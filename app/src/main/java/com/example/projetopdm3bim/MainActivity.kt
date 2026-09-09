package com.example.projetopdm3bim

import android.Manifest
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.projetopdm3bim.service.StepTrackerService
import com.example.projetopdm3bim.ui.*
import com.example.projetopdm3bim.ui.theme.ProjetoPDM3BIMTheme

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.values.all { it }) {
            startStepTrackerService()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val permissions = arrayOf(Manifest.permission.ACTIVITY_RECOGNITION)
        requestPermissionLauncher.launch(permissions)

        setContent {
            ProjetoPDM3BIMTheme {
                MainApp()
            }
        }
    }

    private fun startStepTrackerService() {
        val intent = Intent(this, StepTrackerService::class.java)
        startService(intent)
    }
}

@Composable
fun MainApp() {
    val navController = rememberNavController()
    val viewModel: StepViewModel = viewModel()
    
    // Observar a rota atual para atualizar o estado do menu inferior
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = stringResource(R.string.nav_home)) },
                    label = { Text(stringResource(R.string.nav_home)) },
                    selected = currentDestination?.route == "home",
                    onClick = { 
                        if (currentDestination?.route != "home") {
                            navController.navigate("home") {
                                popUpTo("home") { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.History, contentDescription = stringResource(R.string.nav_history)) },
                    label = { Text(stringResource(R.string.nav_history)) },
                    selected = currentDestination?.route == "history",
                    onClick = { 
                        if (currentDestination?.route != "history") {
                            navController.navigate("history") {
                                popUpTo("home") { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = stringResource(R.string.nav_profile)) },
                    label = { Text(stringResource(R.string.nav_profile)) },
                    selected = currentDestination?.route == "profile",
                    onClick = { 
                        if (currentDestination?.route != "profile") {
                            navController.navigate("profile") {
                                popUpTo("home") { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") { DashboardScreen(viewModel) }
            composable("history") { HistoryScreen(viewModel) }
            composable("profile") { ProfileScreen(viewModel) }
        }
    }
}
