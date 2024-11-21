package com.legendx.batteryschedule.data

data class BatteryData(
    val title: String,
    val description: String? = null,
    val batteryPercentage: Int
)
