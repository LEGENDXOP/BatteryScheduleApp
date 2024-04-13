package com.legendx.batteryschedule.components

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.legendx.batteryschedule.R
import com.legendx.batteryschedule.components.ui.theme.BatteryScheduleTheme
import com.legendx.batteryschedule.helpers.BottomScreen
import com.legendx.batteryschedule.helpers.ViewScheduleBottomSheet

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BatteryScheduleTheme {
                HomeScreen()
            }
        }
    }
}

@Composable
fun HomeScreen() {
    Box {
        Image(
            painter = painterResource(id = R.drawable.background_raw),
            contentDescription = "Background Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        HomeScreenViews()
    }
}

@Composable
fun HomeScreenViews() {
    Column(modifier = Modifier.fillMaxSize()) {
        TopBarOfApp()
    }
}

@Composable
fun TopBarOfApp() {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (topCard) = createRefs()
        val guidelineTop = createGuidelineFromTop(screenHeight * 0.1f)
        val guidelineMid = createGuidelineFromTop(screenHeight * 0.4f)
        ElevatedCard(
            modifier = Modifier
                .width(screenWidth * 0.9f)
                .height(screenHeight * 0.25f)
                .constrainAs(topCard) {
                    top.linkTo(guidelineTop)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(guidelineMid)
                },
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF5F33E1),
            )
        ) {
            Text(
                text = "Battery Schedule",
                color = Color.White,
                fontSize = 24.sp,
                modifier = Modifier
                    .padding(top = 12.dp, bottom = 12.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontFamily = FontFamily.Cursive
            )
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                val setScheduleBottomSheet = remember { mutableStateOf(false) }
                BottomScreen(showBottom = setScheduleBottomSheet)
                FilledTonalButton(
                    onClick = {
                        setScheduleBottomSheet.value = true
                    },
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFEEE9FF),
                    ),
                    shape = RoundedCornerShape(12.dp),

                    ) {
                    Text(
                        text = "Set Schedule",
                        color = Color(0xFF5F33E1),
                        fontSize = 12.sp
                    )
                }
                val viewScheduleBottomSheet = remember { mutableStateOf(false) }
                ViewScheduleBottomSheet(showBottom = viewScheduleBottomSheet)
                FilledTonalButton(
                    onClick = {

                        viewScheduleBottomSheet.value = true
                    },
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFEEE9FF),
                    ),
                    shape = RoundedCornerShape(12.dp),

                    ) {
                    Text(
                        text = "View Schedules",
                        color = Color(0xFF5F33E1),
                        fontSize = 12.sp
                    )
                }
            }
        }
//        MiddleAndBottom(hideLock)
    }
}

/*
@Composable
fun MiddleAndBottom(hideIcons: MutableState<Boolean>) {
    ConstraintLayout {
        val (lockView) = createRefs()
        val screenHeight = LocalConfiguration.current.screenHeightDp.dp
        val guidelineMid = createGuidelineFromTop(screenHeight * 0.4f)
        Column(
            modifier = Modifier
                .constrainAs(lockView) {
                    top.linkTo(guidelineMid)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                }
                .fillMaxWidth()
                .height(screenHeight * 0.4f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (!hideIcons.value) {
                Row(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { Authentication.leftPassword() },
                        modifier = Modifier
                            .size(80.dp)
                            .padding(start = 16.dp),

                        ) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_lock_outline),
                            contentDescription = "lock",
                            tint = Color(0xFF5F33E1)
                        )
                    }
                    IconButton(
                        onClick = { Authentication.rightPassword() },
                        modifier = Modifier
                            .size(80.dp)
                            .padding(end = 16.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_lock_open),
                            contentDescription = "unlock",
                            tint = Color(0xFF5F33E1),
                        )
                    }
                }
            } else {
                println("Hidden")
            }
        }
    }
}
*/

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}
