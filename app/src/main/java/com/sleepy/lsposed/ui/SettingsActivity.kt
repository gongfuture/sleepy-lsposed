package com.sleepy.lsposed.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sleepy.lsposed.BuildConfig
import com.sleepy.lsposed.R
import com.sleepy.lsposed.config.ConfigManager
import com.sleepy.lsposed.databinding.ActivitySettingsBinding
import com.sleepy.lsposed.service.SleepyMonitorService

/**
 * Main settings activity with Material Design
 */
class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var configManager: ConfigManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configManager = ConfigManager(this)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = getString(R.string.app_name)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings_container, SettingsFragment())
                .commit()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_start_service -> {
                startMonitorService()
                true
            }
            R.id.action_stop_service -> {
                stopMonitorService()
                true
            }
            R.id.action_about -> {
                showAboutDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun startMonitorService() {
        if (!configManager.isConfigValid()) {
            MaterialAlertDialogBuilder(this)
                .setTitle("Configuration Required")
                .setMessage("Please configure all required settings before starting the service.")
                .setPositiveButton("OK", null)
                .show()
            return
        }

        val intent = Intent(this, SleepyMonitorService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }

        MaterialAlertDialogBuilder(this)
            .setTitle("Service Started")
            .setMessage("Sleepy monitoring service is now running.")
            .setPositiveButton("OK", null)
            .show()
    }

    private fun stopMonitorService() {
        val intent = Intent(this, SleepyMonitorService::class.java)
        stopService(intent)

        MaterialAlertDialogBuilder(this)
            .setTitle("Service Stopped")
            .setMessage("Sleepy monitoring service has been stopped.")
            .setPositiveButton("OK", null)
            .show()
    }

    private fun showAboutDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.about_title)
            .setMessage(
                "${getString(R.string.about_version, BuildConfig.VERSION_NAME)}\n\n" +
                        "${getString(R.string.about_description)}\n\n" +
                        getString(R.string.about_license)
            )
            .setPositiveButton("OK", null)
            .show()
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.preferences, rootKey)
        }
    }
}
