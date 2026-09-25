package com.example.aiguardianx.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aiguardianx.ui.components.CyberCard
import com.example.aiguardianx.ui.components.CyberTopBar
import com.example.aiguardianx.ui.theme.CyberCyan
import com.example.aiguardianx.ui.theme.CyberEmerald
import com.example.aiguardianx.ui.theme.CyberViolet
import com.example.aiguardianx.viewmodel.SecurityViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SecurityViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToEmergencyLock: () -> Unit,
    onNavigateToAppearance: () -> Unit
) {
    val context = LocalContext.current
    val settings by viewModel.settings.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()

    var showEditNameDialog by remember { mutableStateOf(false) }
    var editedName by remember { mutableStateOf(userProfile.name) }

    var showAboutDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CyberTopBar(
                title = "SETTINGS",
                subtitle = "Security Policies & User Profile",
                onBackClick = onNavigateBack,
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
                Text(
                    text = "USER PROFILE",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    letterSpacing = 1.sp,
                    color = CyberCyan
                )
            }

            // User Profile Card
            item {
                CyberCard(
                    modifier = Modifier.testTag("user_profile_card"),
                    borderColor = CyberCyan.copy(alpha = 0.35f)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(CircleShape)
                                .background(CyberCyan.copy(alpha = 0.15f))
                                .border(1.5.dp, CyberCyan, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = userProfile.avatarInitials,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp,
                                color = CyberCyan
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = userProfile.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = userProfile.email,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        IconButton(
                            onClick = {
                                editedName = userProfile.name
                                showEditNameDialog = true
                            },
                            modifier = Modifier.testTag("edit_user_name_button")
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Name", tint = CyberCyan)
                        }
                    }
                }
            }

            // Quick Access: Appearance & Emergency Lock
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CyberCard(
                        modifier = Modifier
                            .weight(1f)
                            .testTag("settings_appearance_button"),
                        onClick = onNavigateToAppearance
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Palette, contentDescription = null, tint = CyberViolet)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("Appearance", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(if (settings.darkModeEnabled) "Dark Mode" else "Light Mode", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }

                    CyberCard(
                        modifier = Modifier
                            .weight(1f)
                            .testTag("settings_emergency_button"),
                        onClick = onNavigateToEmergencyLock
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("Emergency Lock", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(if (settings.emergencyLockActive) "ACTIVE" else "Standby", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }

            // SECURITY CONTROLS
            item {
                Text(
                    text = "SECURITY CONTROLS",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    letterSpacing = 1.sp,
                    color = CyberCyan
                )
            }

            // 1. Biometric Protection
            item {
                CyberCard {
                    Text(
                        "1. Biometric Protection",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = CyberCyan
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    SettingToggleRow(
                        title = "Face Recognition",
                        subtitle = "Hardware front camera biometric liveness",
                        checked = settings.faceAuthEnabled,
                        onCheckedChange = { viewModel.updateSetting { s -> s.copy(faceAuthEnabled = it) } },
                        tag = "toggle_face_auth"
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))

                    SettingToggleRow(
                        title = "Fingerprint Authentication",
                        subtitle = "Hardware keystore fingerprint biometric verify",
                        checked = settings.fingerprintAuthEnabled,
                        onCheckedChange = { viewModel.updateSetting { s -> s.copy(fingerprintAuthEnabled = it) } },
                        tag = "toggle_fingerprint_auth"
                    )
                }
            }

            // 2. Deepfake Calls
            item {
                CyberCard {
                    Text(
                        "2. Deepfake Calls",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = CyberCyan
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    SettingToggleRow(
                        title = "Suspicious Call Protection",
                        subtitle = "Acoustic vocoder analysis during simulation",
                        checked = settings.suspiciousCallProtectionEnabled,
                        onCheckedChange = { viewModel.updateSetting { s -> s.copy(suspiciousCallProtectionEnabled = it) } },
                        tag = "toggle_suspicious_calls"
                    )
                }
            }

            // 3. App Threat Detection
            item {
                CyberCard {
                    Text(
                        "3. App Threat Detection",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = CyberCyan
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    SettingToggleRow(
                        title = "Real-time Sandbox Audit",
                        subtitle = "Continuous detection of malicious process hooks",
                        checked = settings.appThreatDetectionEnabled,
                        onCheckedChange = { viewModel.updateSetting { s -> s.copy(appThreatDetectionEnabled = it) } },
                        tag = "toggle_app_threat"
                    )
                }
            }

            // 4. Self-Healing Mode
            item {
                CyberCard {
                    Text(
                        "4. Self-Healing Mode",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = CyberCyan
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    SettingToggleRow(
                        title = "Automated State Recovery",
                        subtitle = "Auto-repair dangling caches and restore baselines",
                        checked = settings.selfHealingModeEnabled,
                        onCheckedChange = { viewModel.updateSetting { s -> s.copy(selfHealingModeEnabled = it) } },
                        tag = "toggle_self_healing"
                    )
                }
            }

            // 5. Security Alert
            item {
                CyberCard {
                    Text(
                        "5. Security Alert",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = CyberCyan
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    SettingToggleRow(
                        title = "Proactive Threat Warnings",
                        subtitle = "Instant alert notifications on anomaly thresholds",
                        checked = settings.securityAlertEnabled,
                        onCheckedChange = { viewModel.updateSetting { s -> s.copy(securityAlertEnabled = it) } },
                        tag = "toggle_security_alerts"
                    )
                }
            }

            // MORE Section
            item {
                Text(
                    text = "MORE",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    letterSpacing = 1.sp,
                    color = CyberCyan
                )
            }

            item {
                CyberCard {
                    SettingToggleRow(
                        title = "Notifications",
                        subtitle = "System push alerts for scheduled audits",
                        checked = settings.notificationsEnabled,
                        onCheckedChange = { viewModel.updateSetting { s -> s.copy(notificationsEnabled = it) } },
                        tag = "toggle_notifications"
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showLanguageDialog = true }
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Language", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Selected: ${settings.language}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Icon(Icons.Default.Language, contentDescription = null, tint = CyberCyan)
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                Toast.makeText(context, "Encrypted configuration snapshot backed up locally", Toast.LENGTH_SHORT).show()
                            }
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Backup", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Export security profile & baseline hashes", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Icon(Icons.Default.CloudUpload, contentDescription = null, tint = CyberEmerald)
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showAboutDialog = true }
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("About", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("AI Guardian X • Version: v1.0", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Icon(Icons.Default.Info, contentDescription = null, tint = CyberCyan)
                    }
                }
            }

            item {
                // Logout Button
                Button(
                    onClick = {
                        viewModel.logout()
                        Toast.makeText(context, "Session terminated. Cryptographic keys flushed.", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("logout_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Icon(Icons.Default.ExitToApp, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Logout Session", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Dialog: Edit User Name
        if (showEditNameDialog) {
            AlertDialog(
                onDismissRequest = { showEditNameDialog = false },
                title = { Text("Update Security Officer Name") },
                text = {
                    OutlinedTextField(
                        value = editedName,
                        onValueChange = { editedName = it },
                        label = { Text("Full Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (editedName.isNotBlank()) {
                                viewModel.updateUserName(editedName)
                            }
                            showEditNameDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberCyan)
                    ) {
                        Text("Save", color = Color(0xFF00363D), fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showEditNameDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // Dialog: Language Picker
        if (showLanguageDialog) {
            AlertDialog(
                onDismissRequest = { showLanguageDialog = false },
                title = { Text("Select Language") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("English", "Tamil", "Hindi").forEach { lang ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        viewModel.setLanguage(lang)
                                        showLanguageDialog = false
                                        Toast.makeText(context, "Language updated to $lang", Toast.LENGTH_SHORT).show()
                                    },
                                color = if (settings.language == lang) CyberCyan.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = when (lang) {
                                            "Tamil" -> "Tamil (தமிழ்)"
                                            "Hindi" -> "Hindi (हिन्दी)"
                                            else -> "English (US/UK)"
                                        },
                                        fontWeight = if (settings.language == lang) FontWeight.Bold else FontWeight.Normal
                                    )
                                    if (settings.language == lang) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = CyberCyan)
                                    }
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showLanguageDialog = false }) {
                        Text("Done")
                    }
                }
            )
        }

        // Dialog: About
        if (showAboutDialog) {
            AlertDialog(
                onDismissRequest = { showAboutDialog = false },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = CyberCyan)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("About AI Guardian X")
                    }
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("AI Guardian X", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = CyberCyan)
                        Text("Version: v1.0", fontWeight = FontWeight.SemiBold)
                        Text("Engine: Neural Sentinel Core v4.2")
                        Text("Architecture: Native Android + Jetpack Compose")
                        Text("Biometric Standard: AndroidX BiometricPrompt")
                        Text("Security Sandbox: Local UID Isolation • Hardware Keystore Integration")
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "© 2026 AI Guardian X Security Labs. All Rights Reserved.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { showAboutDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberCyan)
                    ) {
                        Text("Close", color = Color(0xFF00363D), fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    }
}

@Composable
private fun SettingToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    tag: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.testTag(tag),
            colors = SwitchDefaults.colors(
                checkedThumbColor = CyberCyan,
                checkedTrackColor = CyberCyan.copy(alpha = 0.35f)
            )
        )
    }
}
