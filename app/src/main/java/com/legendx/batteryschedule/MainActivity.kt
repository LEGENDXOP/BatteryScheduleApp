package com.legendx.batteryschedule


import android.Manifest
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.legendx.batteryschedule.components.BatteryService
import com.legendx.batteryschedule.components.HomeActivity
import com.legendx.batteryschedule.helpers.DataManage
import com.legendx.batteryschedule.helpers.HelperFunctions
import com.legendx.batteryschedule.ui.theme.BatteryScheduleTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BatteryScheduleTheme {
               MainScreen()
            }
        }
    }
}

const val CHANNEL_ID = "BatterySchedule"
// idhar test kra me ab
fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Battery Schedule",
            importance
        )
        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }
}

@Composable
fun MainScreen(){
    val context = LocalContext.current
    val requestPermission = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted){
            println("Permission Granted")
        }else{
            Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }
        SideEffect {
            DataManage.initialize(context)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
            createNotificationChannel(context)
            val allDataSize = HelperFunctions.allSchedule().size
            Log.d("Data", "All Data Size: $allDataSize")
            if (allDataSize > 0){
                val serviceIntent = Intent(context, BatteryService::class.java)
                context.startService(serviceIntent)
            }else{
                val serviceIntent = Intent(context, BatteryService::class.java)
                context.stopService(serviceIntent)
            }

        }

    Box {
        Image(painter = painterResource(id = R.drawable.background),
            contentDescription = "Background Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        MainViews()
    }
}

@Composable
fun MainViews(){
    val context = LocalContext.current
    val activity = (context as? Activity)
    DataManage.initialize(context)
    val shortDescription: String = """
        This is a simple app that helps you to schedule your Notification based on your battery percentage.
        You can set the message and the battery percentage at which you want to get notified.
    """.trimIndent()
    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (textMain, textDescription, button) = createRefs()
        val (view, icon) = createRefs()
        val screenHeight = LocalConfiguration.current.screenHeightDp
        val guideline = createGuidelineFromTop((screenHeight * 0.6).dp)
        val robotoNormal = FontFamily(
            Font(R.font.roboto_slab)
        )
        Text(
            text = "Battery Schedule",
            style = MaterialTheme.typography.titleMedium,
            color = Color.Black,
            fontSize = 24.sp,
            textAlign = TextAlign.Center,
            fontFamily = robotoNormal,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .constrainAs(textMain){
                    top.linkTo(guideline)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )
        Text(
            text = shortDescription,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Black,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            fontFamily = robotoNormal,
            modifier = Modifier
                .constrainAs(textDescription){
                    top.linkTo(textMain.bottom, margin = 24.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .constrainAs(button) {
                    top.linkTo(textDescription.bottom, margin = 24.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFF5F33E1))
                .clickable {
                    val intent = Intent(context, HomeActivity::class.java)
                    activity?.startActivity(intent)
                    activity?.finish()
                }
                .padding(horizontal = 16.dp),

        ){
           Row (modifier = Modifier.fillMaxWidth(),
               horizontalArrangement = Arrangement.Center,
               verticalAlignment = Alignment.CenterVertically){
               ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
                   Text(
                       text = "Get Started",
                       style = MaterialTheme.typography.bodyMedium,
                       color = Color.White,
                       fontSize = 16.sp,
                       textAlign = TextAlign.Center,
                       fontFamily = robotoNormal,
                          modifier = Modifier
                            .constrainAs(view){
                                top.linkTo(parent.top)
                                bottom.linkTo(parent.bottom)
                                start.linkTo(parent.start)
                                end.linkTo(icon.end)
                            }
                   )
                   Spacer(modifier = Modifier.height(50.dp))
                   Icon(imageVector = Icons.Outlined.ArrowForward, contentDescription = "Arrow Forward",
                       tint = Color.White,
                       modifier = Modifier
                           .constrainAs(icon) {
                               top.linkTo(parent.top)
                               bottom.linkTo(parent.bottom)
                               end.linkTo(parent.end)
                           }
                           .size(34.dp)
                     )
               }
           }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun MainScreenPreview(){
    MainScreen()
}


