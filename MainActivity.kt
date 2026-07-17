package com.smartlauncher.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.smartlauncher.ui.theme.SmartLauncherTheme
import com.smartlauncher.viewmodel.LauncherViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SmartLauncherTheme {
                SettingsScreen()
            }
        }
    }
}

@Composable
fun SettingsScreen(
    viewModel: LauncherViewModel = hiltViewModel()
) {
    var showHiddenApps by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = "SmartLauncher Settings",
                fontSize = 28.sp,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                onClick = { /* Set as default launcher */ }
            ) {
                ListItem(
                    headlineContent = { Text("Set as Default Launcher") },
                    supportingContent = { Text("Make SmartLauncher your home screen") },
                    leadingContent = { Text("🏠", fontSize = 24.sp) }
                )
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                onClick = { showHiddenApps = true }
            ) {
                ListItem(
                    headlineContent = { Text("Hidden Apps") },
                    supportingContent = { Text("Manage hidden applications") },
                    leadingContent = { Text("👁️", fontSize = 24.sp) }
                )
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                onClick = { /* Wallpaper settings */ }
            ) {
                ListItem(
                    headlineContent = { Text("Wallpaper") },
                    supportingContent = { Text("Change home screen wallpaper") },
                    leadingContent = { Text("🎨", fontSize = 24.sp) }
                )
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                onClick = { /* App categories */ }
            ) {
                ListItem(
                    headlineContent = { Text("App Categories") },
                    supportingContent = { Text("Organize apps into folders") },
                    leadingContent = { Text("📁", fontSize = 24.sp) }
                )
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                onClick = { /* About */ }
            ) {
                ListItem(
                    headlineContent = { Text("About") },
                    supportingContent = { Text("SmartLauncher v1.0") },
                    leadingContent = { Text("ℹ️", fontSize = 24.sp) }
                )
            }
        }
    }
}
