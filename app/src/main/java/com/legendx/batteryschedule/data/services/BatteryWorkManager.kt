package com.legendx.batteryschedule.data.services

import android.content.Context
import android.util.Log
import androidx.work.BackoffPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.await
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

object BatteryWorkManager {

    suspend fun setScheduleWork(context: Context) {
        val workManager = WorkManager.getInstance(context)
        Log.d("BatteryWorkManager", "setScheduleWork")
        val isSyncScheduled = withContext(Dispatchers.IO) {
            workManager.getWorkInfosByTag("sync_worker")
                .get()
                .isNotEmpty()
        }
        if (isSyncScheduled) {
            return
        }
        val workRequest = PeriodicWorkRequestBuilder<BatteryWorker>(
            repeatInterval = 15,
            repeatIntervalTimeUnit = TimeUnit.MINUTES
        ).setBackoffCriteria(
            backoffPolicy = BackoffPolicy.EXPONENTIAL,
            backoffDelay = 2000L,
            timeUnit = TimeUnit.MILLISECONDS
        ).addTag("sync_worker").build()

        workManager.enqueue(workRequest).await()
    }

    suspend fun cancelAllSync(context: Context) {
        val workManager = WorkManager.getInstance(context)
        workManager.cancelAllWork()
            .await()
    }
}