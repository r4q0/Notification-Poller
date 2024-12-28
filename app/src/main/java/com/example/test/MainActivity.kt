package com.example.test

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import kotlin.random.Random
import android.os.Bundle
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.core.app.NotificationCompat
import com.example.test.ui.theme.TestTheme

class MainActivity : ComponentActivity() {

    // Channel ID for notifications
    private val channelid = "default_channel"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set content view using Jetpack Compose
        setContent {
            TestTheme {
                Text(text = "Sending Notification Example", modifier = Modifier.fillMaxSize())
            }


            // Create Notification Channel (for Android Oreo and above)
            createNotificationChannel()
            println("notification channel create")
            // Example variables to send notification

            var number = 1
            while (true) {
                val title = "$number Sample Title"
                val description = "$number This is a sample description."
                val url = "https://www.example$number.com"
                sendNotification(title, description, url)
                number++
                Thread.sleep(5000)
            }
        }
    }

    // Function to create the Notification Channel (Required for Android 8.0 and above)
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelid,
                "Default Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "This channel is used for default notifications."
            }

            // Register the channel with the system
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

//
//    // Function to send the notification
    private fun sendNotification(title: String, description: String, url: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Intent to open the URL when the notification is tapped
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        // Build the notification
        val notification = NotificationCompat.Builder(this, channelid)
            .setContentTitle(title)
            .setContentText(description)
            .setSmallIcon(android.R.drawable.ic_dialog_info)  // Simple icon
            .setContentIntent(pendingIntent)  // Open URL on click
            .setAutoCancel(true)  // Auto-dismiss after tap
            .build()

        // Send the notification
        notificationManager.notify(Random.nextInt(0, 999999999), notification)
    }
}
