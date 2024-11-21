package com.legendx.batteryschedule.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.legendx.batteryschedule.R

object NotificationHelper {
    const val SERVICE_CHANNEL_ID = "service_channel"
    private const val ALERT_CHANNEL_ID = "alert_channel"

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                SERVICE_CHANNEL_ID,
                "Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT,
            ).apply {
                description = "This is for the foreground service"
            }
            val alertChannel = NotificationChannel(
                ALERT_CHANNEL_ID,
                "Alert Channel",
                NotificationManager.IMPORTANCE_HIGH,
            ).apply {
                description = "This is for the alert notification"
            }

            val manager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(serviceChannel)
            manager.createNotificationChannel(alertChannel)
        }
    }

    fun sendNotification(
        context: Context,
        title: String,
        message: String,
        id: Int = 11,
        icon: Int = R.drawable.bell_icon
    ): Boolean {
        return try {
            val manager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val notification = NotificationCompat.Builder(context, ALERT_CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(icon)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .build()
            manager.notify(id, notification)
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            e.toString().toast(context)
            false
        }
    }

}