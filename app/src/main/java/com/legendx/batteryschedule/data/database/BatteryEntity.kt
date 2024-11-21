package com.legendx.batteryschedule.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "battery_table")
data class BatteryEntity(
    val title: String,
    val description: String,
    val batteryPercentage: Int,
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
)