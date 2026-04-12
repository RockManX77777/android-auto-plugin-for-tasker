package com.rockmanx77777.aaconnectionlistener

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MainActivity : AppCompatActivity() {

    private val requesterPostNotifications =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                startAaMonitoring()
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // This block will only be entered on API 33+ if permission was denied
                val permission = Manifest.permission.POST_NOTIFICATIONS
                val showRationale = shouldShowRequestPermissionRationale(permission)

                if (showRationale){
                    // Denied once
                    explainRequirementPostNotifications()
                } else{
                    // Permanently denied
                    showSettingsDialog()
                }
            }
        }

    private fun explainRequirementPostNotifications() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Permission Required")
            .setMessage("This app needs the notification permission to run the connection monitoring service in the background. Please grant this permission to enable the app's functionality.")
            .setPositiveButton("Ask Again") { _, _ ->
                // User wants to try again, launch the permission request
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    requesterPostNotifications.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showSettingsDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Permission Permanently Denied")
            .setMessage("You have permanently denied the notification permission. To enable the feature, you must go to the app settings and grant the permission manually.")
            .setPositiveButton("Go to Settings") { _, _ ->
                // Intent to open the app's settings screen
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS) //TODO Activity may not exist
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun setBootReceiverEnabled(enabled: Boolean) {
        val componentName = android.content.ComponentName(this, BootCompletedReceiver::class.java)
        val packageManager = packageManager
        val newState = if (enabled) {
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED
        } else {
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED
        }
        packageManager.setComponentEnabledSetting(
            componentName,
            newState,
            PackageManager.DONT_KILL_APP
        )
    }

    private fun startAaMonitoring() {
        setBootReceiverEnabled(true)
        val serviceIntent = Intent(this, AAConnectionMonitoringService::class.java)
        startForegroundService(serviceIntent)
        Toast.makeText(this, "Service started", Toast.LENGTH_SHORT).show()
    }

    private fun stopAaMonitoring() {
        setBootReceiverEnabled(false)
        val serviceIntent = Intent(this, AAConnectionMonitoringService::class.java)
        stopService(serviceIntent)
        Toast.makeText(this, "Service stopped", Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val startButton: Button = findViewById(R.id.startButton)
        startButton.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // Check if permission is already granted
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                    startAaMonitoring()
                } else {
                    // Request permission
                    requesterPostNotifications.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            } else {
                // No permission needed for older versions
                startAaMonitoring()
            }
        }
        val stopButton: Button = findViewById(R.id.stopButton)
        stopButton.setOnClickListener {
            stopAaMonitoring()
        }
    }
}
