package com.rifsxd.ksunext.ui.screen.susfs

data class EnabledFeature(
    val name: String,
    val isEnabled: Boolean,
    val statusText: String
)

data class ModuleConfig(
    val packageName: String = "com.rifsxd.ksunext",
    val modulesPath: String = "/data/adb/modules",
    val overlayPath: String = "/data/adb/modules/ksu"
)

data class SuSFSUiState(
    val unameValue: String = "",
    val buildTimeValue: String = "",
    val autoStartEnabled: Boolean = false,
    val hideSusMountsForAllProcs: Boolean = false,
    val enableAvcLogSpoofing: Boolean = false,
    val enableHideBl: Boolean = false,
    val enableCleanupResidue: Boolean = false,
    val susPaths: Set<String> = emptySet(),
    val enabledFeatures: List<EnabledFeature> = emptyList(),
    val isLoading: Boolean = false
)
