package com.sleepy.lsposed.utils

import android.content.Context
import android.media.MediaMetadata
import android.media.session.MediaController
import android.media.session.MediaSessionManager
import android.media.session.PlaybackState
import com.sleepy.lsposed.data.MediaInfo

/**
 * Utility for monitoring media playback
 */
class MediaMonitor(private val context: Context) {
    private var mediaSessionManager: MediaSessionManager? = null

    init {
        try {
            mediaSessionManager = context.getSystemService(Context.MEDIA_SESSION_SERVICE) as? MediaSessionManager
        } catch (e: Exception) {
            android.util.Log.e("MediaMonitor", "Failed to get MediaSessionManager", e)
        }
    }

    /**
     * Get current media playback information
     */
    fun getCurrentMediaInfo(): MediaInfo {
        try {
            val controllers = mediaSessionManager?.getActiveSessions(null) ?: emptyList()
            
            for (controller in controllers) {
                val playbackState = controller.playbackState
                if (playbackState?.state == PlaybackState.STATE_PLAYING) {
                    val metadata = controller.metadata
                    if (metadata != null) {
                        return extractMediaInfo(metadata, true)
                    }
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("MediaMonitor", "Failed to get media info", e)
        }

        return MediaInfo(false, "", "", "")
    }

    private fun extractMediaInfo(metadata: MediaMetadata, isPlaying: Boolean): MediaInfo {
        val title = metadata.getString(MediaMetadata.METADATA_KEY_TITLE) ?: ""
        val artist = metadata.getString(MediaMetadata.METADATA_KEY_ARTIST) ?: ""
        val album = metadata.getString(MediaMetadata.METADATA_KEY_ALBUM) ?: ""

        return MediaInfo(
            isPlaying = isPlaying,
            title = title,
            artist = artist,
            album = album
        )
    }

    /**
     * Register callback for media session changes
     */
    fun registerCallback(callback: MediaSessionManager.OnActiveSessionsChangedListener) {
        try {
            mediaSessionManager?.addOnActiveSessionsChangedListener(callback, null)
        } catch (e: Exception) {
            android.util.Log.e("MediaMonitor", "Failed to register callback", e)
        }
    }

    /**
     * Unregister callback
     */
    fun unregisterCallback(callback: MediaSessionManager.OnActiveSessionsChangedListener) {
        try {
            mediaSessionManager?.removeOnActiveSessionsChangedListener(callback)
        } catch (e: Exception) {
            android.util.Log.e("MediaMonitor", "Failed to unregister callback", e)
        }
    }
}
