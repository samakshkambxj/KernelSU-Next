package com.rifsxd.ksunext.ui.screen.susfs

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Destination<RootGraph>
@Composable
fun SuSFSScreen(
    navigator: DestinationsNavigator,
    viewModel: SuSFSViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddPathDialog by remember { mutableStateOf(false) }
    var newPathInput by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("SUSFS") },
                navigationIcon = {
                    IconButton(onClick = { navigator.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(modifier = Modifier.size(48.dp))
                Spacer(modifier = Modifier.height(16.dp))
                Text("Loading SUSFS config...")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item { Spacer(modifier = Modifier.height(4.dp)) }

                item {
                    UnameSection(
                        unameValue = uiState.unameValue,
                        buildTimeValue = uiState.buildTimeValue,
                        onUnameChange = { viewModel.applyUname(it) },
                        onBuildTimeChange = { viewModel.applyBuildTime(it) },
                        onApply = { viewModel.applyAll() }
                    )
                }

                item {
                    TogglesSection(
                        autoStartEnabled = uiState.autoStartEnabled,
                        hideBlEnabled = uiState.enableHideBl,
                        cleanupResidueEnabled = uiState.enableCleanupResidue,
                        avcLogSpoofingEnabled = uiState.enableAvcLogSpoofing,
                        hideSusMountsEnabled = uiState.hideSusMountsForAllProcs,
                        onAutoStartToggle = { viewModel.toggleAutoStart(it) },
                        onHideBlToggle = { viewModel.toggleHideBl(it) },
                        onCleanupResidueToggle = { viewModel.toggleCleanupResidue(it) },
                        onAvcLogSpoofingToggle = { viewModel.toggleAvcLogSpoofing(it) },
                        onHideSusMountsToggle = { viewModel.toggleHideSusMounts(it) }
                    )
                }

                item {
                    SusPathsSection(
                        susPaths = uiState.susPaths,
                        onAddPath = { showAddPathDialog = true },
                        onRemovePath = { viewModel.removeSusPath(it) }
                    )
                }

                item {
                    EnabledFeaturesSection(features = uiState.enabledFeatures)
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }

    if (showAddPathDialog) {
        AddSusPathDialog(
            path = newPathInput,
            onPathChange = { newPathInput = it },
            onDismiss = {
                showAddPathDialog = false
                newPathInput = ""
            },
            onConfirm = {
                viewModel.addSusPath(newPathInput)
                showAddPathDialog = false
                newPathInput = ""
            }
        )
    }
}

@Composable
private fun UnameSection(
    unameValue: String,
    buildTimeValue: String,
    onUnameChange: (String) -> Unit,
    onBuildTimeChange: (String) -> Unit,
    onApply: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Uname Spoofing",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = unameValue,
                onValueChange = onUnameChange,
                label = { Text("Uname value") },
                placeholder = { Text("Android") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = buildTimeValue,
                onValueChange = onBuildTimeChange,
                label = { Text("Build time") },
                placeholder = { Text("Build") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onApply,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Apply All")
            }
        }
    }
}

@Composable
private fun TogglesSection(
    autoStartEnabled: Boolean,
    hideBlEnabled: Boolean,
    cleanupResidueEnabled: Boolean,
    avcLogSpoofingEnabled: Boolean,
    hideSusMountsEnabled: Boolean,
    onAutoStartToggle: (Boolean) -> Unit,
    onHideBlToggle: (Boolean) -> Unit,
    onCleanupResidueToggle: (Boolean) -> Unit,
    onAvcLogSpoofingToggle: (Boolean) -> Unit,
    onHideSusMountsToggle: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Toggles",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))

            ToggleItem(
                title = "Auto Start",
                description = "Automatically start SUSFS on boot",
                checked = autoStartEnabled,
                onCheckedChange = onAutoStartToggle
            )
            ToggleItem(
                title = "Hide KernelSU",
                description = "Hide KernelSU from detection",
                checked = hideBlEnabled,
                onCheckedChange = onHideBlToggle
            )
            ToggleItem(
                title = "Cleanup Residue",
                description = "Clean up KernelSU residue files",
                checked = cleanupResidueEnabled,
                onCheckedChange = onCleanupResidueToggle
            )
            ToggleItem(
                title = "AVC Log Spoofing",
                description = "Spoof SELinux AVC audit logs",
                checked = avcLogSpoofingEnabled,
                onCheckedChange = onAvcLogSpoofingToggle
            )
            ToggleItem(
                title = "Hide SUS Mounts",
                description = "Hide SUS mounts for all processes",
                checked = hideSusMountsEnabled,
                onCheckedChange = onHideSusMountsToggle
            )
        }
    }
}

@Composable
private fun ToggleItem(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
private fun SusPathsSection(
    susPaths: Set<String>,
    onAddPath: () -> Unit,
    onRemovePath: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .animateContentSize()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "SUS Paths",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                IconButton(onClick = onAddPath) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add SUS path"
                    )
                }
            }

            if (susPaths.isEmpty()) {
                Text(
                    text = "No SUS paths configured",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            } else {
                susPaths.forEach { path ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = path,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = { onRemovePath(path) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "Remove path",
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EnabledFeaturesSection(features: List<EnabledFeature>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Enabled Features",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (features.isEmpty()) {
                Text(
                    text = "No features detected",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                features.forEach { feature ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = feature.name,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Surface(
                            color = if (feature.isEnabled)
                                MaterialTheme.colorScheme.primaryContainer
                            else
                                MaterialTheme.colorScheme.errorContainer,
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text(
                                text = feature.statusText,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = if (feature.isEnabled)
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                else
                                    MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AddSusPathDialog(
    path: String,
    onPathChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add SUS Path") },
        text = {
            OutlinedTextField(
                value = path,
                onValueChange = onPathChange,
                label = { Text("Path") },
                placeholder = { Text("/data/local/tmp") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
