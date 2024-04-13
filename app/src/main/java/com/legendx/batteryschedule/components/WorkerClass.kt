package com.legendx.batteryschedule.components

import android.content.Context
import android.content.Intent
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.legendx.batteryschedule.helpers.DataManage

class WorkerClass(appContext: Context, workParams: WorkerParameters): CoroutineWorker(appContext, workParams) {
    override suspend fun doWork(): Result {
        DataManage.initialize(applicationContext)
        val isSchedule = DataManage.getData("isSchedule").toBoolean()
        if (isSchedule){
            val intent = Intent(applicationContext, BatteryService::class.java)
            applicationContext.startService(intent)
        }
        else{
            val intent = Intent(applicationContext, BatteryService::class.java)
            applicationContext.stopService(intent)
        }
        return  Result.success()
    }
}