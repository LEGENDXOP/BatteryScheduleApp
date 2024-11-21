package com.legendx.batteryschedule.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface BatteryDao {
    @Upsert
    suspend fun addScheduleDb(batteryEntity: BatteryEntity)

    @Delete
    suspend fun deleteScheduleDb(batteryEntity: BatteryEntity)

    @Query("SELECT * FROM battery_table")
    fun getAllBatteryDb(): Flow<List<BatteryEntity>>
}