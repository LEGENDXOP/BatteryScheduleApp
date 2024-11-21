package com.legendx.batteryschedule.data.database

import android.content.Context
import androidx.room.Room

object AppDatabase {
    @Volatile
    private var instance: BatteryDatabase? = null

    fun getDatabase(context: Context): BatteryDatabase {
        return instance ?: synchronized(this) {
            var db = Room.databaseBuilder(
                context.applicationContext,
                BatteryDatabase::class.java,
                "battery_database"
            ).build()
            instance = db
            db
        }
    }
}