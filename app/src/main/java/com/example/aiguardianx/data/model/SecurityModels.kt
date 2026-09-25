package com.example.aiguardianx.data.model

data class DeviceHealthInfo(
    val healthPercentage: Int = 94,
    val osVersion: String = "Android 14 (API 34)",
    val securityPatch: String = "2024-08-01",
    val batteryLevel: Int = 85,
    val memoryUsagePercent: Int = 42,
    val storageUsagePercent: Int = 58,
    val isEncrypted: Boolean = true,
    val isRooted: Boolean = false,
    val protectedAppsCount: Int = 18,
    val statusMessage: String = "System integrity intact. Hardware keystore operational."
)

enum class ThreatLevel {
    LOW, MEDIUM, HIGH, CRITICAL
}

data class ThreatMonitoringInfo(
    val level: ThreatLevel = ThreatLevel.LOW,
    val activeThreatsCount: Int = 0,
    val blockedAttacksLast24h: Int = 3,
    val networkSecurity: String = "TLS 1.3 Strict / No MITM",
    val suspiciousAppsCount: Int = 0,
    val sandboxIntegrity: String = "Secured"
)

data class PrivacyScoreInfo(
    val score: Int = 86,
    val sensitivePermissionsGranted: Int = 4,
    val dataBrokersMonitored: Int = 12,
    val trackingTrackersBlocked: Int = 27
)

data class BehaviourEvent(
    val id: String,
    val timestamp: Long,
    val type: String,
    val anomalyScore: Int,
    val details: String
)

data class TypingMetrics(
    val currentIntervalMs: Long = 180,
    val avgIntervalMs: Long = 195,
    val speedWpm: Int = 48,
    val timingVariationMs: Long = 22,
    val normalPatternLabel: String = "Fluid 185-210ms (Rhythmic)",
    val currentPatternLabel: String = "192ms (Match: 95%)",
    val keystrokeCount: Int = 14
)

data class SwipeMetrics(
    val direction: String = "Left to Right",
    val distancePx: Float = 340f,
    val durationMs: Long = 210,
    val speedPxPerSec: Float = 1619f,
    val startX: Float = 120f,
    val startY: Float = 480f,
    val endX: Float = 460f,
    val endY: Float = 475f
)

data class PressureMetrics(
    val pressure: Float = 0.65f,
    val touchSize: Float = 0.12f,
    val isSupported: Boolean = true
)

data class CallRecord(
    val id: String,
    val callerName: String,
    val phoneNumber: String,
    val timestamp: Long,
    val durationFormatted: String,
    val isSimulated: Boolean,
    val detectionStatus: String, // "SAFE", "SUSPICIOUS", "SYNTHETIC DEEPFAKE"
    val confidenceScore: Int, // e.g. 98%
    val riskLevel: ThreatLevel
)

data class SystemIssue(
    val id: String,
    val title: String,
    val severity: ThreatLevel,
    val category: String,
    val description: String,
    val recommendedAction: String,
    val status: String // "DETECTED", "ANALYZED", "REPAIRED", "VERIFIED"
)

data class RestorePoint(
    val id: String,
    val label: String,
    val timestamp: Long,
    val checksum: String,
    val backupSizeKb: Int
)

data class BrokerExposure(
    val id: String,
    val brokerName: String,
    val dataExposed: String,
    val dateReported: String,
    val threatLevel: ThreatLevel,
    val breachSource: String,
    val optOutStatus: String,
    val isDemo: Boolean = true
)

data class AppPermissionAudit(
    val appName: String,
    val packageName: String,
    val isSystemApp: Boolean,
    val sensitivePermissions: List<String>,
    val riskLevel: ThreatLevel
)

data class UserProfile(
    val name: String = "Alex Vance",
    val email: String = "alex.vance@aiguardianx.net",
    val avatarInitials: String = "AV"
)

data class SecuritySettings(
    val faceAuthEnabled: Boolean = true,
    val fingerprintAuthEnabled: Boolean = true,
    val suspiciousCallProtectionEnabled: Boolean = true,
    val appThreatDetectionEnabled: Boolean = true,
    val selfHealingModeEnabled: Boolean = true,
    val securityAlertEnabled: Boolean = true,
    val notificationsEnabled: Boolean = true,
    val language: String = "English", // "English", "Tamil", "Hindi"
    val darkModeEnabled: Boolean = true,
    val emergencyLockActive: Boolean = false
)
