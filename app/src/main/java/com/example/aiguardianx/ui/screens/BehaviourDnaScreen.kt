package com.example.aiguardianx.ui.screens

import android.view.MotionEvent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aiguardianx.data.model.ThreatLevel
import com.example.aiguardianx.ui.components.CircularScoreGauge
import com.example.aiguardianx.ui.components.CyberCard
import com.example.aiguardianx.ui.components.CyberTopBar
import com.example.aiguardianx.ui.components.ThreatBadge
import com.example.aiguardianx.ui.theme.*
import com.example.aiguardianx.viewmodel.SecurityViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs
import kotlin.math.hypot

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BehaviourDnaScreen(
    viewModel: SecurityViewModel,
    onNavigateBack: () -> Unit
) {
    val behaviourState by viewModel.behaviourState.collectAsState()

    var testInputText by remember { mutableStateOf("") }
    var lastKeystrokeTime by remember { mutableLongStateOf(0L) }

    // Swipe Canvas state
    var swipeStartPos by remember { mutableStateOf(Offset.Zero) }
    var swipeEndPos by remember { mutableStateOf(Offset.Zero) }
    var swipeStartTime by remember { mutableLongStateOf(0L) }

    // Touch pressure canvas state
    var currentTouchPressure by remember { mutableFloatStateOf(0.62f) }
    var currentTouchSize by remember { mutableFloatStateOf(0.14f) }
    var pressureSupported by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            CyberTopBar(
                title = "BEHAVIOUR DNA",
                subtitle = "Biometric Cadence & Anomaly Matrix",
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
                // Ethical Privacy & Security Notice
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = CyberCyan.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(12.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CyberCyan.copy(alpha = 0.3f)))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "ZERO KEYLOGGING GUARANTEE: In-app telemetry only. Passwords and text are never recorded or stored. Only inter-key timing deltas (ms) are evaluated.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            // Anomaly Overview Card
            item {
                CyberCard(
                    borderColor = CyberCyan.copy(alpha = 0.4f),
                    modifier = Modifier.testTag("behaviour_overview_card")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "ANOMALY RISK SCORE",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                letterSpacing = 1.sp,
                                color = CyberCyan
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${behaviourState.anomalyScore}",
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = " / 100",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                ThreatBadge(level = behaviourState.riskLevel)
                            }
                        }

                        CircularScoreGauge(
                            percentage = 100 - behaviourState.anomalyScore,
                            label = "CONFIDENCE",
                            gaugeColor = if (behaviourState.anomalyScore < 25) CyberEmerald else CyberAmber,
                            size = 80.dp
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                    Spacer(modifier = Modifier.height(14.dp))

                    // NORMAL vs CURRENT BEHAVIOUR Matrix
                    Text(
                        "NORMAL BEHAVIOUR vs CURRENT BEHAVIOUR",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        letterSpacing = 0.8.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            modifier = Modifier.weight(1f),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("Baseline Profile", fontSize = 11.sp, color = CyberEmerald, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(behaviourState.typing.normalPatternLabel, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                Text("Score: ${behaviourState.normalScore}%", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        Surface(
                            modifier = Modifier.weight(1f),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("Current Session", fontSize = 11.sp, color = CyberCyan, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(behaviourState.typing.currentPatternLabel, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                Text("Score: ${behaviourState.currentScore}%", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Difference Analysis: Δ ${abs(behaviourState.normalScore - behaviourState.currentScore)}% deviation. Variance remains within authenticated user profile threshold.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Notice: This is behavioural anomaly detection and does not claim to identify a person with certainty.",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }

            // FEATURE A: Typing Pattern
            item {
                CyberCard(
                    borderColor = CyberCyan.copy(alpha = 0.3f),
                    modifier = Modifier.testTag("feature_typing_pattern")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "A. TYPING PATTERN",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = CyberCyan
                        )
                        Icon(Icons.Default.Keyboard, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(20.dp))
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Type any sample phrase below to benchmark cadence rhythm and latency:",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = testInputText,
                        onValueChange = { newText ->
                            val now = System.currentTimeMillis()
                            if (lastKeystrokeTime != 0L) {
                                val delta = now - lastKeystrokeTime
                                viewModel.recordTypingInterval(delta)
                            }
                            lastKeystrokeTime = now
                            testInputText = newText
                        },
                        placeholder = { Text("Type here (e.g., 'cyber defense sentinel')...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("typing_test_input"),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // 4 Metrics Grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MetricTile(
                            modifier = Modifier.weight(1f),
                            label = "Typing Speed",
                            value = "${behaviourState.typing.speedWpm} WPM"
                        )
                        MetricTile(
                            modifier = Modifier.weight(1f),
                            label = "Inter-Key Delay",
                            value = "${behaviourState.typing.currentIntervalMs} ms"
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MetricTile(
                            modifier = Modifier.weight(1f),
                            label = "Avg Interval",
                            value = "${behaviourState.typing.avgIntervalMs} ms"
                        )
                        MetricTile(
                            modifier = Modifier.weight(1f),
                            label = "Timing Variance",
                            value = "±${behaviourState.typing.timingVariationMs} ms"
                        )
                    }
                }
            }

            // FEATURE B: Swiping Pattern
            item {
                CyberCard(
                    borderColor = CyberCyan.copy(alpha = 0.3f),
                    modifier = Modifier.testTag("feature_swiping_pattern")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "B. SWIPING PATTERN",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = CyberCyan
                        )
                        Icon(Icons.Default.Swipe, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(20.dp))
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Swipe inside the target zone to calculate gesture telemetry:",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Interactive Swipe Canvas
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                            .border(1.dp, CyberCyan.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                            .pointerInput(Unit) {
                                detectDragGestures(
                                    onDragStart = { offset ->
                                        swipeStartPos = offset
                                        swipeEndPos = offset
                                        swipeStartTime = System.currentTimeMillis()
                                    },
                                    onDrag = { change, _ ->
                                        change.consume()
                                        swipeEndPos = change.position
                                    },
                                    onDragEnd = {
                                        val duration = (System.currentTimeMillis() - swipeStartTime).coerceAtLeast(10)
                                        val dx = swipeEndPos.x - swipeStartPos.x
                                        val dy = swipeEndPos.y - swipeStartPos.y
                                        val distance = hypot(dx, dy)
                                        val speed = (distance / (duration / 1000f)).coerceAtLeast(0f)

                                        val direction = when {
                                            abs(dx) > abs(dy) && dx > 0 -> "Swipe Right"
                                            abs(dx) > abs(dy) && dx < 0 -> "Swipe Left"
                                            dy > 0 -> "Swipe Down"
                                            else -> "Swipe Up"
                                        }

                                        viewModel.recordSwipeGesture(
                                            direction = direction,
                                            distancePx = distance,
                                            durationMs = duration,
                                            speedPxPerSec = speed,
                                            startX = swipeStartPos.x,
                                            startY = swipeStartPos.y,
                                            endX = swipeEndPos.x,
                                            endY = swipeEndPos.y
                                        )
                                    }
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            if (swipeStartPos != Offset.Zero && swipeEndPos != Offset.Zero) {
                                drawCircle(
                                    color = CyberCyan,
                                    radius = 6.dp.toPx(),
                                    center = swipeStartPos
                                )
                                drawLine(
                                    color = CyberEmerald,
                                    start = swipeStartPos,
                                    end = swipeEndPos,
                                    strokeWidth = 3.dp.toPx(),
                                    cap = StrokeCap.Round
                                )
                                drawCircle(
                                    color = CyberEmerald,
                                    radius = 7.dp.toPx(),
                                    center = swipeEndPos
                                )
                            }
                        }

                        if (swipeStartPos == Offset.Zero) {
                            Text(
                                "👉 Drag & Swipe Here",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Swipe Information Visualizer
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MetricTile(modifier = Modifier.weight(1f), label = "Direction", value = behaviourState.swipe.direction)
                        MetricTile(modifier = Modifier.weight(1f), label = "Distance", value = "${behaviourState.swipe.distancePx.toInt()} px")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MetricTile(modifier = Modifier.weight(1f), label = "Duration", value = "${behaviourState.swipe.durationMs} ms")
                        MetricTile(modifier = Modifier.weight(1f), label = "Velocity", value = "${behaviourState.swipe.speedPxPerSec.toInt()} px/s")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Start: (${behaviourState.swipe.startX.toInt()}, ${behaviourState.swipe.startY.toInt()})  →  End: (${behaviourState.swipe.endX.toInt()}, ${behaviourState.swipe.endY.toInt()})",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // FEATURE C: Finger Pressure
            item {
                CyberCard(
                    borderColor = CyberCyan.copy(alpha = 0.3f),
                    modifier = Modifier.testTag("feature_finger_pressure")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "C. FINGER PRESSURE",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = CyberCyan
                        )
                        Icon(Icons.Default.TouchApp, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(20.dp))
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Touch and hold below. Capacitive screen reports relative normalized pressure (0.0 to 1.0):",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Interactive Pressure Pad
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                            .border(1.dp, CyberCyan.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                            .pointerInteropFilter { motionEvent ->
                                when (motionEvent.action) {
                                    MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                                        val p = motionEvent.pressure
                                        val s = motionEvent.size
                                        // Some emulators report 1.0f constant or 0f
                                        currentTouchPressure = p.coerceIn(0f, 1f)
                                        currentTouchSize = s.coerceIn(0f, 1f)
                                        val isSupported = p > 0.01f
                                        pressureSupported = isSupported
                                        viewModel.recordPressureTouch(currentTouchPressure, currentTouchSize, isSupported)
                                        true
                                    }
                                    else -> false
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Fingerprint, contentDescription = null, tint = CyberEmerald, modifier = Modifier.size(32.dp))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Press & Hold Target Zone", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (pressureSupported) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            MetricTile(
                                modifier = Modifier.weight(1f),
                                label = "Relative Pressure",
                                value = "${(currentTouchPressure * 100).toInt()}%"
                            )
                            MetricTile(
                                modifier = Modifier.weight(1f),
                                label = "Contact Area Size",
                                value = "${(currentTouchSize * 100).toInt()}%"
                            )
                        }
                    } else {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Finger pressure is not supported on this device.",
                                modifier = Modifier.padding(10.dp),
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Touch pressure is a normalized sensor ratio (0.0 - 1.0) and not measured in Newtons.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Recent Behaviour Events
            item {
                Text(
                    text = "RECENT BEHAVIOUR EVENTS",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    letterSpacing = 1.sp,
                    color = CyberCyan
                )
            }

            items(behaviourState.recentEvents) { event ->
                CyberCard(
                    modifier = Modifier.testTag("event_item_${event.id}"),
                    backgroundColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = when (event.type) {
                                    "TYPING" -> Icons.Default.Keyboard
                                    "SWIPE" -> Icons.Default.Swipe
                                    else -> Icons.Default.TouchApp
                                },
                                contentDescription = null,
                                tint = CyberCyan,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = event.type,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyberCyan
                            )
                        }

                        Text(
                            text = SimpleDateFormat("hh:mm:ss a", Locale.getDefault()).format(Date(event.timestamp)),
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = event.details,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface
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
fun MetricTile(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(2.dp))
            Text(value, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}
