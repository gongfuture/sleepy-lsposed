package com.sleepy.lsposed.service

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import com.sleepy.lsposed.R
import com.sleepy.lsposed.config.ConfigManager
import com.sleepy.lsposed.config.MediaMode
import com.sleepy.lsposed.data.DeviceStatus
import com.sleepy.lsposed.network.SleepyApiClient
import com.sleepy.lsposed.utils.BatteryUtil
import com.sleepy.lsposed.utils.MediaMonitor
import kotlinx.coroutines.*

/**
 * Background service that monitors device status and sends updates to server
 */
class SleepyMonitorService : Service() {
    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private lateinit var configManager: ConfigManager
    private lateinit var apiClient: SleepyApiClient
    private lateinit var mediaMonitor: MediaMonitor
    private var monitorJob: Job? = null

    private var currentAppName: String = ""
    private var currentPackageName: String = ""
    private var lastSentStatus: String = ""
    private var lastMediaStatus: String = ""

    private val foregroundAppReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == ACTION_FOREGROUND_APP_CHANGED) {
                currentPackageName = intent.getStringExtra("package_name") ?: ""
                currentAppName = intent.getStringExtra("app_name") ?: ""
            }
        }
    }

    companion object {
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "sleepy_monitor_channel"
        const val ACTION_FOREGROUND_APP_CHANGED = "com.sleepy.lsposed.FOREGROUND_APP_CHANGED"
    }

    override fun onCreate() {
        super.onCreate()
        configManager = ConfigManager(this)
        apiClient = SleepyApiClient()
        mediaMonitor = MediaMonitor(this)

        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification())

        // Register receiver for foreground app changes
        val filter = IntentFilter(ACTION_FOREGROUND_APP_CHANGED)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(foregroundAppReceiver, filter, RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(foregroundAppReceiver, filter)
        }

        startMonitoring()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        stopMonitoring()
        unregisterReceiver(foregroundAppReceiver)
        serviceScope.cancel()

        // Send final "not using" status
        serviceScope.launch {
            sendDeviceStatus(false, "Service Stopped")
        }
    }

    private fun startMonitoring() {
        monitorJob?.cancel()
        monitorJob = serviceScope.launch {
            while (isActive) {
                try {
                    checkAndSendStatus()
                } catch (e: Exception) {
                    android.util.Log.e("SleepyMonitorService", "Error in monitoring loop", e)
                }
                delay(configManager.checkInterval)
            }
        }
    }

    private fun stopMonitoring() {
        monitorJob?.cancel()
        monitorJob = null
    }

    private suspend fun checkAndSendStatus() {
        if (!configManager.isConfigValid()) {
            return
        }

        // Check if screen is on
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        val isScreenOn = powerManager.isInteractive

        if (!isScreenOn) {
            // Screen is off, report not using
            if (lastSentStatus.isNotEmpty()) {
                sendDeviceStatus(false, "")
                lastSentStatus = ""
            }
            return
        }

        // Build status string
        var statusString = buildStatusString()

        // Check if we should bypass this update
        if (configManager.bypassSameRequest && statusString == lastSentStatus) {
            return
        }

        // Send device status
        val isUsing = statusString.isNotEmpty()
        sendDeviceStatus(isUsing, statusString)
        lastSentStatus = statusString

        // Handle media status (standalone mode)
        if (configManager.mediaEnabled && configManager.mediaMode == MediaMode.STANDALONE) {
            sendMediaStatus()
        }
    }

    private fun buildStatusString(): String {
        val parts = mutableListOf<String>()

        // Add battery info if enabled
        if (configManager.batteryInfoEnabled) {
            val batteryInfo = BatteryUtil.getBatteryInfo(this)
            parts.add(batteryInfo.toDisplayString())
        }

        // Add app name
        if (currentAppName.isNotEmpty()) {
            parts.add("前台应用: $currentAppName")
        }

        // Add media info if in combined mode
        if (configManager.mediaEnabled && configManager.mediaMode == MediaMode.COMBINED) {
            val mediaInfo = mediaMonitor.getCurrentMediaInfo()
            if (mediaInfo.isPlaying) {
                val mediaString = mediaInfo.toDisplayString()
                if (mediaString.isNotEmpty()) {
                    parts.add("\n【正在播放】: $mediaString")
                }
            }
        }

        return parts.joinToString(" ")
    }

    private suspend fun sendDeviceStatus(using: Boolean, appName: String) {
        val status = DeviceStatus(
            secret = configManager.secret,
            id = configManager.deviceId,
            showName = configManager.deviceName,
            using = using,
            appName = appName
        )

        apiClient.sendDeviceStatus(configManager.apiUrl, status)
    }

    private suspend fun sendMediaStatus() {
        val mediaInfo = mediaMonitor.getCurrentMediaInfo()
        val mediaString = if (mediaInfo.isPlaying) {
            mediaInfo.toDisplayString()
        } else {
            ""
        }

        // Check if media status changed
        if (configManager.bypassSameRequest && mediaString == lastMediaStatus) {
            return
        }

        val status = DeviceStatus(
            secret = configManager.secret,
            id = configManager.mediaDeviceId.ifEmpty { configManager.deviceId + "_media" },
            showName = configManager.mediaDeviceName.ifEmpty { configManager.deviceName + " (Media)" },
            using = mediaInfo.isPlaying,
            appName = mediaString.ifEmpty { "没有媒体播放" }
        )

        apiClient.sendDeviceStatus(configManager.apiUrl, status)
        lastMediaStatus = mediaString
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = getString(R.string.notification_channel_description)
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        val pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_IMMUTABLE
        } else {
            0
        }

        val notificationIntent = packageManager.getLaunchIntentForPackage(packageName)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            pendingIntentFlags
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.notification_title))
            .setContentText(getString(R.string.notification_text))
            .setSmallIcon(R.drawable.ic_notification)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }
}
