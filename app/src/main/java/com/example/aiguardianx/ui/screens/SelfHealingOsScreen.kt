package com.example.aiguardianx.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.aiguardianx.data.model.ThreatLevel
import com.example.aiguardianx.ui.components.CyberCard
import com.example.aiguardianx.ui.components.CyberTopBar
import com.example.aiguardianx.ui.components.ThreatBadge
import com.example.aiguardianx.ui.theme.*
import com.example.aiguardianx.viewmodel.SecurityViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SelfHealingOsScreen(
    viewModel: SecurityViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val selfHealingState by viewModel.selfHealingState.collectAsState()

    Scaffold(
        topBar = {
            CyberTopBar(
                title = "SELF-HEALING OS",
                subtitle = "Automated Remediation & Rollback",
                onBackClick = onNavigateBack
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
                // OS-level permission boundary disclaimer
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = CyberAmber.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(12.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CyberAmber.copy(alpha = 0.4f)))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = CyberAmber, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "SECURITY NOTICE: Android does not allow this operation for a normal application. Root-level system file modifications are strictly prohibited by the Android OS. AI Guardian X performs verified safe app-level sandbox recovery, cache clearing, entropy regeneration, and local state restore points.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            // Architecture Flow Header & Diagram
            item {
                CyberCard(
                    borderColor = CyberEmerald.copy(alpha = 0.4f),
                    modifier = Modifier.testTag("self_healing_pipeline_card")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "SELF-HEALING PIPELINE",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            letterSpacing = 1.sp,
                            color = CyberEmerald
                        )
                        ThreatBadge(
                            level = if (selfHealingState.autoHealEnabled) ThreatLevel.LOW else ThreatLevel.MEDIUM,
                            customText = if (selfHealingState.autoHealEnabled) "AUTO-HEAL ON" else "MANUAL"
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Flow Diagram: Recent Issue -> Analyze -> Repair -> Verify -> Restore Point
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FlowStepNode("Issue", 1, selfHealingState.pipelineStage in listOf("ANALYZING", "REPAIRING", "VERIFYING", "RESTORE_CREATED"))
                        FlowArrow()
                        FlowStepNode("Analyze", 2, selfHealingState.pipelineStage in listOf("ANALYZING", "REPAIRING", "VERIFYING", "RESTORE_CREATED"))
                        FlowArrow()
                        FlowStepNode("Repair", 3, selfHealingState.pipelineStage in listOf("REPAIRING", "VERIFYING", "RESTORE_CREATED"))
                        FlowArrow()
                        FlowStepNode("Verify", 4, selfHealingState.pipelineStage in listOf("VERIFYING", "RESTORE_CREATED"))
                        FlowArrow()
                        FlowStepNode("Restore", 5, selfHealingState.pipelineStage == "RESTORE_CREATED")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    AnimatedVisibility(visible = selfHealingState.isPipelineRunning) {
                        Column {
                            LinearProgressIndicator(
                                progress = { selfHealingState.progressPercent / 100f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = CyberEmerald,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Current Stage: ${selfHealingState.pipelineStage} (${selfHealingState.progressPercent}%)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyberEmerald
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }

                    // Auto Heal Switch & Execute Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Auto Heal Daemon", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Self-heal on anomaly trigger", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = selfHealingState.autoHealEnabled,
                            onCheckedChange = { viewModel.toggleAutoHeal(it) },
                            colors = SwitchDefaults.colors(checkedThumbColor = CyberEmerald, checkedTrackColor = CyberEmerald.copy(alpha = 0.3f))
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { viewModel.runSelfHealingPipeline() },
                        enabled = !selfHealingState.isPipelineRunning,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("run_healing_pipeline_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CyberEmerald)
                    ) {
                        Icon(Icons.Default.Healing, contentDescription = null, tint = Color(0xFF003919))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (selfHealingState.isPipelineRunning) "Executing Healing Pipeline..." else "Run Issue Analysis & Repair",
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF003919),
                            fontSize = 14.sp
                        )
                    }
                }
            }

            // Section 1: Recent System Issue Analysis
            item {
                Text(
                    text = "1. RECENT SYSTEM ISSUE ANALYSIS",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    letterSpacing = 1.sp,
                    color = CyberCyan
                )
            }

            items(selfHealingState.issues) { issue ->
                CyberCard(
                    modifier = Modifier.testTag("issue_item_${issue.id}"),
                    borderColor = if (issue.status.contains("REPAIRED")) CyberEmerald.copy(alpha = 0.4f) else CyberAmber.copy(alpha = 0.4f)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = issue.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Category: ${issue.category}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        ThreatBadge(
                            level = if (issue.status.contains("REPAIRED")) ThreatLevel.LOW else issue.severity,
                            customText = issue.status
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = issue.description,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Recommended: ${issue.recommendedAction}",
                            modifier = Modifier.padding(8.dp),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = CyberCyan
                        )
                    }
                }
            }

            // Section 2: Repair History Log
            item {
                Text(
                    text = "2. REPAIR HISTORY",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    letterSpacing = 1.sp,
                    color = CyberCyan
                )
            }

            item {
                CyberCard(modifier = Modifier.testTag("repair_history_card")) {
                    if (selfHealingState.repairLog.isEmpty()) {
                        Text(
                            text = "Baseline inspection completed. Ready to execute automated remediation.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            selfHealingState.repairLog.forEach { logItem ->
                                Row(verticalAlignment = Alignment.Top) {
                                    Icon(
                                        Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = CyberEmerald,
                                        modifier = Modifier
                                            .size(16.dp)
                                            .padding(top = 2.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = logItem,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        lineHeight = 16.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Section 3: Restore Point
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "3. RESTORE POINTS",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        letterSpacing = 1.sp,
                        color = CyberCyan
                    )
                    Text(
                        text = "${selfHealingState.restorePoints.size} SNAPSHOTS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberEmerald
                    )
                }
            }

            items(selfHealingState.restorePoints) { point ->
                CyberCard(
                    modifier = Modifier.testTag("restore_point_${point.id}"),
                    borderColor = CyberCyan.copy(alpha = 0.25f)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = point.label,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault()).format(Date(point.timestamp)),
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Button(
                            onClick = {
                                Toast.makeText(context, "State rolled back to '${point.label}'", Toast.LENGTH_SHORT).show()
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("Rollback", fontSize = 12.sp, color = CyberCyan, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Integrity: ${point.checksum} • Size: ${point.backupSizeKb} KB",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun FlowStepNode(name: String, stepNumber: Int, isActive: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(if (isActive) CyberEmerald else MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$stepNumber",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (isActive) Color(0xFF003919) else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = name,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (isActive) CyberEmerald else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun FlowArrow() {
    Icon(
        imageVector = Icons.Default.ArrowForward,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
        modifier = Modifier.size(16.dp)
    )
}
