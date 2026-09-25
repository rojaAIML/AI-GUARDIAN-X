package com.example.aiguardianx.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
fun SecurityModulesScreen(
    viewModel: SecurityViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToBehaviourDna: () -> Unit,
    onNavigateToDeepfakeCalls: () -> Unit,
    onNavigateToSelfHealing: () -> Unit,
    onNavigateToEmergencyLock: () -> Unit
) {
    val settings by viewModel.settings.collectAsState()
    val behaviourState by viewModel.behaviourState.collectAsState()
    val deepfakeState by viewModel.deepfakeState.collectAsState()
    val selfHealingState by viewModel.selfHealingState.collectAsState()

    Scaffold(
        topBar = {
            CyberTopBar(
                title = "SECURITY MODULES",
                subtitle = "Active Neural Countermeasures",
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
                    text = "SELECT ADVANCED DEFENSE MODULE",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = CyberCyan
                )
            }

            // Module 1: Behaviour DNA
            item {
                CyberCard(
                    onClick = onNavigateToBehaviourDna,
                    borderColor = CyberCyan.copy(alpha = 0.4f),
                    modifier = Modifier.testTag("module_card_behaviour_dna")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(CyberCyan.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Fingerprint,
                                contentDescription = null,
                                tint = CyberCyan,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        ThreatBadge(level = behaviourState.riskLevel, customText = "ANOMALY ${behaviourState.anomalyScore}%")
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "1. Behaviour DNA",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Continuous biometric telemetry analyzing internal typing intervals, swipe vector velocities, and capacitive touch pressure without storing characters.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Typing • Swipe • Pressure",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = CyberCyan
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Open Module",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyberCyan
                            )
                            Icon(
                                Icons.Default.ArrowForward,
                                contentDescription = null,
                                tint = CyberCyan,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            // Module 2: Deepfake Calls
            item {
                CyberCard(
                    onClick = onNavigateToDeepfakeCalls,
                    borderColor = CyberCrimson.copy(alpha = 0.4f),
                    modifier = Modifier.testTag("module_card_deepfake_calls")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(CyberCrimson.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.RecordVoiceOver,
                                contentDescription = null,
                                tint = CyberCrimson,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        ThreatBadge(level = ThreatLevel.HIGH, customText = "SYNTH DETECTOR")
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "2. Deepfake Calls",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Audio frequency variance and spectral jitter analyzer detecting synthetic AI voice clones and telecom spoofing. Includes interactive call simulation.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${deepfakeState.recentCalls.size} Logged Events (Demo)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = CyberCrimson
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Open Module",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyberCrimson
                            )
                            Icon(
                                Icons.Default.ArrowForward,
                                contentDescription = null,
                                tint = CyberCrimson,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            // Module 3: Self-Healing OS
            item {
                CyberCard(
                    onClick = onNavigateToSelfHealing,
                    borderColor = CyberEmerald.copy(alpha = 0.4f),
                    modifier = Modifier.testTag("module_card_self_healing")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(CyberEmerald.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Healing,
                                contentDescription = null,
                                tint = CyberEmerald,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        ThreatBadge(level = ThreatLevel.LOW, customText = "AUTO-HEAL READY")
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "3. Self-Healing OS",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Multi-phase pipeline (Analyze → Repair → Verify → Restore Point) performing safe app-level cache remediation and state rollback.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${selfHealingState.restorePoints.size} Restore Snapshots Available",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = CyberEmerald
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Open Module",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyberEmerald
                            )
                            Icon(
                                Icons.Default.ArrowForward,
                                contentDescription = null,
                                tint = CyberEmerald,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
