package com.legendx.batteryschedule.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.BatteryManager
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.legendx.batteryschedule.data.BatteryData
import com.legendx.batteryschedule.data.database.BatteryEntity
import com.legendx.batteryschedule.data.services.AlarmReceiver
import com.legendx.batteryschedule.data.services.BatteryWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

fun String.toast(context: Context) {
    try {
        CoroutineScope(Dispatchers.Main).launch {
            Toast.makeText(context, this@toast, Toast.LENGTH_SHORT).show()
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun Context.handleNotificationPermission(activity: ComponentActivity, onResult: (Boolean) -> Unit) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            onResult(true)
        } else {
            val requestPermissionLauncher = activity.registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted ->
                if (isGranted) {
                    onResult(true)
                } else {
                    onResult(false)
                }
            }
            try {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } catch (e: Exception) {
                e.printStackTrace()
                onResult(false)
            }
        }
    } else {
        onResult(true)
    }
}


fun Context.handleAlarmPermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val alarmManager = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (!alarmManager.canScheduleExactAlarms()) {
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            "Please allow battery schedule alarm permission".toast(applicationContext)
            this.startActivity(intent)
        }
    }
}

class Throttler(private val intervalMillis: Long) {
    private val isRunning = AtomicBoolean(false)

    suspend fun runOncePerInterval(block: suspend () -> Unit) {
        if (isRunning.compareAndSet(false, true)) {
            try {
                block()
            } finally {
                delay(intervalMillis * 60000)
                isRunning.set(false)
            }
        }
    }
}

object DeviceUtils {
    fun getDeviceName(): String {
        val manufacturer = Build.MANUFACTURER.uppercase()
        val model = Build.MODEL

        return if (model.startsWith(manufacturer)) {
            "$model"
        } else {
            "$manufacturer $model"
        }
    }

}

fun BatteryEntity.toBatteryData(): BatteryData {
    return BatteryData(
        title = this.title,
        description = this.description,
        batteryPercentage = this.batteryPercentage
    )
}

object BatteryUtils {
    fun getCurrentBatteryPercentage(context: Context): Int {
        val intentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryStatus: Intent? = context.registerReceiver(null, intentFilter)
        batteryStatus?.let {
            val level = it.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale = it.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            if (level != -1 && scale != -1) {
                return (level * 100) / scale
            }
        }
        return -1
    }

    fun isBatteryCloseToTarget(currentBattery: Int, targetBattery: Int): Boolean {
        val lowerBound = targetBattery - 5
        val upperBound = targetBattery + 3

        return currentBattery in lowerBound..upperBound
    }

}

object Schedulers {
    private const val INTERVAL_MILLIS = 15 * 60 * 1000L

    @SuppressLint("ScheduleExactAlarm")
    @RequiresApi(Build.VERSION_CODES.O)
    fun scheduleExactAlarm(context: Context, intervalMillis: Long = INTERVAL_MILLIS) {
        val alarmManager by lazy { context.getSystemService(ALARM_SERVICE) as AlarmManager }
        val alarmIntent = Intent(context, AlarmReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + intervalMillis,
            alarmIntent
        )
    }


    fun scheduleWithWorkManager(context: Context) {
        val threadThrottler = Throttler(2)
        val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        val workRequest = PeriodicWorkRequestBuilder<BatteryWorker>(
            15, TimeUnit.MINUTES
        ).build()
        val randomID = UUID.randomUUID().toString()
        coroutineScope.launch {
            threadThrottler.runOncePerInterval {
                WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                    randomID,
                    ExistingPeriodicWorkPolicy.REPLACE,
                    workRequest
                )
            }
        }
    }
}