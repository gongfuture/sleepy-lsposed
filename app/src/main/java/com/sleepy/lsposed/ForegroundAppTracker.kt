package com.sleepy.lsposed

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager

/**
 * Tracks foreground app changes and notifies the service
 */
object ForegroundAppTracker {
    private var lastPackageName: String? = null

    fun updateForegroundApp(context: Context, packageName: String) {
        if (packageName == lastPackageName) {
            return
        }
        
        lastPackageName = packageName
        
        // Get app name from package name
        val appName = getAppName(context, packageName)
        
        XposedLogger.d("Sending broadcast: $appName ($packageName)")
        
        // Notify the service
        val intent = Intent("com.sleepy.lsposed.FOREGROUND_APP_CHANGED").apply {
            setPackage("com.sleepy.lsposed")
            putExtra("package_name", packageName)
            putExtra("app_name", appName)
        }
        
        try {
            context.sendBroadcast(intent)
            XposedLogger.d("Broadcast sent successfully")
        } catch (e: Exception) {
            XposedLogger.e("Failed to send broadcast", e)
        }
    }

    private fun getAppName(context: Context, packageName: String): String {
        return try {
            val pm = context.packageManager
            val appInfo = pm.getApplicationInfo(packageName, 0)
            pm.getApplicationLabel(appInfo).toString()
        } catch (e: PackageManager.NameNotFoundException) {
            packageName
        }
    }
}
