package com.legendx.batteryschedule.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [BatteryEntity::class], version = 1, exportSchema = false)
abstract class BatteryDatabase : RoomDatabase() {
    abstract fun getBatteryDao(): BatteryDao
}