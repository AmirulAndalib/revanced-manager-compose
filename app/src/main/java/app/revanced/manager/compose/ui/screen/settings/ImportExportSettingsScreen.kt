package app.revanced.manager.compose.ui.screen.settings

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import app.revanced.manager.compose.R
import app.revanced.manager.compose.domain.manager.KeystoreManager
import app.revanced.manager.compose.domain.manager.PreferencesManager
import app.revanced.manager.compose.ui.component.AppTopBar
import app.revanced.manager.compose.ui.component.FileSelector
import app.revanced.manager.compose.ui.component.GroupHeader
import app.revanced.manager.compose.ui.viewmodel.ImportExportViewModel
import org.koin.androidx.compose.getViewModel
import org.koin.compose.rememberKoinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportExportSettingsScreen(
    onBackClick: () -> Unit,
    vm: ImportExportViewModel = getViewModel()
) {
    var showImportKeystoreDialog by rememberSaveable { mutableStateOf(false) }
    var showExportKeystoreDialog by rememberSaveable { mutableStateOf(false) }

    if (showImportKeystoreDialog) {
        ImportKeystoreDialog(
            onDismissRequest = { showImportKeystoreDialog = false },
            onImport = vm::import
        )
    }
    if (showExportKeystoreDialog) {
        ExportKeystoreDialog(
            onDismissRequest = { showExportKeystoreDialog = false },
            onExport = vm::export
        )
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = stringResource(R.string.import_export),
                onBackClick = onBackClick
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            GroupHeader(stringResource(R.string.signing))
            GroupItem(
                onClick = {
                    showImportKeystoreDialog = true
                },
                headline = R.string.import_keystore,
                description = R.string.import_keystore_descripion
            )
            GroupItem(
                onClick = {
                    showExportKeystoreDialog = true
                },
                headline = R.string.export_keystore,
                description = R.string.export_keystore_description
            )
            GroupItem(
                onClick = vm::regenerate,
                headline = R.string.regenerate_keystore,
                description = R.string.regenerate_keystore_description
            )
        }
    }
}

@Composable
private fun GroupItem(onClick: () -> Unit, @StringRes headline: Int, @StringRes description: Int) = ListItem(
    modifier = Modifier.clickable { onClick() },
    headlineContent = { Text(stringResource(headline)) },
    supportingContent = { Text(stringResource(description)) }
)

@Composable
fun ExportKeystoreDialog(
    onDismissRequest: () -> Unit,
    onExport: (Uri) -> Unit
) {
    val activityLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("*/*")) { uri ->
        uri?.let {
            onExport(it)
            onDismissRequest()
        }
    }
    val prefs: PreferencesManager = rememberKoinInject()

    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            Button(
                onClick = { activityLauncher.launch("Manager.keystore") }
            ) {
                Text(stringResource(R.string.select_file))
            }
        },
        title = { Text(stringResource(R.string.export_keystore)) },
        text = {
            Column {
                Text("Current common name: ${prefs.keystoreCommonName}")
                Text("Current password: ${prefs.keystorePass}")
            }
        }
    )
}

@Composable
fun ImportKeystoreDialog(
    onDismissRequest: () -> Unit, onImport: (Uri, String, String) -> Unit
) {
    var cn by rememberSaveable { mutableStateOf(KeystoreManager.defaultKeystoreValue) }
    var pass by rememberSaveable { mutableStateOf(KeystoreManager.defaultKeystoreValue) }
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            FileSelector("*/*", onSelect = {
                onImport(it, cn, pass)
                onDismissRequest()
            }) {
                Text(stringResource(R.string.select_file))
            }
        },
        title = { Text(stringResource(R.string.import_keystore)) },
        text = {
            Column {
                TextField(
                    value = cn,
                    onValueChange = { cn = it },
                    label = { Text("Common Name") }
                )
                TextField(
                    value = pass,
                    onValueChange = { pass = it },
                    label = { Text("Password") }
                )

                Text("Credential presets")

                Button(
                    onClick = {
                        cn = KeystoreManager.defaultKeystoreValue
                        pass = KeystoreManager.defaultKeystoreValue
                    }
                ) {
                    Text(stringResource(R.string.import_keystore_preset_default))
                }
                Button(
                    onClick = {
                        cn = KeystoreManager.defaultKeystoreValue
                        pass = "s3cur3p@ssw0rd"
                    }
                ) {
                    Text(stringResource(R.string.import_keystore_preset_flutter))
                }
            }
        }
    )
}