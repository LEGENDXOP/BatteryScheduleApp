package com.legendx.batteryschedule.components

import android.content.Context
import android.widget.Toast
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WorkerClass(appContext: Context, workParams: WorkerParameters): CoroutineWorker(appContext, workParams) {
    override suspend fun doWork(): Result {
        sendNotification(applicationContext, "Testing")
        withContext(Dispatchers.Main){
            Toast.makeText(applicationContext, "Testing", Toast.LENGTH_LONG).show()
        }
        return  Result.success()
    }
}