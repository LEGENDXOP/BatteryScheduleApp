package com.legendx.batteryschedule

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.legendx.batteryschedule.data.database.AppDatabase
import com.legendx.batteryschedule.presentation.viewmodels.HomeViewModel
import com.legendx.batteryschedule.presentation.views.AddScheduleComponent
import com.legendx.batteryschedule.presentation.views.DetailComponent
import com.legendx.batteryschedule.presentation.views.FloatingComponent
import com.legendx.batteryschedule.presentation.views.HelpMenu
import com.legendx.batteryschedule.presentation.views.MainItems
import com.legendx.batteryschedule.presentation.views.TopComponent
import com.legendx.batteryschedule.ui.theme.BatteryScheduleTheme
import com.legendx.batteryschedule.utils.NotificationHelper
import com.legendx.batteryschedule.utils.Schedulers
import com.legendx.batteryschedule.utils.handleAlarmPermission
import com.legendx.batteryschedule.utils.handleNotificationPermission
import com.legendx.batteryschedule.utils.toast


class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val appDatabase = AppDatabase.getDatabase(this)
        applicationContext.handleNotificationPermission(this@HomeActivity) {
            if (it) {
                println("Notification permission granted")
                applicationContext.handleAlarmPermission()
                NotificationHelper.createNotificationChannels(applicationContext)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    handleAlarmPermission()
                    Schedulers.scheduleExactAlarm(applicationContext)
                    NotificationHelper.sendNotification(
                        applicationContext,
                        "First Schedule",
                        "First schedule added successfully"
                    )
                } else {
                    Schedulers.scheduleWithWorkManager(applicationContext)
                }
            } else {
                "Notification permission denied, please allow from settings".toast(
                    applicationContext
                )
                (this as? ComponentActivity)?.finishAffinity()
            }
        }
        setContent {
            val homeViewModel = viewModel<HomeViewModel> { HomeViewModel(appDatabase) }
            val haptics = LocalHapticFeedback.current
            var showScheduleAdd by remember { mutableStateOf(false) }
            var helpMenu by remember { mutableStateOf(false) }
            BatteryScheduleTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopComponent(
                            title = "Battery Schedule",
                            icon = painterResource(R.drawable.help_icon)
                        ) {
                            helpMenu = true
                        }
                    },
                    floatingActionButton = {
                        FloatingComponent {
                            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            showScheduleAdd = true
                            println("Floating button clicked")
                        }
                    }
                ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    ) {
                        DetailComponent(homeViewModel)
                        MainItems(homeViewModel)
                    }
                    if (showScheduleAdd) {
                        AddScheduleComponent(
                            homeViewModel = homeViewModel,
                            onDismiss = { showScheduleAdd = false }
                        )
                    }
                    if (helpMenu) {
                        HelpMenu {
                            helpMenu = false
                        }
                    }
                }
            }
        }
    }

}