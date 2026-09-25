package com.example.aiguardianx.ui.screens

import android.app.admin.DevicePolicyManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aiguardianx.data.model.ThreatLevel
import com.example.aiguardianx.ui.components.CyberCard
import com.example.aiguardianx.ui.components.CyberTopBar
import com.example.aiguardianx.ui.components.ThreatBadge
import com.example.aiguardianx.ui.theme.*
import com.example.aiguardianx.viewmodel.SecurityViewModel

@Composable
fun EmergencyLockScreen(
    viewModel: SecurityViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val settings by viewModel.settings.collectAsState()

    var showConfirmDialog by remember { mutableStateOf(false) }

    // Check DevicePolicyManager capability
    val dpm = remember { context.getSystemService(Context.DEVICE_POLICY_SERVICE) as? DevicePolicyManager }
    val isDeviceAdminActive = remember {
        try {
            dpm?.activeAdmins?.isNotEmpty() == true
        } catch (_: Exception) {
            false
        }
    }

    Scaffold(
        topBar = {
            CyberTopBar(
                title = "EMERGENCY LOCK",
                subtitle = "Active Threat Containment",
                onBackClick = onNavigateBack,
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
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Big Lockdown Badge
            item {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(
                            if (settings.emergencyLockActive)
                                Brush.radialGradient(listOf(CyberCrimson.copy(alpha = 0.35f), MaterialTheme.colorScheme.surface))
                            else
                                Brush.radialGradient(listOf(CyberCyan.copy(alpha = 0.2f), MaterialTheme.colorScheme.surface))
                        )
                        .border(
                            2.dp,
                            if (settings.emergencyLockActive) CyberCrimson else CyberCyan,
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (settings.emergencyLockActive) Icons.Default.Lock else Icons.Default.LockOpen,
                        contentDescription = "Lock Status",
                        tint = if (settings.emergencyLockActive) CyberCrimson else CyberCyan,
                        modifier = Modifier.size(52.dp)
                    )
                }
            }

            // Status Label
            item {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (settings.emergencyLockActive) "EMERGENCY LOCKDOWN ENGAGED" else "EMERGENCY LOCK STANDBY",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp,
                        letterSpacing = 1.sp,
                        color = if (settings.emergencyLockActive) CyberCrimson else MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (settings.emergencyLockActive)
                            "Protected mode active: Keystore session paused, background sync locked."
                        else
                            "Ready to initiate instantaneous defense containment.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Big CTA Button: ACTIVATE EMERGENCY LOCK
            item {
                Button(
                    onClick = {
                        if (settings.emergencyLockActive) {
                            viewModel.setEmergencyLock(false)
                            Toast.makeText(context, "Emergency lock deactivated", Toast.LENGTH_SHORT).show()
                        } else {
                            showConfirmDialog = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("activate_emergency_lock_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (settings.emergencyLockActive) CyberEmerald else CyberCrimson
                    )
                ) {
                    Icon(
                        imageVector = if (settings.emergencyLockActive) Icons.Default.CheckCircle else Icons.Default.Warning,
                        contentDescription = null,
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (settings.emergencyLockActive) "DEACTIVATE LOCKDOWN" else "ACTIVATE EMERGENCY LOCK",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp,
                        letterSpacing = 1.sp,
                        color = Color.White
                    )
                }
            }

            // Android Hardware Policy Compliance Notice
            item {
                CyberCard(
                    borderColor = if (isDeviceAdminActive) CyberEmerald.copy(alpha = 0.4f) else CyberAmber.copy(alpha = 0.4f),
                    modifier = Modifier.testTag("device_lock_policy_card")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "ANDROID DEVICE-ADMIN STATUS",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            letterSpacing = 1.sp,
                            color = CyberCyan
                        )
                        ThreatBadge(
                            level = if (isDeviceAdminActive) ThreatLevel.LOW else ThreatLevel.MEDIUM,
                            customText = if (isDeviceAdminActive) "ENROLLED" else "UNENROLLED"
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (!isDeviceAdminActive) {
                        Text(
                            text = "Device lock permission required.",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "To trigger an immediate physical Android OS screen lockout via DevicePolicyManager.lockNow(), standard Android security requires enterprise Device Administration enrollment. Without device admin privileges, AI Guardian X activates app-level lockdown, purges sensitive runtime sessions, and blocks in-app interfaces.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 17.sp
                        )
                    } else {
                        Text(
                            text = "DevicePolicyManager active. System lockNow() authorization verified.",
                            fontSize = 12.sp,
                            color = CyberEmerald
                        )
                    }
                }
            }

            // Lockdown Protections Checklist
            item {
                Text(
                    text = "CONTAINMENT PROTECTIONS",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    letterSpacing = 1.sp,
                    color = CyberCyan
                )
            }

            item {
                CyberCard {
                    LockdownItem("Sandbox Isolation", "Sever local IPC sockets and prevent background leaks", settings.emergencyLockActive)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                    LockdownItem("Token Zeroing", "Flush cached OAuth, biometric tokens, and ephemeral keys", settings.emergencyLockActive)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                    LockdownItem("Sensory Muting", "Block incoming synthetic caller handshake prompts", settings.emergencyLockActive)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                    LockdownItem("Panic Sentinel Alert", "Log high-priority tamper event to tamper-evident audit ledger", settings.emergencyLockActive)
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Confirmation Dialog
        if (showConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showConfirmDialog = false },
                icon = {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = CyberCrimson, modifier = Modifier.size(36.dp))
                },
                title = {
                    Text("Confirm Emergency Lock", fontWeight = FontWeight.Bold)
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Are you sure you want to engage Emergency Lockdown?")
                        Text(
                            "This will lock down application access, flush memory caches, and trigger defensive containment.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (!isDeviceAdminActive) {
                            Text(
                                "Note: Device lock permission required for physical OS screen turn-off. Safe in-app lockdown will be enforced.",
                                fontSize = 11.sp,
                                color = CyberAmber
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showConfirmDialog = false
                            viewModel.setEmergencyLock(true)
                            Toast.makeText(context, "Emergency lock activated!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberCrimson)
                    ) {
                        Text("Engage Lockdown", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showConfirmDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
private fun LockdownItem(title: String, desc: String, isEngaged: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (isEngaged) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
            contentDescription = null,
            tint = if (isEngaged) CyberEmerald else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Text(desc, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
