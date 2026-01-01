package com.sleepy.lsposed

import android.util.Log

/**
 * Logging utility for Xposed module
 */
object XposedLogger {
    private const val TAG = "SleepyLsposed"

    fun i(message: String) {
        Log.i(TAG, message)
    }

    fun e(message: String, throwable: Throwable? = null) {
        if (throwable != null) {
            Log.e(TAG, message, throwable)
        } else {
            Log.e(TAG, message)
        }
    }

    fun d(message: String) {
        Log.d(TAG, message)
    }

    fun w(message: String) {
        Log.w(TAG, message)
    }
}
