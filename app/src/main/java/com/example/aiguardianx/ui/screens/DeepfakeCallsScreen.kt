package com.example.aiguardianx.ui.screens

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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.aiguardianx.data.model.ThreatLevel
import com.example.aiguardianx.ui.components.AudioWaveformVisualizer
import com.example.aiguardianx.ui.components.CyberCard
import com.example.aiguardianx.ui.components.CyberTopBar
import com.example.aiguardianx.ui.components.ThreatBadge
import com.example.aiguardianx.ui.theme.*
import com.example.aiguardianx.viewmodel.SecurityViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DeepfakeCallsScreen(
    viewModel: SecurityViewModel,
    onNavigateBack: () -> Unit
) {
    val deepfakeState by viewModel.deepfakeState.collectAsState()

    Scaffold(
        topBar = {
            CyberTopBar(
                title = "DEEPFAKE CALLS",
                subtitle = "Synthetic Voice & Vishing Defense",
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
                // Platform Limitation Notice
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
                            text = "ANDROID PLATFORM CONSTRAINT: Standard Android security sandboxing forbids third-party apps from intercepting real cellular call audio streams without root privileges. Interactive call simulations below demonstrate on-device acoustic modeling.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            // Protection Status Card
            item {
                CyberCard(
                    borderColor = CyberCyan.copy(alpha = 0.4f),
                    modifier = Modifier.testTag("call_protection_card")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "CALL PROTECTION STATUS",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                letterSpacing = 1.sp,
                                color = CyberCyan
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "ACOUSTIC SENTINEL ACTIVE",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = CyberEmerald
                            )
                        }
                        ThreatBadge(level = ThreatLevel.LOW, customText = "ONLINE")
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Vocal harmonics, pitch jitter, and neural vocoder artifacts are monitored during incoming calls to thwart voice clone fraud.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { viewModel.triggerSimulatedCall() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("simulate_call_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CyberCrimson)
                    ) {
                        Icon(Icons.Default.PhoneCallback, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Simulate Incoming Call (Demo)",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            // Recent Calls Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "RECENT CALLS",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        letterSpacing = 1.sp,
                        color = CyberCyan
                    )
                    Text(
                        text = "DEMO / SIMULATION",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberAmber
                    )
                }
            }

            // Recent Calls List
            items(deepfakeState.recentCalls) { call ->
                CyberCard(
                    modifier = Modifier.testTag("call_item_${call.id}"),
                    borderColor = when (call.riskLevel) {
                        ThreatLevel.CRITICAL -> CyberCrimson.copy(alpha = 0.5f)
                        ThreatLevel.HIGH -> CyberCrimson.copy(alpha = 0.3f)
                        ThreatLevel.MEDIUM -> CyberAmber.copy(alpha = 0.3f)
                        ThreatLevel.LOW -> CyberEmerald.copy(alpha = 0.3f)
                    }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when (call.riskLevel) {
                                            ThreatLevel.CRITICAL, ThreatLevel.HIGH -> CyberCrimson.copy(alpha = 0.2f)
                                            ThreatLevel.MEDIUM -> CyberAmber.copy(alpha = 0.2f)
                                            ThreatLevel.LOW -> CyberEmerald.copy(alpha = 0.2f)
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (call.detectionStatus.contains("DEEPFAKE")) Icons.Default.Warning else Icons.Default.Phone,
                                    contentDescription = null,
                                    tint = when (call.riskLevel) {
                                        ThreatLevel.CRITICAL, ThreatLevel.HIGH -> CyberCrimson
                                        ThreatLevel.MEDIUM -> CyberAmber
                                        ThreatLevel.LOW -> CyberEmerald
                                    },
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = call.callerName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = call.phoneNumber,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        ThreatBadge(level = call.riskLevel, customText = call.detectionStatus)
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Time: ${SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault()).format(Date(call.timestamp))}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Confidence: ${call.confidenceScore}%",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (call.confidenceScore > 85) CyberCrimson else CyberEmerald
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // SIMULATED INCOMING CALL DIALOG HUD
        if (deepfakeState.isSimulatingCall) {
            Dialog(
                onDismissRequest = { viewModel.dismissSimulatedCall() },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF060912)),
                    color = Color(0xFF060912)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Header
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(top = 40.dp)
                        ) {
                            Surface(
                                color = CyberCrimson.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "DEMO / SIMULATION",
                                    color = CyberCrimson,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    letterSpacing = 1.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            Box(
                                modifier = Modifier
                                    .size(90.dp)
                                    .clip(CircleShape)
                                    .background(CyberCyan.copy(alpha = 0.15f))
                                    .border(2.dp, CyberCyan.copy(alpha = 0.5f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = null,
                                    tint = CyberCyan,
                                    modifier = Modifier.size(54.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = deepfakeState.currentCall?.callerName ?: "Unknown Caller",
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = Color.White
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = deepfakeState.currentCall?.phoneNumber ?: "+1 (888) 012-9988",
                                fontSize = 14.sp,
                                color = TextSecondaryDark
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = when (deepfakeState.simulationStage) {
                                    0 -> "Incoming Call • Neural Scanner Ready..."
                                    1 -> "Analyzing Audio Resonances..."
                                    else -> "DETECTION RESULT: SYNTHETIC VOICE"
                                },
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = when (deepfakeState.simulationStage) {
                                    0 -> CyberCyan
                                    1 -> CyberAmber
                                    else -> CyberCrimson
                                }
                            )
                        }

                        // Middle: Waveform and Spectral Analyzer
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            AudioWaveformVisualizer(
                                isAnalyzing = deepfakeState.simulationStage > 0,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            if (deepfakeState.simulationStage > 0) {
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    color = SurfaceDark,
                                    shape = RoundedCornerShape(14.dp),
                                    border = CardDefaults.outlinedCardBorder().copy(
                                        brush = androidx.compose.ui.graphics.SolidColor(
                                            if (deepfakeState.simulationStage == 2) CyberCrimson else CyberCyan
                                        )
                                    )
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text("Neural Vocoder Probability", fontSize = 12.sp, color = TextSecondaryDark)
                                            Text(
                                                "${deepfakeState.liveConfidencePercent}%",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (deepfakeState.liveConfidencePercent > 70) CyberCrimson else CyberCyan
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))
                                        LinearProgressIndicator(
                                            progress = { deepfakeState.liveConfidencePercent / 100f },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(6.dp)
                                                .clip(RoundedCornerShape(3.dp)),
                                            color = if (deepfakeState.liveConfidencePercent > 70) CyberCrimson else CyberCyan,
                                            trackColor = SurfaceVariantDark
                                        )

                                        Spacer(modifier = Modifier.height(10.dp))
                                        Text(
                                            text = if (deepfakeState.simulationStage == 2)
                                                "⚠️ ALERT: Artificial pitch jitter detected. Unnatural glottal closure sequence matches ElevenLabs/Tortoise voice synthesis model."
                                            else
                                                "Sampling acoustic stream at 44.1kHz • Extracting Mel-frequency cepstral coefficients...",
                                            fontSize = 11.sp,
                                            color = TextPrimaryDark,
                                            lineHeight = 15.sp
                                        )
                                    }
                                }
                            }
                        }

                        // Bottom Action Controls
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 30.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (deepfakeState.simulationStage == 0) {
                                // Decline
                                FloatingActionButton(
                                    onClick = { viewModel.dismissSimulatedCall() },
                                    containerColor = CyberCrimson,
                                    contentColor = Color.White,
                                    shape = CircleShape,
                                    modifier = Modifier.size(64.dp)
                                ) {
                                    Icon(Icons.Default.CallEnd, contentDescription = "Decline Call", modifier = Modifier.size(28.dp))
                                }

                                // Accept & Scan
                                FloatingActionButton(
                                    onClick = { viewModel.answerSimulatedCall() },
                                    containerColor = CyberEmerald,
                                    contentColor = Color.White,
                                    shape = CircleShape,
                                    modifier = Modifier.size(64.dp)
                                ) {
                                    Icon(Icons.Default.Call, contentDescription = "Answer Call", modifier = Modifier.size(28.dp))
                                }
                            } else {
                                // End & Dismiss
                                Button(
                                    onClick = { viewModel.dismissSimulatedCall() },
                                    colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                ) {
                                    Text("Dismiss Call Simulation", fontWeight = FontWeight.Bold, color = Color(0xFF00363D))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
