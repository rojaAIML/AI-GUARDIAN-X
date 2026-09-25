package com.example

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.example.aiguardianx.ui.navigation.NavRoutes
import com.example.aiguardianx.ui.screens.*
import com.example.aiguardianx.ui.theme.AIGuardianXTheme
import com.example.aiguardianx.ui.theme.CyberCyan
import com.example.aiguardianx.viewmodel.SecurityViewModel

class MainActivity : FragmentActivity() {

    private val viewModel: SecurityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val settings by viewModel.settings.collectAsState()
            val isLoggedIn by viewModel.isLoggedIn.collectAsState()

            AIGuardianXTheme(darkTheme = settings.darkModeEnabled) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route ?: if (isLoggedIn) NavRoutes.DASHBOARD else NavRoutes.LOGIN

                    val bottomNavRoutes = listOf(
                        NavRoutes.DASHBOARD,
                        NavRoutes.SECURITY,
                        NavRoutes.PRIVACY_TWIN,
                        NavRoutes.SETTINGS
                    )

                    val showBottomBar = currentRoute in bottomNavRoutes && isLoggedIn

                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        contentWindowInsets = WindowInsets.systemBars,
                        bottomBar = {
                            if (showBottomBar) {
                                NavigationBar(
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    contentColor = MaterialTheme.colorScheme.onSurface,
                                    tonalElevation = 8.dp,
                                    modifier = Modifier.testTag("cyber_bottom_navigation")
                                ) {
                                    // 1. Dashboard
                                    NavigationBarItem(
                                        icon = {
                                            Icon(
                                                imageVector = if (currentRoute == NavRoutes.DASHBOARD) Icons.Filled.Dashboard else Icons.Outlined.Dashboard,
                                                contentDescription = "Dashboard"
                                            )
                                        },
                                        label = { Text("Dashboard", fontSize = 11.sp) },
                                        selected = currentRoute == NavRoutes.DASHBOARD,
                                        onClick = {
                                            navController.navigate(NavRoutes.DASHBOARD) {
                                                popUpTo(navController.graph.findStartDestination().id) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = CyberCyan,
                                            selectedTextColor = CyberCyan,
                                            indicatorColor = CyberCyan.copy(alpha = 0.15f)
                                        ),
                                        modifier = Modifier.testTag("bottom_nav_dashboard")
                                    )

                                    // 2. Security
                                    NavigationBarItem(
                                        icon = {
                                            Icon(
                                                imageVector = if (currentRoute == NavRoutes.SECURITY) Icons.Filled.Security else Icons.Outlined.Security,
                                                contentDescription = "Security"
                                            )
                                        },
                                        label = { Text("Security", fontSize = 11.sp) },
                                        selected = currentRoute == NavRoutes.SECURITY,
                                        onClick = {
                                            navController.navigate(NavRoutes.SECURITY) {
                                                popUpTo(navController.graph.findStartDestination().id) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = CyberCyan,
                                            selectedTextColor = CyberCyan,
                                            indicatorColor = CyberCyan.copy(alpha = 0.15f)
                                        ),
                                        modifier = Modifier.testTag("bottom_nav_security")
                                    )

                                    // 3. Privacy Twin
                                    NavigationBarItem(
                                        icon = {
                                            Icon(
                                                imageVector = if (currentRoute == NavRoutes.PRIVACY_TWIN) Icons.Filled.VisibilityOff else Icons.Outlined.VisibilityOff,
                                                contentDescription = "Privacy Twin"
                                            )
                                        },
                                        label = { Text("Privacy Twin", fontSize = 11.sp) },
                                        selected = currentRoute == NavRoutes.PRIVACY_TWIN,
                                        onClick = {
                                            navController.navigate(NavRoutes.PRIVACY_TWIN) {
                                                popUpTo(navController.graph.findStartDestination().id) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = CyberCyan,
                                            selectedTextColor = CyberCyan,
                                            indicatorColor = CyberCyan.copy(alpha = 0.15f)
                                        ),
                                        modifier = Modifier.testTag("bottom_nav_privacy_twin")
                                    )

                                    // 4. Settings
                                    NavigationBarItem(
                                        icon = {
                                            Icon(
                                                imageVector = if (currentRoute == NavRoutes.SETTINGS) Icons.Filled.Settings else Icons.Outlined.Settings,
                                                contentDescription = "Settings"
                                            )
                                        },
                                        label = { Text("Settings", fontSize = 11.sp) },
                                        selected = currentRoute == NavRoutes.SETTINGS,
                                        onClick = {
                                            navController.navigate(NavRoutes.SETTINGS) {
                                                popUpTo(navController.graph.findStartDestination().id) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = CyberCyan,
                                            selectedTextColor = CyberCyan,
                                            indicatorColor = CyberCyan.copy(alpha = 0.15f)
                                        ),
                                        modifier = Modifier.testTag("bottom_nav_settings")
                                    )
                                }
                            }
                        }
                    ) { innerPadding ->
                        NavHost(
                            navController = navController,
                            startDestination = if (isLoggedIn) NavRoutes.DASHBOARD else NavRoutes.LOGIN,
                            modifier = Modifier.padding(innerPadding)
                        ) {
                            // 1. LOGIN
                            composable(NavRoutes.LOGIN) {
                                LoginScreen(
                                    viewModel = viewModel,
                                    onLoginSuccess = {
                                        navController.navigate(NavRoutes.DASHBOARD) {
                                            popUpTo(NavRoutes.LOGIN) { inclusive = true }
                                        }
                                    }
                                )
                            }

                            // 2. DASHBOARD
                            composable(NavRoutes.DASHBOARD) {
                                DashboardScreen(
                                    viewModel = viewModel,
                                    onNavigateToSecurity = { navController.navigate(NavRoutes.SECURITY) },
                                    onNavigateToBehaviourDna = { navController.navigate(NavRoutes.BEHAVIOUR_DNA) },
                                    onNavigateToDeepfakeCalls = { navController.navigate(NavRoutes.DEEPFAKE_CALLS) },
                                    onNavigateToSelfHealing = { navController.navigate(NavRoutes.SELF_HEALING_OS) },
                                    onNavigateToPrivacyTwin = { navController.navigate(NavRoutes.PRIVACY_TWIN) },
                                    onNavigateToEmergencyLock = { navController.navigate(NavRoutes.EMERGENCY_LOCK) }
                                )
                            }

                            // 3. SECURITY MODULES
                            composable(NavRoutes.SECURITY) {
                                SecurityModulesScreen(
                                    viewModel = viewModel,
                                    onNavigateBack = { navController.popBackStack() },
                                    onNavigateToBehaviourDna = { navController.navigate(NavRoutes.BEHAVIOUR_DNA) },
                                    onNavigateToDeepfakeCalls = { navController.navigate(NavRoutes.DEEPFAKE_CALLS) },
                                    onNavigateToSelfHealing = { navController.navigate(NavRoutes.SELF_HEALING_OS) },
                                    onNavigateToEmergencyLock = { navController.navigate(NavRoutes.EMERGENCY_LOCK) }
                                )
                            }

                            // 3.1 BEHAVIOUR DNA
                            composable(NavRoutes.BEHAVIOUR_DNA) {
                                BehaviourDnaScreen(
                                    viewModel = viewModel,
                                    onNavigateBack = { navController.popBackStack() }
                                )
                            }

                            // 3.2 DEEPFAKE CALLS
                            composable(NavRoutes.DEEPFAKE_CALLS) {
                                DeepfakeCallsScreen(
                                    viewModel = viewModel,
                                    onNavigateBack = { navController.popBackStack() }
                                )
                            }

                            // 3.3 SELF-HEALING OS
                            composable(NavRoutes.SELF_HEALING_OS) {
                                SelfHealingOsScreen(
                                    viewModel = viewModel,
                                    onNavigateBack = { navController.popBackStack() }
                                )
                            }

                            // 7. PRIVACY TWIN
                            composable(NavRoutes.PRIVACY_TWIN) {
                                PrivacyTwinScreen(
                                    viewModel = viewModel,
                                    onNavigateBack = { navController.popBackStack() },
                                    onNavigateToEmergencyLock = { navController.navigate(NavRoutes.EMERGENCY_LOCK) }
                                )
                            }

                            // 4. SETTINGS
                            composable(NavRoutes.SETTINGS) {
                                SettingsScreen(
                                    viewModel = viewModel,
                                    onNavigateBack = { navController.popBackStack() },
                                    onNavigateToEmergencyLock = { navController.navigate(NavRoutes.EMERGENCY_LOCK) },
                                    onNavigateToAppearance = { navController.navigate(NavRoutes.APPEARANCE) }
                                )
                            }

                            // 5. EMERGENCY LOCK
                            composable(NavRoutes.EMERGENCY_LOCK) {
                                EmergencyLockScreen(
                                    viewModel = viewModel,
                                    onNavigateBack = { navController.popBackStack() }
                                )
                            }

                            // 6. APPEARANCE
                            composable(NavRoutes.APPEARANCE) {
                                AppearanceScreen(
                                    viewModel = viewModel,
                                    onNavigateBack = { navController.popBackStack() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
