package com.legendx.batteryschedule.presentation.views


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.legendx.batteryschedule.R
import com.legendx.batteryschedule.data.BatteryData
import com.legendx.batteryschedule.presentation.viewmodels.HomeViewModel
import com.legendx.batteryschedule.utils.ColorConfig
import com.legendx.batteryschedule.utils.FontConfig
import com.legendx.batteryschedule.utils.toBatteryData

@Composable
fun MainItems(
    homeViewModel: HomeViewModel,
    showSorted: Boolean = true,
    modifier: Modifier = Modifier
) {
    val batteryList = if (showSorted) homeViewModel.sortedBatteryList else homeViewModel.batteryList
    val haptics = LocalHapticFeedback.current
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Pending Tasks", style = MaterialTheme.typography.headlineSmall,
                fontFamily = FontConfig.lexend_deca,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(16.dp)
            )
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(ColorConfig.mainColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${homeViewModel.batteryList.size}",
                    color = ColorConfig.mainColor,
                    fontFamily = FontConfig.lexend_deca,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
        if (batteryList.isNotEmpty()) {
            LazyColumn {

                items(batteryList, key = { it.id }) { batteryData ->
                    STDBox(
                        onEdit = {},
                        onDelete = {
                            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            homeViewModel.deleteItemFromBatteryList(batteryData)
                        }
                    ) {
                        ItemCardView(
                            batteryData = batteryData.toBatteryData()
                        )
                    }
                }
            }
        } else {
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                Text(
                    text = "No tasks found. Please add tasks",
                    fontFamily = FontConfig.lexend_deca,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun STDBox(
    onEdit: (String) -> Unit,
    onDelete: (String) -> Unit,
    content: @Composable () -> Unit
) {
    var icon = Icons.Default.Settings
    var color = Color.Transparent
    var alignment = Alignment.Center
    val haptics = LocalHapticFeedback.current
    val stdState = rememberSwipeToDismissBoxState(positionalThreshold = { 1000f })
    when (stdState.dismissDirection) {
        SwipeToDismissBoxValue.StartToEnd -> {
//            icon = Icons.Default.Edit
//            color = Color.Green
//            alignment = Alignment.CenterStart
        }

        SwipeToDismissBoxValue.EndToStart -> {
            LaunchedEffect(true) {
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
            }
            icon = Icons.Default.Delete
            color = Color.Red
            alignment = Alignment.CenterEnd
        }

        else -> false
    }
    when (stdState.currentValue) {
        SwipeToDismissBoxValue.StartToEnd -> {
            onEdit("")
        }

        SwipeToDismissBoxValue.EndToStart -> {
            onDelete("")
        }

        else -> false
    }
    SwipeToDismissBox(
        state = stdState,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            Box(
                contentAlignment = alignment,
                modifier = Modifier
                    .fillMaxSize()
                    .background(color)
            ) {
                Icon(
                    modifier = Modifier.minimumInteractiveComponentSize(),
                    imageVector = icon, contentDescription = "Delete Icon"
                )
            }
        }
    ) {
        content()
    }
}

@Composable
fun ItemCardView(
    batteryData: BatteryData
) {
    val batteryColor = ColorConfig.getBatteryColor(batteryData.batteryPercentage)
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .size(120.dp)
            .padding(18.dp),
        shape = RoundedCornerShape(18.dp),
        border = CardDefaults.outlinedCardBorder(
            enabled = false
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(batteryColor.copy(alpha = 0.2f))
                    .padding(10.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.book_bag_icon),
                    contentDescription = batteryData.title,
                    tint = batteryColor,
                    modifier = Modifier.size(30.dp)
                )
            }
            Text(
                text = batteryData.title,
                style = MaterialTheme.typography.bodyLarge,
                fontFamily = FontConfig.lexend_deca,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                modifier = Modifier
                    .padding(start = 16.dp)
                    .weight(1f)
            )
            CircleComponent(
                text = "${batteryData.batteryPercentage}%",
                textColor = MaterialTheme.colorScheme.onSurface,
                mainColor = batteryColor,
                percentage = batteryData.batteryPercentage,
                size = 50.dp,
                textSize = 14.sp,
                strokeWith = 5.dp
            )
        }
    }
}