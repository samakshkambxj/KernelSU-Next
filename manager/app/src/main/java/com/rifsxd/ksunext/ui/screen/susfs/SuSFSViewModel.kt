package com.rifsxd.ksunext.ui.screen.susfs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.topjohnwu.superuser.Shell
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SuSFSViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SuSFSUiState())
    val uiState: StateFlow<SuSFSUiState> = _uiState.asStateFlow()

    init {
        loadConfig()
    }

    private fun getKsuDaemonPath(): String = "/data/adb/ksu/bin/ksud"

    private fun getRootShell(): Shell = Shell.Builder.create()
        .shellBuilder()
        .build()

    private suspend fun executeShellCommand(command: String): String {
        return withContext(Dispatchers.IO) {
            try {
                val result = getRootShell().exec(command)
                result.out.joinToString("\n")
            } catch (e: Exception) {
                "error: ${e.message}"
            }
        }
    }

    fun loadConfig() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val daemonPath = getKsuDaemonPath()

            val unameResult = executeShellCommand("$daemonPath susfs get-uname")
            val susPathsResult = executeShellCommand("$daemonPath susfs get-sus-path")
            val avcResult = executeShellCommand("$daemonPath susfs get-avc-log-spoofing-status")
            val hideBlResult = executeShellCommand("$daemonPath susfs get-hide-kernelsu")
            val susMountResult = executeShellCommand("$daemonPath susfs get-sus-mount-for-all-procs")

            val unameValue = unameResult.trim().ifBlank { "Android" }
            val susPaths = parseSusPaths(susPathsResult)
            val avcEnabled = parseBoolStatus(avcResult)
            val hideBlEnabled = parseBoolStatus(hideBlResult)
            val susMountEnabled = parseBoolStatus(susMountResult)

            val features = buildFeatureList(
                avcEnabled = avcEnabled,
                hideBlEnabled = hideBlEnabled,
                cleanupResidue = false,
                autoStart = false
            )

            _uiState.update {
                it.copy(
                    unameValue = unameValue,
                    susPaths = susPaths,
                    enableAvcLogSpoofing = avcEnabled,
                    enableHideBl = hideBlEnabled,
                    hideSusMountsForAllProcs = susMountEnabled,
                    enabledFeatures = features,
                    isLoading = false
                )
            }
        }
    }

    fun applyUname(value: String) {
        if (value.isBlank()) return
        viewModelScope.launch {
            val daemonPath = getKsuDaemonPath()
            executeShellCommand("$daemonPath susfs set-uname '$value' 'Build'")
            _uiState.update { it.copy(unameValue = value) }
        }
    }

    fun applyBuildTime(value: String) {
        if (value.isBlank()) return
        viewModelScope.launch {
            val daemonPath = getKsuDaemonPath()
            executeShellCommand("$daemonPath susfs set-uname '${_uiState.value.unameValue}' '$value'")
            _uiState.update { it.copy(buildTimeValue = value) }
        }
    }

    fun toggleAvcLogSpoofing(enabled: Boolean) {
        viewModelScope.launch {
            val daemonPath = getKsuDaemonPath()
            val flag = if (enabled) "1" else "0"
            executeShellCommand("$daemonPath susfs enable-avc-log-spoofing $flag")
            _uiState.update {
                it.copy(
                    enableAvcLogSpoofing = enabled,
                    enabledFeatures = buildFeatureList(
                        avcEnabled = enabled,
                        hideBlEnabled = it.enableHideBl,
                        cleanupResidue = it.enableCleanupResidue,
                        autoStart = it.autoStartEnabled
                    )
                )
            }
        }
    }

    fun toggleHideBl(enabled: Boolean) {
        viewModelScope.launch {
            val daemonPath = getKsuDaemonPath()
            val flag = if (enabled) "1" else "0"
            executeShellCommand("$daemonPath susfs enable-hide-kernelsu $flag")
            _uiState.update {
                it.copy(
                    enableHideBl = enabled,
                    enabledFeatures = buildFeatureList(
                        avcEnabled = it.enableAvcLogSpoofing,
                        hideBlEnabled = enabled,
                        cleanupResidue = it.enableCleanupResidue,
                        autoStart = it.autoStartEnabled
                    )
                )
            }
        }
    }

    fun toggleCleanupResidue(enabled: Boolean) {
        viewModelScope.launch {
            val daemonPath = getKsuDaemonPath()
            val flag = if (enabled) "1" else "0"
            executeShellCommand("$daemonPath susfs enable-cleanup-residue $flag")
            _uiState.update {
                it.copy(
                    enableCleanupResidue = enabled,
                    enabledFeatures = buildFeatureList(
                        avcEnabled = it.enableAvcLogSpoofing,
                        hideBlEnabled = it.enableHideBl,
                        cleanupResidue = enabled,
                        autoStart = it.autoStartEnabled
                    )
                )
            }
        }
    }

    fun toggleAutoStart(enabled: Boolean) {
        viewModelScope.launch {
            val daemonPath = getKsuDaemonPath()
            val flag = if (enabled) "1" else "0"
            executeShellCommand("$daemonPath susfs enable-auto-start $flag")
            _uiState.update {
                it.copy(
                    autoStartEnabled = enabled,
                    enabledFeatures = buildFeatureList(
                        avcEnabled = it.enableAvcLogSpoofing,
                        hideBlEnabled = it.enableHideBl,
                        cleanupResidue = it.enableCleanupResidue,
                        autoStart = enabled
                    )
                )
            }
        }
    }

    fun toggleHideSusMounts(enabled: Boolean) {
        viewModelScope.launch {
            val daemonPath = getKsuDaemonPath()
            val flag = if (enabled) "1" else "0"
            executeShellCommand("$daemonPath susfs enable-sus-mount-for-all-procs $flag")
            _uiState.update { it.copy(hideSusMountsForAllProcs = enabled) }
        }
    }

    fun addSusPath(path: String) {
        if (path.isBlank()) return
        viewModelScope.launch {
            val daemonPath = getKsuDaemonPath()
            executeShellCommand("$daemonPath susfs add-sus-path '$path'")
            _uiState.update {
                it.copy(susPaths = it.susPaths + path)
            }
        }
    }

    fun removeSusPath(path: String) {
        viewModelScope.launch {
            val daemonPath = getKsuDaemonPath()
            executeShellCommand("$daemonPath susfs del-sus-path '$path'")
            _uiState.update {
                it.copy(susPaths = it.susPaths - path)
            }
        }
    }

    fun applyAll() {
        viewModelScope.launch {
            val state = _uiState.value
            val daemonPath = getKsuDaemonPath()

            if (state.unameValue.isNotBlank()) {
                executeShellCommand("$daemonPath susfs set-uname '${state.unameValue}' '${state.buildTimeValue.ifBlank { "Build" }}'")
            }

            val commands = mutableListOf<String>()
            commands.add("$daemonPath susfs enable-avc-log-spoofing ${if (state.enableAvcLogSpoofing) "1" else "0"}")
            commands.add("$daemonPath susfs enable-hide-kernelsu ${if (state.enableHideBl) "1" else "0"}")
            commands.add("$daemonPath susfs enable-cleanup-residue ${if (state.enableCleanupResidue) "1" else "0"}")
            commands.add("$daemonPath susfs enable-auto-start ${if (state.autoStartEnabled) "1" else "0"}")
            commands.add("$daemonPath susfs enable-sus-mount-for-all-procs ${if (state.hideSusMountsForAllProcs) "1" else "0"}")

            for (cmd in commands) {
                executeShellCommand(cmd)
            }
        }
    }

    private fun parseSusPaths(output: String): Set<String> {
        return output.lines()
            .map { it.trim() }
            .filter { it.isNotEmpty() && !it.startsWith("error") }
            .toSet()
    }

    private fun parseBoolStatus(output: String): Boolean {
        val trimmed = output.trim()
        return trimmed == "1" || trimmed.equals("true", ignoreCase = true)
    }

    private fun buildFeatureList(
        avcEnabled: Boolean,
        hideBlEnabled: Boolean,
        cleanupResidue: Boolean,
        autoStart: Boolean
    ): List<EnabledFeature> {
        return listOf(
            EnabledFeature(
                name = "AVC Log Spoofing",
                isEnabled = avcEnabled,
                statusText = if (avcEnabled) "Active" else "Inactive"
            ),
            EnabledFeature(
                name = "Hide KernelSU",
                isEnabled = hideBlEnabled,
                statusText = if (hideBlEnabled) "Active" else "Inactive"
            ),
            EnabledFeature(
                name = "Cleanup Residue",
                isEnabled = cleanupResidue,
                statusText = if (cleanupResidue) "Active" else "Inactive"
            ),
            EnabledFeature(
                name = "Auto Start",
                isEnabled = autoStart,
                statusText = if (autoStart) "Active" else "Inactive"
            )
        )
    }
}
