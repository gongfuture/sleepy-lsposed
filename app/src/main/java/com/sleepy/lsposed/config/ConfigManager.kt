package com.sleepy.lsposed.config

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

/**
 * Manages app configuration using SharedPreferences
 */
class ConfigManager(private val context: Context) {
    private val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    companion object {
        private const val KEY_API_URL = "api_url"
        private const val KEY_SECRET = "secret"
        private const val KEY_DEVICE_ID = "device_id"
        private const val KEY_DEVICE_NAME = "device_name"
        private const val KEY_CHECK_INTERVAL = "check_interval"
        private const val KEY_BYPASS_SAME = "bypass_same"
        private const val KEY_BATTERY_INFO = "battery_info"
        private const val KEY_MEDIA_ENABLED = "media_enabled"
        private const val KEY_MEDIA_MODE = "media_mode"
        private const val KEY_MEDIA_DEVICE_ID = "media_device_id"
        private const val KEY_MEDIA_DEVICE_NAME = "media_device_name"

        private const val DEFAULT_API_URL = "https://api.example.com/device/set"
        private const val DEFAULT_CHECK_INTERVAL = 3000L
    }

    var apiUrl: String
        get() = prefs.getString(KEY_API_URL, DEFAULT_API_URL) ?: DEFAULT_API_URL
        set(value) = prefs.edit().putString(KEY_API_URL, value).apply()

    var secret: String
        get() = prefs.getString(KEY_SECRET, "") ?: ""
        set(value) = prefs.edit().putString(KEY_SECRET, value).apply()

    var deviceId: String
        get() = prefs.getString(KEY_DEVICE_ID, "") ?: ""
        set(value) = prefs.edit().putString(KEY_DEVICE_ID, value).apply()

    var deviceName: String
        get() = prefs.getString(KEY_DEVICE_NAME, "") ?: ""
        set(value) = prefs.edit().putString(KEY_DEVICE_NAME, value).apply()

    var checkInterval: Long
        get() = prefs.getString(KEY_CHECK_INTERVAL, DEFAULT_CHECK_INTERVAL.toString())?.toLongOrNull()
            ?: DEFAULT_CHECK_INTERVAL
        set(value) = prefs.edit().putString(KEY_CHECK_INTERVAL, value.toString()).apply()

    var bypassSameRequest: Boolean
        get() = prefs.getBoolean(KEY_BYPASS_SAME, true)
        set(value) = prefs.edit().putBoolean(KEY_BYPASS_SAME, value).apply()

    var batteryInfoEnabled: Boolean
        get() = prefs.getBoolean(KEY_BATTERY_INFO, true)
        set(value) = prefs.edit().putBoolean(KEY_BATTERY_INFO, value).apply()

    var mediaEnabled: Boolean
        get() = prefs.getBoolean(KEY_MEDIA_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_MEDIA_ENABLED, value).apply()

    var mediaMode: MediaMode
        get() = MediaMode.fromString(prefs.getString(KEY_MEDIA_MODE, "combined") ?: "combined")
        set(value) = prefs.edit().putString(KEY_MEDIA_MODE, value.value).apply()

    var mediaDeviceId: String
        get() = prefs.getString(KEY_MEDIA_DEVICE_ID, "") ?: ""
        set(value) = prefs.edit().putString(KEY_MEDIA_DEVICE_ID, value).apply()

    var mediaDeviceName: String
        get() = prefs.getString(KEY_MEDIA_DEVICE_NAME, "") ?: ""
        set(value) = prefs.edit().putString(KEY_MEDIA_DEVICE_NAME, value).apply()

    /**
     * Check if the configuration is valid and complete
     */
    fun isConfigValid(): Boolean {
        return apiUrl.isNotBlank() &&
                secret.isNotBlank() &&
                deviceId.isNotBlank() &&
                deviceName.isNotBlank()
    }
}

enum class MediaMode(val value: String) {
    COMBINED("combined"),
    STANDALONE("standalone");

    companion object {
        fun fromString(value: String): MediaMode {
            return values().find { it.value == value } ?: COMBINED
        }
    }
}
