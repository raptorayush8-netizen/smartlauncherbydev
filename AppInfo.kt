package com.smartlauncher.data

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.drawable.Drawable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class AppInfo(
    val packageName: String,
    val activityName: String,
    val label: String,
    val icon: Drawable?,
    val isSystemApp: Boolean = false
) {
    val launchIntent: Intent
        get() = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
            component = ComponentName(packageName, activityName)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
        }

    fun launch(context: Context) {
        context.startActivity(launchIntent)
    }
}

object AppInfoHelper {

    suspend fun getInstalledApps(context: Context): List<AppInfo> = withContext(Dispatchers.IO) {
        val packageManager = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val resolveInfos: List<ResolveInfo> = packageManager.queryIntentActivities(intent, PackageManager.MATCH_ALL)

        resolveInfos.mapNotNull { resolveInfo ->
            val packageName = resolveInfo.activityInfo.packageName
            val activityName = resolveInfo.activityInfo.name
            val label = resolveInfo.loadLabel(packageManager).toString()
            val icon = try {
                resolveInfo.loadIcon(packageManager)
            } catch (e: Exception) {
                null
            }
            val isSystemApp = (resolveInfo.activityInfo.applicationInfo.flags and
                    android.content.pm.ApplicationInfo.FLAG_SYSTEM) != 0

            if (packageName != context.packageName) {
                AppInfo(
                    packageName = packageName,
                    activityName = activityName,
                    label = label,
                    icon = icon,
                    isSystemApp = isSystemApp
                )
            } else null
        }.sortedBy { it.label.lowercase() }
    }
}
