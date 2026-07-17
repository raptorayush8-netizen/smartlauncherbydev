package com.smartlauncher.viewmodel

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.smartlauncher.data.AppInfo
import com.smartlauncher.data.AppInfoHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LauncherViewModel @Inject constructor(
    application: Application
) : AndroidViewModel(application) {

    private val _apps = MutableStateFlow<List<AppInfo>>(emptyList())
    val apps: StateFlow<List<AppInfo>> = _apps.asStateFlow()

    private val _filteredApps = MutableStateFlow<List<AppInfo>>(emptyList())
    val filteredApps: StateFlow<List<AppInfo>> = _filteredApps.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _dockApps = MutableStateFlow<List<AppInfo>>(emptyList())
    val dockApps: StateFlow<List<AppInfo>> = _dockApps.asStateFlow()

    private val _categories = MutableStateFlow<Map<String, List<AppInfo>>>(emptyMap())
    val categories: StateFlow<Map<String, List<AppInfo>>> = _categories.asStateFlow()

    private val appChangeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            loadApps()
        }
    }

    init {
        loadApps()
        registerReceiver()
    }

    private fun registerReceiver() {
        val filter = IntentFilter().apply {
            addAction("com.smartlauncher.APP_LIST_CHANGED")
            addAction("com.smartlauncher.BOOT_COMPLETED")
        }
        getApplication<Application>().registerReceiver(appChangeReceiver, filter)
    }

    fun loadApps() {
        viewModelScope.launch {
            val context = getApplication<Application>()
            val installedApps = AppInfoHelper.getInstalledApps(context)
            _apps.value = installedApps
            _filteredApps.value = installedApps
            categorizeApps(installedApps)
            setDefaultDockApps(installedApps)
        }
    }

    private fun setDefaultDockApps(apps: List<AppInfo>) {
        if (_dockApps.value.isEmpty()) {
            val defaultDockPackages = listOf(
                "com.android.dialer",
                "com.android.mms",
                "com.android.chrome",
                "com.android.camera"
            )
            _dockApps.value = defaultDockPackages.mapNotNull { pkg ->
                apps.find { it.packageName == pkg }
            }.take(5)
        }
    }

    private fun categorizeApps(apps: List<AppInfo>) {
        val categoryMap = mutableMapOf<String, MutableList<AppInfo>>()

        apps.forEach { app ->
            val category = categorizeApp(app)
            categoryMap.getOrPut(category) { mutableListOf() }.add(app)
        }

        _categories.value = categoryMap
    }

    private fun categorizeApp(app: AppInfo): String {
        val pkg = app.packageName.lowercase()
        return when {
            pkg.contains("game") || pkg.contains("play") -> "Games"
            pkg.contains("camera") || pkg.contains("photo") || pkg.contains("gallery") -> "Camera"
            pkg.contains("music") || pkg.contains("video") || pkg.contains("media") -> "Media"
            pkg.contains("browser") || pkg.contains("chrome") || pkg.contains("firefox") -> "Browser"
            pkg.contains("mail") || pkg.contains("email") || pkg.contains("message") -> "Communication"
            pkg.contains("map") || pkg.contains("navigation") || pkg.contains("travel") -> "Travel"
            pkg.contains("shop") || pkg.contains("store") || pkg.contains("pay") -> "Shopping"
            pkg.contains("news") || pkg.contains("feed") || pkg.contains("social") -> "Social"
            pkg.contains("work") || pkg.contains("office") || pkg.contains("document") -> "Productivity"
            pkg.contains("health") || pkg.contains("fitness") || pkg.contains("sport") -> "Health"
            pkg.contains("learn") || pkg.contains("education") || pkg.contains("school") -> "Education"
            pkg.contains("bank") || pkg.contains("finance") || pkg.contains("wallet") -> "Finance"
            app.isSystemApp -> "System"
            else -> "Other"
        }
    }

    fun searchApps(query: String) {
        _searchQuery.value = query
        if (query.isBlank()) {
            _filteredApps.value = _apps.value
        } else {
            _filteredApps.value = _apps.value.filter {
                it.label.contains(query, ignoreCase = true) ||
                        it.packageName.contains(query, ignoreCase = true)
            }
        }
    }

    fun addToDock(app: AppInfo) {
        if (_dockApps.value.size < 5 && _dockApps.value.none { it.packageName == app.packageName }) {
            _dockApps.value = _dockApps.value + app
        }
    }

    fun removeFromDock(app: AppInfo) {
        _dockApps.value = _dockApps.value.filter { it.packageName != app.packageName }
    }

    override fun onCleared() {
        super.onCleared()
        try {
            getApplication<Application>().unregisterReceiver(appChangeReceiver)
        } catch (e: Exception) {
            // Receiver not registered
        }
    }
}
