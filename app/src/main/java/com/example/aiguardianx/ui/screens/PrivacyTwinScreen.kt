package com.example.aiguardianx.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import com.example.aiguardianx.ui.components.CircularScoreGauge
import com.example.aiguardianx.ui.components.CyberCard
import com.example.aiguardianx.ui.components.CyberTopBar
import com.example.aiguardianx.ui.components.ThreatBadge
import com.example.aiguardianx.ui.theme.*
import com.example.aiguardianx.viewmodel.SecurityViewModel

@Composable
fun PrivacyTwinScreen(
    viewModel: SecurityViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToEmergencyLock: () -> Unit
) {
    val context = LocalContext.current
    val privacyState by viewModel.privacyTwinState.collectAsState()
    val settings by viewModel.settings.collectAsState()

    Scaffold(
        topBar = {
            CyberTopBar(
                title = "PRIVACY TWIN",
                subtitle = "Digital Exposure & Broker Shield",
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
            }

            // Privacy Score & Scan Header
            item {
                CyberCard(
                    borderColor = CyberViolet.copy(alpha = 0.4f),
                    modifier = Modifier.testTag("privacy_twin_score_card")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "PRIVACY TWIN HEALTH",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                letterSpacing = 1.sp,
                                color = CyberViolet
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "EXPOSURE MINIMAL",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = CyberEmerald
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = privacyState.scanStageText,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        CircularScoreGauge(
                            percentage = privacyState.privacyScore,
                            label = "PRIVACY",
                            gaugeColor = CyberViolet,
                            size = 84.dp
                        )
                    }

                    AnimatedVisibility(visible = privacyState.isScanning) {
                        Column(modifier = Modifier.padding(top = 12.dp)) {
                            LinearProgressIndicator(
                                progress = { privacyState.scanProgress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = CyberViolet,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // SCAN Flow Button: SCAN -> Privacy Analysis -> Risk Detection -> Privacy Recommendations
                    Button(
                        onClick = { viewModel.runPrivacyTwinScan() },
                        enabled = !privacyState.isScanning,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("privacy_scan_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CyberViolet)
                    ) {
                        Icon(Icons.Default.FindInPage, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (privacyState.isScanning) "Running 4-Phase Privacy Audit..." else "SCAN PRIVACY PROFILE",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            // Sensitive App Permissions Audit
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "APP PERMISSION AUDIT",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        letterSpacing = 1.sp,
                        color = CyberCyan
                    )
                    Text(
                        text = "${privacyState.auditedApps.size} APPS INSPECTED",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberCyan
                    )
                }
            }

            items(privacyState.auditedApps) { app ->
                CyberCard(
                    modifier = Modifier.testTag("audit_app_${app.packageName}"),
                    borderColor = when (app.riskLevel) {
                        ThreatLevel.HIGH -> CyberCrimson.copy(alpha = 0.35f)
                        ThreatLevel.MEDIUM -> CyberAmber.copy(alpha = 0.35f)
                        else -> MaterialTheme.colorScheme.outline
                    }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = app.appName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = app.packageName,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        ThreatBadge(level = app.riskLevel)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (app.sensitivePermissions.isNotEmpty()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            app.sensitivePermissions.forEach { perm ->
                                Surface(
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = perm,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    } else {
                        Text(
                            text = "No high-risk sensitive permissions requested",
                            fontSize = 11.sp,
                            color = CyberEmerald
                        )
                    }
                }
            }

            // BROKER FEED Section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "BROKER FEED",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        letterSpacing = 1.sp,
                        color = CyberCyan
                    )
                    Surface(
                        color = CyberAmber.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "DEMO DATA",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = CyberAmber,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            item {
                Text(
                    text = "External commercial telemetry aggregators and dark-pattern brokers. (Demo data simulated for testing).",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            items(privacyState.brokerFeed) { broker ->
                CyberCard(
                    modifier = Modifier.testTag("broker_item_${broker.id}"),
                    borderColor = CyberAmber.copy(alpha = 0.25f)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = broker.brokerName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Source: ${broker.breachSource}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        ThreatBadge(level = broker.threatLevel)
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Exposed Attributes: ${broker.dataExposed}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Reported: ${broker.dateReported}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        TextButton(
                            onClick = {
                                Toast.makeText(context, "Opt-out challenge dispatched to ${broker.brokerName}", Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Text(broker.optOutStatus, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CyberCyan)
                        }
                    }
                }
            }

            // PRIVACY RECOMMENDATIONS
            item {
                Text(
                    text = "PRIVACY RECOMMENDATIONS",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    letterSpacing = 1.sp,
                    color = CyberCyan
                )
            }

            items(privacyState.recommendations) { rec ->
                CyberCard(modifier = Modifier.testTag("rec_item")) {
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(
                            Icons.Default.Shield,
                            contentDescription = null,
                            tint = CyberEmerald,
                            modifier = Modifier
                                .size(18.dp)
                                .padding(top = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = rec,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 17.sp
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
