A long time ago, Tasker used the variable `%UIMODE` to detect Android Auto connection events. At some point, Google removed `%UIMODE` and Tasker has been unable to detect AA connection status reliably since then. Apparently, Google separated Android Auto from Android (Android Auto is now a "system projection service"), and it made the connection state harder to detect. I tried using Bluetooth and Notification events to detect AA connections, but none worked reliably. So I made my own.

The app creates a Service that binds to the `CarConnection` library. Once bound, the app is notified by the library when the connection type changes. It then fires the events that Tasker would be listening for.

Battery drain is minimal. The app is not polling the status. The app is basically asleep until the library wakes it on any connection type change.

This does what I need it to do. It's barebones and may not see many updates unless Android or AA changes something that breaks it. I'm sharing it in case someone else misses `%UIMODE`, too. If somehow this app becomes popular and a certain feature is requested often, I might implement it.
