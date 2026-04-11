package com.rockmanx77777.aaconnectionlistener

import android.app.Activity
import android.content.Context
import android.os.Bundle
import com.joaomgcd.taskerpluginlibrary.config.TaskerPluginConfig
import com.joaomgcd.taskerpluginlibrary.config.TaskerPluginConfigHelperEventNoOutputOrInputOrUpdate
import com.joaomgcd.taskerpluginlibrary.config.TaskerPluginConfigNoInput
import com.joaomgcd.taskerpluginlibrary.extensions.requestQuery
import com.joaomgcd.taskerpluginlibrary.input.TaskerInput

//region Connected Event
/**
 * A helper class for the Tasker connected event.
 * This class is used to configure the event in Tasker.
 * @param config The Tasker plugin configuration.
 */
class ConnectedEventHelper(config: TaskerPluginConfig<Unit>) : TaskerPluginConfigHelperEventNoOutputOrInputOrUpdate(config) {
    override fun addToStringBlurb(input: TaskerInput<Unit>, blurbBuilder: StringBuilder) {
        // This text will appear in Tasker.
        blurbBuilder.append(context.getString(R.string.event_label_connected))
    }
}

/**
 * An activity that is used to configure the Tasker connected event.
 */
class ActivityConfigConnectedEvent : Activity(), TaskerPluginConfigNoInput {
    override val context: Context get() = applicationContext
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ConnectedEventHelper(this).finishForTasker()
    }
}

/**
 * Triggers the Tasker connected event.
 * This function is an extension function for the Context class.
 */
fun Context.triggerConnectedTaskerEvent() {
    // This function triggers the 'Connected' event.
    ActivityConfigConnectedEvent::class.java.requestQuery(this)
}
//endregion

//region Disconnected Event
/**
 * A helper class for the Tasker disconnected event.
 * This class is used to configure the event in Tasker.
 * @param config The Tasker plugin configuration.
 */
class DisconnectedEventHelper(config: TaskerPluginConfig<Unit>) : TaskerPluginConfigHelperEventNoOutputOrInputOrUpdate(config) {
    override fun addToStringBlurb(input: TaskerInput<Unit>, blurbBuilder: StringBuilder) {
        // This text will appear in Tasker.
        blurbBuilder.append(context.getString(R.string.event_label_disconnected))
    }
}

/**
 * An activity that is used to configure the Tasker disconnected event.
 */
class ActivityConfigDisconnectedEvent : Activity(), TaskerPluginConfigNoInput {
    override val context: Context get() = applicationContext
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DisconnectedEventHelper(this).finishForTasker()
    }
}

/**
 * Triggers the Tasker disconnected event.
 * This function is an extension function for the Context class.
 */
fun Context.triggerDisconnectedTaskerEvent() {
    // This function triggers the 'Disconnected' event.
    ActivityConfigDisconnectedEvent::class.java.requestQuery(this)
}
//endregion