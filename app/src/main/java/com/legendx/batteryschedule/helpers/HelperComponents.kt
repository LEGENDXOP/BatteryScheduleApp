package com.legendx.batteryschedule.helpers


import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.legendx.batteryschedule.R
import com.legendx.batteryschedule.components.BatteryService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomScreen(showBottom: MutableState<Boolean>) {
    val bottomSheetState: SheetState = rememberModalBottomSheetState()
    val scope: CoroutineScope = rememberCoroutineScope()
    if (showBottom.value) {
        ModalBottomSheet(
            onDismissRequest = { showBottom.value = false },
            sheetState = bottomSheetState,
            containerColor = Color.White.copy(0.9f),
            tonalElevation = 12.dp,
            dragHandle = { DragScreen("Set Your Schedule") }
        ) {
            BottomSheetContent(
                bottomSheetState = bottomSheetState,
                scope = scope,
                showBottomSheet = showBottom
            )
        }
    } else {
        println("Hidden")
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DragScreen(text: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BottomSheetDefaults.DragHandle(color = Color.Black)
        Text(
            text = text,
            fontSize = 24.sp,
            fontFamily = FontFamily(Font(R.font.roboto_slab)),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Divider()
        Spacer(modifier = Modifier.height(4.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetContent(
    scope: CoroutineScope,
    bottomSheetState: SheetState,
    showBottomSheet: MutableState<Boolean>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(start = 16.dp, end = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val context = LocalContext.current
        DataManage.initialize(context)
        val screenHeight = LocalConfiguration.current.screenHeightDp.dp
        var sliderValue by remember { mutableFloatStateOf(0f) }
        var textValue by remember { mutableStateOf("") }
        TextField(
            value = textValue, onValueChange = { textValue = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp, start = 4.dp, end = 4.dp),
            label = { Text("Enter your schedule Message") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "icon on text field"
                )
            },
            maxLines = 3,
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.Black.copy(0.8f),
                focusedContainerColor = Color.Black.copy(0.8f),
                unfocusedIndicatorColor = Color.Red,
                focusedIndicatorColor = Color.Red,
                focusedLabelColor = Color.White.copy(0.8f),
                unfocusedLabelColor = Color.White.copy(0.8f),
            ),
            textStyle = TextStyle(
                color = Color.White,
                fontSize = 16.sp,
                fontFamily = FontFamily(Font(R.font.roboto_slab)),
                fontWeight = FontWeight.Normal
            )
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFFFB1F3),
            )
        ) {
            Slider(
                value = sliderValue, onValueChange = { sliderValue = it },
                steps = 100,
                valueRange = 0f..100f,
                colors = SliderDefaults.colors(
                    thumbColor = Color(0xFF5F33E1),
                    inactiveTrackColor = Color(0xFFFF77FA),
                    activeTrackColor = Color(0xFFFF4B4B)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, end = 8.dp),
            )
        }

        if (sliderValue.toInt() > 0) {
            Text(
                text = "Battery Level: ${sliderValue.toInt()}%",
                fontSize = 18.sp,
                fontFamily = FontFamily(Font(R.font.roboto_slab)),
                fontWeight = FontWeight.SemiBold,
                color = Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )

            FilledTonalButton(
                onClick = {
                    if (textValue == "") {
                        return@FilledTonalButton
                    }
                    val id = DataManage.getData("currentKey")
                    val scheduleData = ScheduleSet(id?.toInt() ?: 1, sliderValue.toInt(), textValue)
                    scope.launch {
                        HelperFunctions.saveKey()
                        HelperFunctions.saveSchedule(scheduleData)
                        bottomSheetState.hide()
                    }.invokeOnCompletion {
                        if (!bottomSheetState.isVisible) {
                            showBottomSheet.value = false
                        }
                    }
                    scope.launch {
                        val intentService =  Intent(context, BatteryService::class.java)
                        context.startService(intentService)
                    }
                    Toast.makeText(context, "Schedule Set Successfully", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .padding(top = 16.dp, start = 8.dp, end = 8.dp)
                    .fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                ),
                shape = RoundedCornerShape(12.dp),

                ) {
                Text(
                    text = "Save Schedule",
                    color = Color.White,
                    fontSize = 20.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(screenHeight * 0.8f))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewScheduleBottomSheet(showBottom: MutableState<Boolean>) {
    val sheetState: SheetState = rememberModalBottomSheetState()
    if (showBottom.value) {
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = { showBottom.value = false },
            containerColor = Color.White.copy(0.9f),
            tonalElevation = 12.dp,
            dragHandle = { DragScreen("View Your Schedule") }
        ) {
            ViewScheduleContent()
        }
    }
}


@Composable
fun ViewScheduleContent() {
    val allData = HelperFunctions.allSchedule()
    val screenHeight = LocalConfiguration.current.screenHeightDp
    LazyColumn {
        items(allData.size) { index ->
            DemoData(scheduleSet = allData[index])
            Divider(modifier = Modifier.padding(top = 8.dp, bottom = 8.dp))
        }
        item {
            Spacer(modifier = Modifier.height(screenHeight.dp * 0.8f))
        }
    }
}


@Composable
fun DemoData(scheduleSet: ScheduleSet) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp),

        ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle, contentDescription = "id icon",
                modifier = Modifier.size(32.dp)
            )
            Text(text = ": ${scheduleSet.id}", textAlign = TextAlign.Center, fontSize = 24.sp)
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${scheduleSet.batteryLevel} :",
                    textAlign = TextAlign.Center,
                    fontSize = 24.sp
                )
                Icon(
                    painter = painterResource(id = R.drawable.sharp_battery_charging_full),
                    contentDescription = "battery icon",
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        Column(
            modifier = Modifier
                .padding(top = 14.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Message 👇",
                fontSize = 24.sp,
                fontFamily = FontFamily(Font(R.font.roboto_slab)),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFB1F3),
                )
            ) {
                Text(
                    text = scheduleSet.scheduleMessage,
                    fontSize = 18.sp,
                    fontFamily = FontFamily(Font(R.font.roboto_slab)),
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF8000FF),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 8.dp)
                )
            }
        }
    }
}