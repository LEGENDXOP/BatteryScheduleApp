package com.legendx.batteryschedule.presentation.views

import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSizeIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.legendx.batteryschedule.data.services.BatteryWorker
import com.legendx.batteryschedule.presentation.viewmodels.HomeViewModel
import com.legendx.batteryschedule.utils.FontConfig
import com.legendx.batteryschedule.utils.Schedulers
import com.legendx.batteryschedule.utils.Throttler
import com.legendx.batteryschedule.utils.toast
import com.legendx.composerpro.customButtons.CustomOutlinedButton
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScheduleComponent(
    homeViewModel: HomeViewModel,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val threadThrottler = Throttler(5)
    var enableButton by remember { mutableStateOf(false) }
    var batteryValue by remember { mutableFloatStateOf(0f) }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = {},
    ) {
        val keyBoardController = LocalSoftwareKeyboardController.current
        val focusManager = LocalFocusManager.current
        Column(
            modifier = modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(70.dp))
            Text(
                "Add Schedule",
                style = MaterialTheme.typography.titleLarge,
                fontFamily = FontConfig.lexend_deca,
            )
            Spacer(Modifier.height(70.dp))
            OutlinedTextField(
                value = homeViewModel.textState,
                onValueChange = { homeViewModel.updateTextState(it) },
                label = { Text("Schedule Name", fontFamily = FontConfig.lexend_deca) },
                maxLines = 1,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
            )
            Spacer(Modifier.height(20.dp))
            OutlinedTextField(
                value = homeViewModel.descriptionState,
                onValueChange = { homeViewModel.updateDescriptionState(it) },
                label = { Text("Schedule Description", fontFamily = FontConfig.lexend_deca) },
                maxLines = 4
            )
            Spacer(Modifier.height(30.dp))
            OutlinedCard(
                modifier = Modifier.padding(horizontal = 40.dp),
                colors = CardDefaults.outlinedCardColors(
                    containerColor = Color.Transparent
                )
            ) {
                Text(
                    "Battery Percentage", fontFamily = FontConfig.lexend_deca,
                    fontWeight = FontWeight.Light,
                    modifier = Modifier.padding(12.dp)
                )
                LegacySlider {
                    focusManager.clearFocus()
                    keyBoardController?.hide()
                    batteryValue = it
                    enableButton = it != 0f
                }
            }
            Spacer(Modifier.height(20.dp))
            CustomOutlinedButton(
                haptics = true,
                enabled = enableButton,
                shape = RoundedCornerShape(10.dp),
                onClick = {
                    keyBoardController?.hide()
                    if (homeViewModel.checkForAdd()) {
                        homeViewModel.addSchedule(
                            title = homeViewModel.textState,
                            description = homeViewModel.descriptionState,
                            batteryPercentage = batteryValue.toInt()
                        )
                        coroutineScope.launch {
                            sheetState.hide()
                        }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                homeViewModel.resetTextStates()
                                onDismiss()
                                val workRequest = OneTimeWorkRequestBuilder<BatteryWorker>().build()
                                WorkManager.getInstance(context).enqueue(workRequest)
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
                                    coroutineScope.launch{
                                        threadThrottler.runOncePerInterval {
                                            Schedulers.scheduleExactAlarm(context)
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        "Please fill all the fields".toast(context)
                    }
                }
            ) {
                Text(
                    "Add Schedule",
                    fontFamily = FontConfig.lexend_deca,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LegacySlider(onChange: (Float) -> Unit) {
    var sliderPosition by remember { mutableFloatStateOf(0f) }
    val interactionSource = remember { MutableInteractionSource() }
    val trackHeight = 4.dp
    val thumbSize = DpSize(20.dp, 20.dp)
    LaunchedEffect(sliderPosition) {
        onChange(sliderPosition)
    }
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        AnimatedVisibility(
            visible = sliderPosition != 0f,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(
                text = "${sliderPosition.toInt()}%",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                textAlign = TextAlign.Center,
                fontFamily = FontConfig.lexend_deca,
                fontWeight = FontWeight.SemiBold
            )
        }
        Slider(
            interactionSource = interactionSource,
            modifier =
            Modifier
                .semantics { contentDescription = "Thumb Slider" }
                .requiredSizeIn(minWidth = thumbSize.width, minHeight = trackHeight),
            value = sliderPosition,
            onValueChange = { sliderPosition = it },
            valueRange = 0f..100f,
            thumb = {
                val modifier =
                    Modifier
                        .size(thumbSize)
                        .shadow(1.dp, CircleShape, clip = false)
                        .indication(
                            interactionSource = interactionSource,
                            indication = ripple(bounded = false, radius = 20.dp)
                        )
                SliderDefaults.Thumb(interactionSource = interactionSource, modifier = modifier)
            },
            track = {
                val modifier = Modifier.height(trackHeight)
                SliderDefaults.Track(
                    sliderState = it,
                    modifier = modifier,
                    thumbTrackGapSize = 0.dp,
                    trackInsideCornerSize = 0.dp,
                    drawStopIndicator = null
                )
            }
        )
    }
}