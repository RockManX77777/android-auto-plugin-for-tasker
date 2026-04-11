package com.rockmanx77777.aaconnectionlistener

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * A broadcast receiver that listens for the device to finish booting.
 *
 * When the device boots, this receiver starts the [AAConnectionMonitoringService] to monitor
 * the Android Auto connection state.
 */
class BootCompletedReceiver : BroadcastReceiver() {
    /**
     * This method is called when the BroadcastReceiver is receiving an Intent broadcast.
     *
     * @param context The Context in which the receiver is running.
     * @param intent The Intent being received.
     */
    override fun onReceive(context: Context, intent: Intent) {
        // Check if the received action is for the boot completed event.
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // Create an Intent to start the AutoConnectionService.
            val serviceIntent = Intent(
                context,
                AAConnectionMonitoringService::class.java
            )

            // Start the service as a foreground service to ensure it continues running.
            context.startForegroundService(serviceIntent)
        }
    }
}
