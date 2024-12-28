package com.example.Notification.Poller

import kotlinx.coroutines.*
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import kotlin.random.Random
import okhttp3.OkHttpClient
import okhttp3.Request
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.core.app.NotificationCompat
import com.example.Notification.Poller.ui.theme.TestTheme
import org.json.JSONArray
import android.os.PowerManager

class MainActivity : ComponentActivity() {

    // Channel ID for notifications
    private val channelId = "default_channel"

    // Polling delay in milliseconds (converted from minutes in BuildConfig)
    private val pollingDelay = BuildConfig.polling_delay.toLong() * 60000

    // URL for fetching notifications
    private val notificationUrl = BuildConfig.notifications_url

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set content view using Jetpack Compose
        setContent {
            TestTheme {
                Text(
                    text = "Made by Bilal Kerkeni Sponsored by Milo Van Dam",
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // This piece of code makes sure the app still works while minimized
            val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
            val wakeLock = powerManager.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK,
                "MyApp::WakeLock"
            )
            try {
                wakeLock.acquire()

            // Launch a coroutine for periodic polling
            CoroutineScope(Dispatchers.IO).launch {
                while (true) {
                    pollNotifications()
                    Thread.sleep(pollingDelay) // Wait for the next polling cycle
                }
            }        } finally {
            wakeLock.release()
        }
    }


    private fun createChannel(channelId: String, channelName: String, context: Context) {
        // Check if we're running on Android Oreo or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT // You can change this based on your needs
            val channel = NotificationChannel(channelId, channelName, importance).apply {
            }

            // Get the system's NotificationManager
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Create the channel (only on Android Oreo or higher)
            notificationManager.createNotificationChannel(channel)
        }
    }

    // Function to make a network request and process notifications
    private fun pollNotifications() {
        CoroutineScope(Dispatchers.IO).launch {
            val url = notificationUrl
            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()

            val response = client.newCall(request).execute()
            val json = response.body?.string()

            withContext(Dispatchers.Main) {
                if (!json.isNullOrEmpty()) {
                    // Parse the JSON response and create notifications
                    val jsonArray = JSONArray(json)
                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        val title = jsonObject.getString("title")
                        val description = jsonObject.getString("description")
                        val notificationUrl = jsonObject.optString("url", "")
                        sendNotification(title, description, notificationUrl)
                    }
                }
            }
        }
    }

    // Function to send a notification to the user
    private fun sendNotification(title: String, description: String, url: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create an intent to open the URL when the notification is tapped
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        // Build the notification
        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title) // Notification title
            .setContentText(description) // Notification description
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Notification icon
            .setContentIntent(pendingIntent) // Open URL on click
            .setAutoCancel(true) // Dismiss notification after tap
            .build()

        // Send the notification with a unique ID
        notificationManager.notify(Random.nextInt(0, 999999999), notification)
    }
}
