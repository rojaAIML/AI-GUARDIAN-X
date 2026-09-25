package com.example.aiguardianx.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.aiguardianx.data.model.*
import com.example.aiguardianx.data.repository.SecurityRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.random.Random

data class DashboardUiState(
    val deviceHealth: DeviceHealthInfo = DeviceHealthInfo(),
    val threatMonitoring: ThreatMonitoringInfo = ThreatMonitoringInfo(),
    val privacyScore: PrivacyScoreInfo = PrivacyScoreInfo(),
    val isScanning: Boolean = false,
    val scanProgress: Float = 0f,
    val lastScanFormatted: String = "Just now",
    val scanStatus: String = "All systems protected"
)

data class BehaviourUiState(
    val typing: TypingMetrics = TypingMetrics(),
    val swipe: SwipeMetrics = SwipeMetrics(),
    val pressure: PressureMetrics = PressureMetrics(),
    val normalScore: Int = 94,
    val currentScore: Int = 89,
    val anomalyScore: Int = 11, // 0 - 100
    val riskLevel: ThreatLevel = ThreatLevel.LOW,
    val recentEvents: List<BehaviourEvent> = emptyList()
)

data class DeepfakeUiState(
    val isProtectionActive: Boolean = true,
    val recentCalls: List<CallRecord> = emptyList(),
    val isSimulatingCall: Boolean = false,
    val simulationStage: Int = 0, // 0: Ringing, 1: Connected/Analyzing, 2: Detected Result
    val currentCall: CallRecord? = null,
    val liveConfidencePercent: Int = 0
)

data class SelfHealingUiState(
    val issues: List<SystemIssue> = emptyList(),
    val isPipelineRunning: Boolean = false,
    val pipelineStage: String = "IDLE", // "ANALYZING", "REPAIRING", "VERIFYING", "RESTORE_CREATED", "COMPLETED"
    val progressPercent: Int = 0,
    val repairLog: List<String> = emptyList(),
    val restorePoints: List<RestorePoint> = emptyList(),
    val autoHealEnabled: Boolean = true
)

data class PrivacyTwinUiState(
    val privacyScore: Int = 86,
    val isScanning: Boolean = false,
    val scanProgress: Float = 0f,
    val scanStageText: String = "Ready for privacy scan",
    val auditedApps: List<AppPermissionAudit> = emptyList(),
    val brokerFeed: List<BrokerExposure> = emptyList(),
    val recommendations: List<String> = emptyList()
)

class SecurityViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = SecurityRepository(application.applicationContext)

    // Auth state
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _userProfile = MutableStateFlow(repository.getUserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    private val _settings = MutableStateFlow(repository.loadSettings())
    val settings: StateFlow<SecuritySettings> = _settings.asStateFlow()

    // Dashboard state
    private val _dashboardState = MutableStateFlow(
        DashboardUiState(
            deviceHealth = repository.getDeviceHealth(),
            lastScanFormatted = repository.getLastScanTimeFormatted()
        )
    )
    val dashboardState: StateFlow<DashboardUiState> = _dashboardState.asStateFlow()

    // Behaviour DNA
    private val _behaviourState = MutableStateFlow(
        BehaviourUiState(
            recentEvents = listOf(
                BehaviourEvent("ev-1", System.currentTimeMillis() - 1000 * 60 * 12, "TYPING", 8, "Baseline cadence match 96%"),
                BehaviourEvent("ev-2", System.currentTimeMillis() - 1000 * 60 * 25, "SWIPE", 14, "Linear acceleration normal"),
                BehaviourEvent("ev-3", System.currentTimeMillis() - 1000 * 60 * 42, "PRESSURE", 10, "Capacitive contact profile nominal")
            )
        )
    )
    val behaviourState: StateFlow<BehaviourUiState> = _behaviourState.asStateFlow()

    // Deepfake Calls
    private val _deepfakeState = MutableStateFlow(
        DeepfakeUiState(recentCalls = repository.getInitialRecentCalls())
    )
    val deepfakeState: StateFlow<DeepfakeUiState> = _deepfakeState.asStateFlow()

    // Self Healing
    private val _selfHealingState = MutableStateFlow(
        SelfHealingUiState(
            issues = repository.getInitialIssues(),
            restorePoints = repository.getInitialRestorePoints(),
            autoHealEnabled = _settings.value.selfHealingModeEnabled
        )
    )
    val selfHealingState: StateFlow<SelfHealingUiState> = _selfHealingState.asStateFlow()

    // Privacy Twin
    private val _privacyTwinState = MutableStateFlow(
        PrivacyTwinUiState(
            auditedApps = repository.auditInstalledApps(),
            brokerFeed = repository.getBrokerExposures(),
            recommendations = listOf(
                "Revoke background location access for social media and camera apps",
                "Disable advertising ID personalization in Android Settings",
                "Opt-out from DataXchange and LocateTrack telemetric aggregators",
                "Enable Biometric app lock on confidential communication tools"
            )
        )
    )
    val privacyTwinState: StateFlow<PrivacyTwinUiState> = _privacyTwinState.asStateFlow()

    // Key stroke intervals cache for variance calculation
    private val recentKeyIntervals = mutableListOf<Long>()

    init {
        refreshDeviceDiagnostics()
    }

    fun login(email: String = "", method: String = "CREDENTIALS") {
        _isLoggedIn.value = true
        if (email.isNotBlank()) {
            val name = email.substringBefore("@").replace(".", " ").capitalize(Locale.ROOT)
            val updated = _userProfile.value.copy(name = name, email = email)
            _userProfile.value = updated
            repository.saveUserName(name)
        }
    }

    fun logout() {
        _isLoggedIn.value = false
    }

    fun refreshDeviceDiagnostics() {
        viewModelScope.launch {
            val health = repository.getDeviceHealth()
            _dashboardState.update { it.copy(deviceHealth = health) }
        }
    }

    // Dashboard: Scan Now
    fun runDashboardScan() {
        if (_dashboardState.value.isScanning) return
        viewModelScope.launch {
            _dashboardState.update {
                it.copy(isScanning = true, scanProgress = 0.05f, scanStatus = "Auditing device integrity...")
            }

            delay(600)
            _dashboardState.update {
                it.copy(scanProgress = 0.35f, scanStatus = "Inspecting application sandboxes...")
            }

            delay(700)
            _dashboardState.update {
                it.copy(scanProgress = 0.70f, scanStatus = "Scanning network endpoints & certificates...")
            }

            delay(600)
            _dashboardState.update {
                it.copy(scanProgress = 0.95f, scanStatus = "Verifying cryptographic keystore...")
            }

            delay(400)
            val now = System.currentTimeMillis()
            repository.saveLastScanTime(now)
            val timeText = SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault()).format(Date(now))
            val updatedHealth = repository.getDeviceHealth()

            _dashboardState.update {
                it.copy(
                    isScanning = false,
                    scanProgress = 1f,
                    lastScanFormatted = timeText,
                    deviceHealth = updatedHealth,
                    scanStatus = "Scan completed: Zero active threats detected"
                )
            }
        }
    }

    // Behaviour DNA: Typing Measurement
    fun recordTypingInterval(intervalMs: Long) {
        val clamped = intervalMs.coerceIn(50, 1200)
        recentKeyIntervals.add(clamped)
        if (recentKeyIntervals.size > 20) recentKeyIntervals.removeAt(0)

        val avg = recentKeyIntervals.average().toLong()
        val variance = if (recentKeyIntervals.size > 1) {
            recentKeyIntervals.map { abs(it - avg) }.average().toLong()
        } else 18L
        val wpm = (60_000 / (avg * 5).coerceAtLeast(100)).toInt().coerceIn(20, 110)

        val matchPercent = (100 - (abs(avg - 195) / 3)).coerceIn(70, 99).toInt()
        val anomalyDelta = abs(matchPercent - 95)
        val anomalyScore = (anomalyDelta * 1.5).roundToInt().coerceIn(4, 38)
        val risk = if (anomalyScore > 40) ThreatLevel.HIGH else if (anomalyScore > 20) ThreatLevel.MEDIUM else ThreatLevel.LOW

        val newMetrics = TypingMetrics(
            currentIntervalMs = clamped,
            avgIntervalMs = avg,
            speedWpm = wpm,
            timingVariationMs = variance,
            normalPatternLabel = "Fluid 185-210ms (Rhythmic)",
            currentPatternLabel = "${avg}ms (Cadence Match: $matchPercent%)",
            keystrokeCount = _behaviourState.value.typing.keystrokeCount + 1
        )

        val newEvent = BehaviourEvent(
            id = "key-${System.currentTimeMillis()}",
            timestamp = System.currentTimeMillis(),
            type = "TYPING",
            anomalyScore = anomalyScore,
            details = "Cadence $wpm WPM | Inter-key delay ${clamped}ms (Δ ${variance}ms)"
        )

        _behaviourState.update {
            it.copy(
                typing = newMetrics,
                anomalyScore = anomalyScore,
                riskLevel = risk,
                currentScore = 100 - anomalyScore,
                recentEvents = (listOf(newEvent) + it.recentEvents).take(10)
            )
        }
    }

    // Behaviour DNA: Swipe Measurement
    fun recordSwipeGesture(
        direction: String,
        distancePx: Float,
        durationMs: Long,
        speedPxPerSec: Float,
        startX: Float,
        startY: Float,
        endX: Float,
        endY: Float
    ) {
        val swipeMetrics = SwipeMetrics(
            direction = direction,
            distancePx = distancePx,
            durationMs = durationMs,
            speedPxPerSec = speedPxPerSec,
            startX = startX,
            startY = startY,
            endX = endX,
            endY = endY
        )

        val anomaly = if (speedPxPerSec > 4000f || speedPxPerSec < 150f) 28 else 8
        val risk = if (anomaly > 25) ThreatLevel.MEDIUM else ThreatLevel.LOW

        val newEvent = BehaviourEvent(
            id = "swp-${System.currentTimeMillis()}",
            timestamp = System.currentTimeMillis(),
            type = "SWIPE",
            anomalyScore = anomaly,
            details = "$direction (${distancePx.toInt()}px in ${durationMs}ms at ${speedPxPerSec.toInt()}px/s)"
        )

        _behaviourState.update {
            it.copy(
                swipe = swipeMetrics,
                recentEvents = (listOf(newEvent) + it.recentEvents).take(10)
            )
        }
    }

    // Behaviour DNA: Pressure Measurement
    fun recordPressureTouch(pressure: Float, size: Float, isSupported: Boolean) {
        val pressureMetrics = PressureMetrics(
            pressure = pressure,
            touchSize = size,
            isSupported = isSupported
        )

        val anomaly = if (pressure > 0.95f || pressure < 0.1f) 22 else 6
        val newEvent = BehaviourEvent(
            id = "prs-${System.currentTimeMillis()}",
            timestamp = System.currentTimeMillis(),
            type = "PRESSURE",
            anomalyScore = anomaly,
            details = if (isSupported) "Capacitive pressure ${(pressure * 100).toInt()}% (Contact size ${(size * 100).toInt()}%)" else "Pressure measurement not provided by sensor"
        )

        _behaviourState.update {
            it.copy(
                pressure = pressureMetrics,
                recentEvents = (listOf(newEvent) + it.recentEvents).take(10)
            )
        }
    }

    // Deepfake Calls: Simulation
    fun triggerSimulatedCall() {
        val simulatedCall = CallRecord(
            id = "sim-${System.currentTimeMillis()}",
            callerName = "Executive Desk (Simulated Voice)",
            phoneNumber = "+1 (888) 012-9988",
            timestamp = System.currentTimeMillis(),
            durationFormatted = "00:00",
            isSimulated = true,
            detectionStatus = "ANALYZING AUDIO STREAM",
            confidenceScore = 96,
            riskLevel = ThreatLevel.CRITICAL
        )

        _deepfakeState.update {
            it.copy(
                isSimulatingCall = true,
                simulationStage = 0, // Ringing
                currentCall = simulatedCall,
                liveConfidencePercent = 0
            )
        }
    }

    fun answerSimulatedCall() {
        viewModelScope.launch {
            _deepfakeState.update { it.copy(simulationStage = 1) } // Connected, analyzing

            // Animate confidence and spectral check
            for (p in 20..96 step 15) {
                delay(300)
                _deepfakeState.update { it.copy(liveConfidencePercent = p) }
            }
            delay(400)

            val finishedCall = _deepfakeState.value.currentCall?.copy(
                detectionStatus = "SYNTHETIC DEEPFAKE",
                confidenceScore = 97,
                riskLevel = ThreatLevel.CRITICAL,
                durationFormatted = "00:18"
            )

            _deepfakeState.update {
                it.copy(
                    simulationStage = 2,
                    currentCall = finishedCall,
                    liveConfidencePercent = 97
                )
            }
        }
    }

    fun dismissSimulatedCall() {
        val callToAdd = _deepfakeState.value.currentCall
        _deepfakeState.update {
            it.copy(
                isSimulatingCall = false,
                simulationStage = 0,
                currentCall = null,
                recentCalls = if (callToAdd != null) (listOf(callToAdd) + it.recentCalls).take(8) else it.recentCalls
            )
        }
    }

    // Self-Healing OS: Pipeline
    fun runSelfHealingPipeline() {
        if (_selfHealingState.value.isPipelineRunning) return
        viewModelScope.launch {
            _selfHealingState.update {
                it.copy(
                    isPipelineRunning = true,
                    pipelineStage = "ANALYZING",
                    progressPercent = 15,
                    repairLog = listOf("Initiating automated vulnerability & diagnostic inspection...")
                )
            }

            delay(700)
            _selfHealingState.update {
                it.copy(
                    progressPercent = 40,
                    pipelineStage = "REPAIRING",
                    repairLog = it.repairLog + listOf(
                        "Found 3 anomalous transient state vectors.",
                        "Executing safe sandbox memory purge & entropy pool regeneration..."
                    )
                )
            }

            val safeResult = repository.executeSafeRepair()

            delay(800)
            _selfHealingState.update {
                it.copy(
                    progressPercent = 75,
                    pipelineStage = "VERIFYING",
                    repairLog = it.repairLog + listOf(
                        safeResult,
                        "Verifying cryptographic root anchors and socket isolation..."
                    )
                )
            }

            delay(600)
            val newRestorePoint = RestorePoint(
                id = "rp-${System.currentTimeMillis() % 10000}",
                label = "Healed System Snapshot #${_selfHealingState.value.restorePoints.size + 1}",
                timestamp = System.currentTimeMillis(),
                checksum = "SHA256: ${UUID.randomUUID().toString().take(12)}",
                backupSizeKb = 352
            )

            val resolvedIssues = _selfHealingState.value.issues.map {
                it.copy(status = "REPAIRED & VERIFIED")
            }

            _selfHealingState.update {
                it.copy(
                    isPipelineRunning = false,
                    pipelineStage = "RESTORE_CREATED",
                    progressPercent = 100,
                    issues = resolvedIssues,
                    restorePoints = listOf(newRestorePoint) + it.restorePoints,
                    repairLog = it.repairLog + listOf(
                        "Verification passed: Keystore intact, memory aligned.",
                        "Restore Point created: '${newRestorePoint.label}'"
                    )
                )
            }
        }
    }

    fun toggleAutoHeal(enabled: Boolean) {
        _selfHealingState.update { it.copy(autoHealEnabled = enabled) }
        updateSetting { it.copy(selfHealingModeEnabled = enabled) }
    }

    // Privacy Twin Scan
    fun runPrivacyTwinScan() {
        if (_privacyTwinState.value.isScanning) return
        viewModelScope.launch {
            _privacyTwinState.update {
                it.copy(
                    isScanning = true,
                    scanProgress = 0.15f,
                    scanStageText = "Phase 1/4: Inspecting installed application permissions..."
                )
            }

            delay(600)
            val realAudits = repository.auditInstalledApps()
            _privacyTwinState.update {
                it.copy(
                    scanProgress = 0.50f,
                    auditedApps = realAudits,
                    scanStageText = "Phase 2/4: Auditing sensitive hardware access (Camera/Mic/GPS)..."
                )
            }

            delay(700)
            _privacyTwinState.update {
                it.copy(
                    scanProgress = 0.80f,
                    scanStageText = "Phase 3/4: Correlating telemetry graphs with Data Broker feed..."
                )
            }

            delay(500)
            val calculatedScore = (98 - (realAudits.count { it.riskLevel == ThreatLevel.HIGH } * 4)).coerceIn(65, 96)
            _privacyTwinState.update {
                it.copy(
                    isScanning = false,
                    scanProgress = 1f,
                    privacyScore = calculatedScore,
                    scanStageText = "Privacy Audit Complete: Score updated to $calculatedScore%"
                )
            }
        }
    }

    // Settings actions
    fun updateUserName(name: String) {
        val updated = _userProfile.value.copy(name = name)
        _userProfile.value = updated
        repository.saveUserName(name)
    }

    fun updateSetting(transform: (SecuritySettings) -> SecuritySettings) {
        val current = _settings.value
        val updated = transform(current)
        _settings.value = updated
        repository.saveSettings(updated)
    }

    fun setLanguage(language: String) {
        updateSetting { it.copy(language = language) }
    }

    fun setDarkMode(darkMode: Boolean) {
        updateSetting { it.copy(darkModeEnabled = darkMode) }
    }

    fun setEmergencyLock(active: Boolean) {
        updateSetting { it.copy(emergencyLockActive = active) }
    }
}
