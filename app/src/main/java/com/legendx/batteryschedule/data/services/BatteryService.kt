package com.legendx.batteryschedule.data.services

import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import androidx.core.app.NotificationCompat
import com.legendx.batteryschedule.HomeActivity
import com.legendx.batteryschedule.R
import com.legendx.batteryschedule.data.database.AppDatabase
import com.legendx.batteryschedule.data.database.BatteryEntity
import com.legendx.batteryschedule.utils.NotificationHelper
import com.legendx.batteryschedule.utils.toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch

class BatteryService : Service() {
    var batteryBroadcastReceiver: BatteryBroadcastReceiver? = null
    val batteryDao by lazy { AppDatabase.getDatabase(applicationContext).getBatteryDao() }
    val batteryList = mutableListOf<BatteryEntity>()
    val coroutineScope = CoroutineScope(Dispatchers.IO + Job())

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS).apply {
                putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                putExtra(Settings.EXTRA_CHANNEL_ID, NotificationHelper.SERVICE_CHANNEL_ID)
            }
        } else {
            Intent(this, HomeActivity::class.java)
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, NotificationHelper.SERVICE_CHANNEL_ID)
            .setContentTitle("Battery Service")
            .setContentText("Running in the background")
            .setSmallIcon(R.drawable.run_sports_runner_icon)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .addAction(
                R.drawable.help_icon,
                "Hide",
                pendingIntent
            )
            .build()
        startForeground(1, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        coroutineScope.launch {
            batteryDao.getAllBatteryDb().collect { data ->
                data.forEach {
                    batteryList.clear()
                    batteryList.addAll(data)
                }
            }
        }
        batteryBroadcastReceiver = BatteryBroadcastReceiver()
        registerReceiver(batteryBroadcastReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(batteryBroadcastReceiver)
        coroutineScope.coroutineContext.cancelChildren()
    }

    inner class BatteryBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == Intent.ACTION_BATTERY_CHANGED) {
                Log.d("BatteryLog", "Battery level changed")
                val level = intent.getIntExtra("level", 0)
                val scale = intent.getIntExtra("scale", 100)
                val currentBatteryPercent = level * 100 / scale
                batteryList.forEach { data ->
                    if (currentBatteryPercent == data.batteryPercentage) {
                        NotificationHelper.sendNotification(
                            context!!,
                            data.title,
                            data.description,
                            R.drawable.bell_icon
                        )
                        coroutineScope.launch {
                            batteryList.remove(data)
                            batteryDao.deleteScheduleDb(data)
                        }
                        data.title.toast(applicationContext)
                    }
                }
            }
        }

    }

}