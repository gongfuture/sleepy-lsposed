package com.sleepy.lsposed.data

/**
 * Device status data
 */
data class DeviceStatus(
    val secret: String,
    val id: String,
    val showName: String,
    val using: Boolean,
    val appName: String
)

/**
 * Media playback information
 */
data class MediaInfo(
    val isPlaying: Boolean,
    val title: String,
    val artist: String,
    val album: String
) {
    fun toDisplayString(): String {
        if (!isPlaying || (title.isEmpty() && artist.isEmpty())) {
            return ""
        }

        val parts = mutableListOf<String>()
        if (title.isNotEmpty()) {
            parts.add("♪$title")
        }
        if (artist.isNotEmpty() && artist != title) {
            parts.add(artist)
        }
        if (album.isNotEmpty() && album != title && album != artist) {
            parts.add(album)
        }

        return if (parts.isNotEmpty()) {
            parts.joinToString(" - ")
        } else {
            "♪播放中"
        }
    }
}

/**
 * Battery information
 */
data class BatteryInfo(
    val level: Int,
    val isCharging: Boolean
) {
    fun toDisplayString(): String {
        return if (isCharging) {
            "[🔋${level}%⚡]"
        } else {
            "[🔋${level}%]"
        }
    }
}
