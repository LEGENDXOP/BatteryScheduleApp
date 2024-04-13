package com.legendx.batteryschedule.components

import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.widget.Toast
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.legendx.batteryschedule.R
import com.legendx.batteryschedule.helpers.DataManage
import com.legendx.batteryschedule.helpers.HelperFunctions
import com.legendx.batteryschedule.helpers.ScheduleSet


class BatteryService : Service() {
    private var batteryBroadcastReceiver: BatteryBroadcastReceiver? = null

    override fun onCreate() {
        super.onCreate()
        batteryBroadcastReceiver = BatteryBroadcastReceiver()
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        registerReceiver(batteryBroadcastReceiver, filter)
    }


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    inner class BatteryBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            DataManage.initialize(context!!)
            intent?.let {
                if (it.action == Intent.ACTION_BATTERY_CHANGED) {
                    val level = it.getIntExtra("level", 0)
                    val scale = it.getIntExtra("scale", 100)
                    val batteryLevel = level * 100 / scale
                    workNow(batteryLevel, context)
                }
            }
        }
    }
}


fun workNow(currentBatteryLevel: Int, context: Context) {
    val allData = HelperFunctions.allSchedule()
    allData.forEach {
        if (it.batteryLevel == currentBatteryLevel) {
            Toast.makeText(context, it.scheduleMessage, Toast.LENGTH_LONG).show()
            sendNotification(context, it.scheduleMessage)
            val finalMessage = ScheduleSet(it.id, it.batteryLevel, it.scheduleMessage)
            HelperFunctions.removeSchedule(finalMessage)
        }
    }
}

const val CHANNEL_ID = "BatterySchedule"


fun sendNotification(context: Context, message: String) {
    val notificationManager = ContextCompat.getSystemService(
        context,
        NotificationManager::class.java
    ) as NotificationManager

    val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.baseline_notifications_active_24)
        .setContentTitle("Battery Schedule")
        .setContentText(message)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)
        .setColor(Color(0xFF5F33E1).toArgb())

    val randomID = (0..1000).random()
    notificationManager.notify(randomID, notificationBuilder.build())
}
