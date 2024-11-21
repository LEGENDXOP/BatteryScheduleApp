package com.legendx.batteryschedule.utils

import androidx.compose.ui.graphics.Color

object ColorConfig {
    val mainColor = Color(0xFF5F33E1)

    fun getBatteryColor(percentage: Int): Color {
        return when (percentage) {
            in 0..20 -> Color(0xFFFFD12E)
            in 21..50 -> Color(0xFFFF9142)
            in 51..80 -> Color(0xFF5F33E1)
            else -> Color(0xFFF478B8)
        }
    }
}