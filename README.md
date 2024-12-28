In `gradle.properties`, change the following:

```properties
# Put your notification endpoint URL here
notifications.url=http://example.com

# Set the polling interval in minutes
polling.delay=10
```

After building and compiling the app, and downloading it, you should enable notifications and unrestrict background battery usage.

This is my first Kotlin project, so it may not result in great performance, but it is a simple tool to get working quickly. This application is designed to poll an API and use the results to create notifications on the Android operating system. I have created an API application that can be used with this polling system. You can find the repository [here](https://github.com/r4q0/Notification-API).

Created by Bilal Kerkeni  
Sponsored by Milo van Dam