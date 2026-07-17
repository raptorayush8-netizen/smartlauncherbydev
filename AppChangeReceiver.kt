package com.smartlauncher.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AppChangeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        when (action) {
            Intent.ACTION_PACKAGE_ADDED,
            Intent.ACTION_PACKAGE_REMOVED,
            Intent.ACTION_PACKAGE_REPLACED,
            Intent.ACTION_PACKAGE_CHANGED -> {
                val packageName = intent.data?.schemeSpecificPart
                val broadcastIntent = Intent("com.smartlauncher.APP_LIST_CHANGED").apply {
                    putExtra("packageName", packageName)
                    putExtra("action", action)
                    setPackage(context.packageName)
                }
                context.sendBroadcast(broadcastIntent)
            }
        }
    }
}
