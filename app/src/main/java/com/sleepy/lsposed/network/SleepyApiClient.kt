package com.sleepy.lsposed.network

import android.util.Log
import com.sleepy.lsposed.data.DeviceStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/**
 * HTTP client for communicating with Sleepy API
 */
class SleepyApiClient {
    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    companion object {
        private const val TAG = "SleepyApiClient"
        private val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()
    }

    /**
     * Send device status to server
     */
    suspend fun sendDeviceStatus(
        apiUrl: String,
        status: DeviceStatus
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val json = JSONObject().apply {
                put("secret", status.secret)
                put("id", status.id)
                put("show_name", status.showName)
                put("using", status.using)
                put("app_name", status.appName)
            }

            val requestBody = json.toString().toRequestBody(JSON_MEDIA_TYPE)
            val request = Request.Builder()
                .url(apiUrl)
                .post(requestBody)
                .build()

            client.newCall(request).execute().use { response ->
                val responseBody = response.body?.string() ?: ""
                
                if (response.isSuccessful) {
                    Log.d(TAG, "Status sent successfully: ${status.appName}")
                    Result.success(responseBody)
                } else {
                    val error = "Server error: ${response.code} - $responseBody"
                    Log.e(TAG, error)
                    Result.failure(Exception(error))
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to send status", e)
            Result.failure(e)
        }
    }
}
