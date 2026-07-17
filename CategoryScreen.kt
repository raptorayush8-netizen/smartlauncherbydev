package com.smartlauncher.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartlauncher.data.AppInfo

@Composable
fun CategoryScreen(
    categories: Map<String, List<AppInfo>>,
    onAppClick: (AppInfo) -> Unit,
    modifier: Modifier = Modifier
) {
    var expandedCategory by remember { mutableStateOf<String?>(null) }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categories.entries.toList()) { (category, apps) ->
            CategoryCard(
                category = category,
                apps = apps,
                isExpanded = expandedCategory == category,
                onExpand = {
                    expandedCategory = if (expandedCategory == category) null else category
                },
                onAppClick = onAppClick
            )
        }
    }
}

@Composable
fun CategoryCard(
    category: String,
    apps: List<AppInfo>,
    isExpanded: Boolean,
    onExpand: () -> Unit,
    onAppClick: (AppInfo) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onExpand),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = category,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "${apps.size}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            if (isExpanded) {
                Spacer(modifier = Modifier.height(8.dp))
                apps.take(8).forEach { app ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onAppClick(app) }
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AppIcon(
                            icon = app.icon,
                            label = app.label,
                            size = 32
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = app.label,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy((-8).dp)
                ) {
                    apps.take(4).forEach { app ->
                        AppIcon(
                            icon = app.icon,
                            label = app.label,
                            size = 36
                        )
                    }
                }
            }
        }
    }
}
