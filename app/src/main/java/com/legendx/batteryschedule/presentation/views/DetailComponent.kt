package com.legendx.batteryschedule.presentation.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.legendx.batteryschedule.presentation.viewmodels.HomeViewModel
import com.legendx.batteryschedule.utils.ColorConfig
import com.legendx.batteryschedule.utils.DeviceUtils
import com.legendx.batteryschedule.utils.FontConfig


@Composable
fun DetailComponent(homeViewModel: HomeViewModel) {
    val context = LocalContext.current
    val batteryLevel = homeViewModel.getBatteryLevel(context)
    val deviceName = DeviceUtils.getDeviceName()
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = ColorConfig.mainColor
        ),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 6.dp
        )
    ) {
        Row {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .weight(1f),
            ) {
                Text(
                    text = deviceName,
                    fontFamily = FontConfig.lexend_deca,
                    fontSize = 18.sp,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = {},
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                    )
                ) {
                    Text(
                        text = "Turn Off",
                        fontSize = 16.sp,
                        fontFamily = FontConfig.lexend_deca,
                        fontWeight = FontWeight.SemiBold,
                        color = ColorConfig.mainColor
                    )
                }
            }
            CircleComponent(
                text = "$batteryLevel%",
                textColor = Color.White,
                mainColor = Color.White,
                percentage = batteryLevel,
                size = 80.dp,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(all = 30.dp),
            )
        }
    }
}