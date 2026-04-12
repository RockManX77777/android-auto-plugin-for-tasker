package com.rockmanx77777.aaconnectionlistener

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.widget.Toast
import androidx.car.app.connection.CarConnection
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Observer

/**
 * A service that monitors the state of the Android Auto connection and triggers Tasker events accordingly.
 */
class AAConnectionMonitoringService : Service() {

    private lateinit var carConnection: CarConnection
    private var lastState: Boolean? = null
    private var notificationManager: NotificationManager? = null
    private lateinit var notificationBuilder: NotificationCompat.Builder


    private fun isAaConnected(): Boolean {
        val connectionState = carConnection.type.value ?: CarConnection.CONNECTION_TYPE_NOT_CONNECTED
        return connectionState != CarConnection.CONNECTION_TYPE_NOT_CONNECTED
    }

    private fun getConnectionText(connected: Boolean): String{
        return if (connected) getString(R.string.aa_connected) else getString(R.string.aa_disconnected)
    }
    /**
     * An observer that listens for changes in the car connection state.
     */
    private val connectionObserver = Observer<Int> {
        val isConnected = isAaConnected()
        var message: String = getConnectionText(isConnected)

        // On start, fire the 'Connected' event if we are already connected.
        if (lastState == null && isConnected){
            triggerConnectedTaskerEvent()
        }
        // Only fire events if we have a previous state (normal operation)
        else if (lastState != null && isConnected != lastState) {
            if(isConnected) triggerConnectedTaskerEvent() else triggerDisconnectedTaskerEvent()

        }
        message.let{
            Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
            updateNotification(isConnected)
        }

        lastState = isConnected
    }

    private fun updateNotification(isConnected: Boolean) {
        val statusText = getConnectionText(isConnected)
        
        notificationBuilder.setContentText(statusText)
        notificationManager?.notify(1, notificationBuilder.build())
    }

    /**
     * Creates a notification to show that the service is running.
     */
    private fun createNotification(): Notification {
        val channelId = "AAConnectionChannel"
        val channel = NotificationChannel(
            channelId,
            getString(R.string.notification_channel_name),
            NotificationManager.IMPORTANCE_LOW
        )
        val contentText = getConnectionText(isAaConnected())
        notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager?.createNotificationChannel(channel)

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setContentTitle(getString(R.string.notification_title))
            .setContentText(contentText)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setOngoing(true)
            .setSilent(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setContentIntent(pendingIntent)

        return notificationBuilder.build()
    }

    // Overridden functions
    override fun onCreate() {
        super.onCreate()
        carConnection = CarConnection(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(1, createNotification())

        carConnection.type.observeForever(connectionObserver)
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        carConnection.type.removeObserver(connectionObserver)
    }
}