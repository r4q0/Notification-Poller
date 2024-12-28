    package com.example.test
    import kotlinx.coroutines.*
    import android.app.NotificationChannel
    import android.app.NotificationManager
    import android.app.PendingIntent
    import android.content.Context
    import android.content.Intent
    import android.net.Uri
    import kotlin.random.Random
    import okhttp3.OkHttpClient
    import okhttp3.Request
    import android.os.StrictMode
    import okhttp3.Response
    import android.os.Bundle
    import android.os.Build
    import androidx.activity.ComponentActivity
    import androidx.activity.compose.setContent
    import androidx.compose.foundation.layout.fillMaxSize
    import androidx.compose.material3.Text
    import androidx.compose.ui.Modifier
    import androidx.core.app.NotificationCompat
    import com.example.test.ui.theme.TestTheme
    import org.json.JSONArray

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
            }
                CoroutineScope(Dispatchers.IO).launch {

                    var number = 1
                    while (true) {
                        testrequest()
                        number++
                        Thread.sleep(30000)
                    }
                }

            }



        private fun testrequest() {
           CoroutineScope(Dispatchers.IO).launch {
        val url = "http://212.227.128.166:8090/"
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()


            val response = client.newCall(request).execute()
            val json = response.body?.string()
            withContext(Dispatchers.Main) {
                json?.let {
                    val jsonArray = JSONArray(it)
                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        val title = jsonObject.getString("title")
                        val description = jsonObject.getString("message")
                        val notificationUrl = jsonObject.getString("url")
                        sendNotification(title, description, notificationUrl)

                }
            }
        }
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
