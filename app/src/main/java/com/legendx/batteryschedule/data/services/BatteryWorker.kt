package com.legendx.batteryschedule.data.services

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.legendx.batteryschedule.data.database.AppDatabase
import com.legendx.batteryschedule.utils.BatteryUtils
import com.legendx.batteryschedule.utils.NotificationHelper
import com.legendx.batteryschedule.utils.Schedulers
import com.legendx.batteryschedule.utils.toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class BatteryWorker(appContext: Context, workParams: WorkerParameters) :
    CoroutineWorker(appContext, workParams) {

    override suspend fun doWork(): Result {
        val batteryDao = AppDatabase.getDatabase(applicationContext).getBatteryDao()
        val currentBattery = BatteryUtils.getCurrentBatteryPercentage(applicationContext)
        batteryDao.getAllBatteryDb().collect { data ->
            data.forEach { batteryData ->
                if (BatteryUtils.isBatteryCloseToTarget(
                        currentBattery,
                        batteryData.batteryPercentage
                    )
                ) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                WorkerHelper.checkAndRequestExactAlarmPermission(applicationContext)
            } else {
                WorkerHelper.startService(applicationContext)
            }
            } else {
            applicationContext.stopService(
                Intent(
                    applicationContext,
                    BatteryService::class.java
                )
            )
        }
            }
        }
        return Result.success()
    }

}

object WorkerHelper {

    @RequiresApi(Build.VERSION_CODES.S)
    fun checkAndRequestExactAlarmPermission(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (!alarmManager.canScheduleExactAlarms()) {
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            "Please allow battery schedule alarm permission".toast(context)
            context.startActivity(intent)
        } else {
            startServiceWithPendingIntent(context)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun startServiceWithPendingIntent(context: Context) {
        val alarmManager =
            context.applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context.applicationContext, BatteryService::class.java)
        val pendingIntent = PendingIntent.getForegroundService(
            context.applicationContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + 3000,
            pendingIntent
        )
    }

    fun startService(context: Context) {
        val intent = Intent(context.applicationContext, BatteryService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                checkAndRequestExactAlarmPermission(context.applicationContext)
            } else {
                startServiceWithPendingIntent(context.applicationContext)
            }
        } else {
            context.applicationContext.startService(intent)
        }
    }
}

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        NotificationHelper.sendNotification(
            context.applicationContext,
            "Starting to Schedule a new task",
            "soon it will be started",
            50
        )
        val batteryDao by lazy {
            AppDatabase.getDatabase(context.applicationContext).getBatteryDao()
        }
        val currentBattery = BatteryUtils.getCurrentBatteryPercentage(context.applicationContext)
        val coroutineScope = CoroutineScope(Dispatchers.IO)
        coroutineScope.launch {
            batteryDao.getAllBatteryDb().collect { data ->
                data.forEach { batteryData ->
                    if (
                        BatteryUtils.isBatteryCloseToTarget(
                            currentBattery,
                            batteryData.batteryPercentage
                        )
                    ) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            WorkerHelper.startServiceWithPendingIntent(context)
                        } else {
                            WorkerHelper.startService(context)
                        }
                    }
                }
            }
        }.invokeOnCompletion {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                Schedulers.scheduleExactAlarm(context.applicationContext)
            } else {
                Schedulers.scheduleWithWorkManager(context.applicationContext)
            }
            NotificationHelper.sendNotification(
                context.applicationContext,
                "Scheduled A Alarm",
                "hehe-hehe",
                60
            )
        }
    }
}