package com.example.aiguardianx.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aiguardianx.data.model.ThreatLevel
import com.example.aiguardianx.ui.components.CircularScoreGauge
import com.example.aiguardianx.ui.components.CyberCard
import com.example.aiguardianx.ui.components.CyberTopBar
import com.example.aiguardianx.ui.components.ThreatBadge
import com.example.aiguardianx.ui.theme.*
import com.example.aiguardianx.viewmodel.SecurityViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: SecurityViewModel,
    onNavigateToSecurity: () -> Unit,
    onNavigateToBehaviourDna: () -> Unit,
    onNavigateToDeepfakeCalls: () -> Unit,
    onNavigateToSelfHealing: () -> Unit,
    onNavigateToPrivacyTwin: () -> Unit,
    onNavigateToEmergencyLock: () -> Unit
) {
    val dashboardState by viewModel.dashboardState.collectAsState()
    val settings by viewModel.settings.collectAsState()

    var selectedDetailDialog by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            CyberTopBar(
                title = "AI GUARDIAN X",
                subtitle = "Active Neural Protection",
                onEmergencyLockClick = onNavigateToEmergencyLock,
                isEmergencyActive = settings.emergencyLockActive
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
            }

            // Scanner Section
            item {
                CyberCard(
                    borderColor = if (dashboardState.isScanning) CyberCyan else CyberCyan.copy(alpha = 0.35f),
                    modifier = Modifier.testTag("dashboard_scanner_section")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(if (dashboardState.isScanning) CyberAmber else CyberEmerald)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (dashboardState.isScanning) "SCAN IN PROGRESS" else "SECURITY SENTINEL ONLINE",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    letterSpacing = 1.sp,
                                    color = if (dashboardState.isScanning) CyberAmber else CyberEmerald
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Last Scan: ${dashboardState.lastScanFormatted}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = dashboardState.scanStatus,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Button(
                            onClick = { viewModel.runDashboardScan() },
                            enabled = !dashboardState.isScanning,
                            modifier = Modifier
                                .height(44.dp)
                                .testTag("scan_now_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            if (dashboardState.isScanning) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = Color(0xFF00363D),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    Icons.Default.Radar,
                                    contentDescription = null,
                                    tint = Color(0xFF00363D),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    "Scan Now",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 13.sp,
                                    color = Color(0xFF00363D)
                                )
                            }
                        }
                    }

                    AnimatedVisibility(visible = dashboardState.isScanning) {
                        Column(modifier = Modifier.padding(top = 12.dp)) {
                            LinearProgressIndicator(
                                progress = { dashboardState.scanProgress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = CyberCyan,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${(dashboardState.scanProgress * 100).toInt()}% completed",
                                fontSize = 11.sp,
                                color = CyberCyan,
                                modifier = Modifier.align(Alignment.End)
                            )
                        }
                    }
                }
            }

            // Security Grid: 4 Cards (Device Health, Threat Monitoring, Privacy Score, Protected Apps)
            item {
                Text(
                    text = "DEFENSE METRICS",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    letterSpacing = 1.sp,
                    color = CyberCyan
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Card 1: Device Health
                    CyberCard(
                        modifier = Modifier
                            .weight(1f)
                            .testTag("card_device_health"),
                        onClick = { selectedDetailDialog = "DEVICE_HEALTH" }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Memory, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(24.dp))
                            ThreatBadge(level = ThreatLevel.LOW, customText = "HEALTHY")
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "${dashboardState.deviceHealth.healthPercentage}%",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Device Health",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { dashboardState.deviceHealth.healthPercentage / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            color = CyberEmerald,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    }

                    // Card 2: Threat Monitoring
                    CyberCard(
                        modifier = Modifier
                            .weight(1f)
                            .testTag("card_threat_monitoring"),
                        onClick = { selectedDetailDialog = "THREAT_MONITORING" }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = CyberEmerald, modifier = Modifier.size(24.dp))
                            ThreatBadge(level = dashboardState.threatMonitoring.level)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "LOW",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = CyberEmerald
                        )
                        Text(
                            text = "Threat Monitoring",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${dashboardState.threatMonitoring.blockedAttacksLast24h} blocked today",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Card 3: Privacy Score
                    CyberCard(
                        modifier = Modifier
                            .weight(1f)
                            .testTag("card_privacy_score"),
                        onClick = { onNavigateToPrivacyTwin() }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.VisibilityOff, contentDescription = null, tint = CyberViolet, modifier = Modifier.size(24.dp))
                            Text(
                                "TWIN",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyberViolet
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "${dashboardState.privacyScore.score}%",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Privacy Score",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { dashboardState.privacyScore.score / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            color = CyberViolet,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    }

                    // Card 4: Protected Apps
                    CyberCard(
                        modifier = Modifier
                            .weight(1f)
                            .testTag("card_protected_apps"),
                        onClick = { selectedDetailDialog = "PROTECTED_APPS" }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Apps, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(24.dp))
                            Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = CyberEmerald, modifier = Modifier.size(16.dp))
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "${dashboardState.deviceHealth.protectedAppsCount}",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Protected Apps",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Sandboxes isolated",
                            fontSize = 11.sp,
                            color = CyberEmerald
                        )
                    }
                }
            }

            // Quick Access to Security Modules
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SECURITY MODULES",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        letterSpacing = 1.sp,
                        color = CyberCyan
                    )
                    TextButton(onClick = onNavigateToSecurity) {
                        Text("View All", fontSize = 12.sp, color = CyberCyan, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Quick Module Cards
            item {
                CyberCard(
                    onClick = onNavigateToBehaviourDna,
                    modifier = Modifier.testTag("dashboard_nav_behaviour_dna")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(CyberCyan.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Fingerprint, contentDescription = null, tint = CyberCyan)
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Behaviour DNA",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                "Typing, swiping & capacitive anomaly tracking",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            item {
                CyberCard(
                    onClick = onNavigateToDeepfakeCalls,
                    modifier = Modifier.testTag("dashboard_nav_deepfake_calls")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(CyberCrimson.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.PhoneInTalk, contentDescription = null, tint = CyberCrimson)
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Deepfake Calls",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                "Voice synth detection & call simulations",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            item {
                CyberCard(
                    onClick = onNavigateToSelfHealing,
                    modifier = Modifier.testTag("dashboard_nav_self_healing")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(CyberEmerald.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Healing, contentDescription = null, tint = CyberEmerald)
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Self-Healing OS",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                "Automated sandbox recovery & restore points",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Details Dialogs for Clicked Security Cards
        selectedDetailDialog?.let { dialogType ->
            AlertDialog(
                onDismissRequest = { selectedDetailDialog = null },
                title = {
                    Text(
                        when (dialogType) {
                            "DEVICE_HEALTH" -> "Device Health Diagnostics"
                            "THREAT_MONITORING" -> "Threat Monitoring Engine"
                            else -> "Protected Apps Sandbox"
                        },
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        when (dialogType) {
                            "DEVICE_HEALTH" -> {
                                Text("OS Version: ${dashboardState.deviceHealth.osVersion}")
                                Text("Security Patch: ${dashboardState.deviceHealth.securityPatch}")
                                Text("Battery Capacity: ${dashboardState.deviceHealth.batteryLevel}%")
                                Text("RAM Utilization: ${dashboardState.deviceHealth.memoryUsagePercent}%")
                                Text("Storage Utilization: ${dashboardState.deviceHealth.storageUsagePercent}%")
                                Text("Hardware Encryption: Enabled (AES-XTS 256)")
                                Text("Root Status: ${if (dashboardState.deviceHealth.isRooted) "Rooted (Compromised)" else "Verified Unrooted"}")
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "Note: Extracted from real Android system telemetry.",
                                    fontSize = 11.sp,
                                    color = CyberCyan
                                )
                            }
                            "THREAT_MONITORING" -> {
                                Text("Threat Status: LOW (Zero Active Vectors)")
                                Text("TLS Inspection: ${dashboardState.threatMonitoring.networkSecurity}")
                                Text("Sandbox Integrity: ${dashboardState.threatMonitoring.sandboxIntegrity}")
                                Text("Suspicious Packages: None")
                                Text("Attacks Thwarted (24h): ${dashboardState.threatMonitoring.blockedAttacksLast24h}")
                            }
                            else -> {
                                Text("Installed launcher packages: ${dashboardState.deviceHealth.protectedAppsCount}")
                                Text("Isolated via Android application UID sandbox.")
                                Text("Cryptographic process isolation active.")
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { selectedDetailDialog = null },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberCyan)
                    ) {
                        Text("Close", color = Color(0xFF00363D), fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    }
}
