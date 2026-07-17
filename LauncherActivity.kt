package com.smartlauncher.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.smartlauncher.ui.components.*
import com.smartlauncher.ui.theme.SmartLauncherTheme
import com.smartlauncher.viewmodel.LauncherViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LauncherActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            SmartLauncherTheme {
                LauncherScreen(
                    onOpenSettings = {
                        startActivity(Intent(this, MainActivity::class.java))
                    }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }
}

@Composable
fun LauncherScreen(
    onOpenSettings: () -> Unit,
    viewModel: LauncherViewModel = hiltViewModel()
) {
    val apps by viewModel.apps.collectAsState()
    val filteredApps by viewModel.filteredApps.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val dockApps by viewModel.dockApps.collectAsState()
    val categories by viewModel.categories.collectAsState()

    var showDrawer by remember { mutableStateOf(false) }
    var showCategories by remember { mutableStateOf(false) }
    var showWallpaperPicker by remember { mutableStateOf(false) }
    var longPressTarget by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectVerticalDragGestures { _, dragAmount ->
                        if (dragAmount < -100) {
                            showDrawer = true
                        }
                    }
                }
        ) {
            // Widgets area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Top
                ) {
                    // Clock Widget
                    ClockWidget()

                    Spacer(modifier = Modifier.height(8.dp))

                    // Battery & Weather row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        BatteryWidget(modifier = Modifier.weight(1f))
                        WeatherWidget(modifier = Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Quick search bar
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showDrawer = true },
                        shape = RoundedCornerShape(25.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = "Search",
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Search apps...",
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }

            // Dock bar
            DockBar(
                dockApps = dockApps,
                onAppClick = { app -> app.launch(context) }
            )
        }

        // App Drawer overlay
        AnimatedVisibility(
            visible = showDrawer,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it })
        ) {
            AppDrawer(
                apps = filteredApps,
                searchQuery = searchQuery,
                onSearch = { viewModel.searchApps(it) },
                onAppClick = { app ->
                    app.launch(context)
                    showDrawer = false
                },
                onDismiss = { showDrawer = false }
            )
        }

        // Categories overlay
        AnimatedVisibility(
            visible = showCategories,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it })
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.95f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Categories",
                            fontSize = 24.sp,
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Text(
                            text = "✕",
                            fontSize = 24.sp,
                            modifier = Modifier.clickable { showCategories = false }
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    CategoryScreen(
                        categories = categories,
                        onAppClick = { app ->
                            app.launch(context)
                            showCategories = false
                        }
                    )
                }
            }
        }

        // Wallpaper picker dialog
        if (showWallpaperPicker) {
            WallpaperPicker(
                onWallpaperSelected = { uri ->
                    com.smartlauncher.ui.components.setWallpaper(context, uri)
                    showWallpaperPicker = false
                }
            )
        }

        // Settings FAB
        FloatingActionButton(
            onClick = onOpenSettings,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .statusBarsPadding(),
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ) {
            Text("⚙️", fontSize = 20.sp)
        }

        // Categories FAB
        FloatingActionButton(
            onClick = { showCategories = true },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .statusBarsPadding(),
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ) {
            Text("📁", fontSize = 20.sp)
        }
    }
}
