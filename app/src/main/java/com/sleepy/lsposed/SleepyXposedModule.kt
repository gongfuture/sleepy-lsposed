package com.sleepy.lsposed

import android.app.AndroidAppHelper
import android.content.Context
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

/**
 * Main Xposed module entry point
 * This hooks into the Android system to monitor app usage
 */
class SleepyXposedModule : IXposedHookLoadPackage {

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        // Only hook into system processes
        if (lpparam.packageName != "android") {
            return
        }

        try {
            hookActivityManagerService(lpparam)
        } catch (e: Throwable) {
            XposedLogger.e("Failed to hook ActivityManagerService", e)
        }
    }

    private fun hookActivityManagerService(lpparam: XC_LoadPackage.LoadPackageParam) {
        // Hook the method that handles activity resume to track foreground app changes
        val activityRecordClass = XposedHelpers.findClass(
            "com.android.server.wm.ActivityRecord",
            lpparam.classLoader
        )

        XposedHelpers.findAndHookMethod(
            activityRecordClass,
            "setState",
            Int::class.javaPrimitiveType,
            String::class.java,
            object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    try {
                        val state = param.args[0] as Int
                        // State 2 = RESUMED (activity is in foreground)
                        if (state == 2) {
                            val activityRecord = param.thisObject
                            
                            // Get package name
                            val packageName = XposedHelpers.getObjectField(
                                activityRecord, 
                                "packageName"
                            ) as? String
                            
                            if (packageName != null) {
                                // Notify our service about the foreground app change
                                notifyForegroundAppChanged(packageName)
                            }
                        }
                    } catch (e: Throwable) {
                        XposedLogger.e("Error in setState hook", e)
                    }
                }
            }
        )

        XposedLogger.i("Successfully hooked ActivityManagerService")
    }

    private fun notifyForegroundAppChanged(packageName: String) {
        try {
            val context = AndroidAppHelper.currentApplication() as? Context
            if (context != null) {
                ForegroundAppTracker.updateForegroundApp(context, packageName)
            }
        } catch (e: Throwable) {
            XposedLogger.e("Failed to notify foreground app change", e)
        }
    }
}
