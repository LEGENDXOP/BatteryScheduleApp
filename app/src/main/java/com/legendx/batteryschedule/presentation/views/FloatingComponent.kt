package com.legendx.batteryschedule.presentation.views

import androidx.compose.foundation.layout.size
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.legendx.batteryschedule.R
import com.legendx.batteryschedule.utils.ColorConfig

@Composable
fun FloatingComponent(onClick: () -> Unit) {
    FloatingActionButton(
        onClick = onClick,
        shape = FloatingActionButtonDefaults.largeShape,
        containerColor = ColorConfig.mainColor,
        contentColor = Color.White
    ) {
        Icon(
            painter = painterResource(R.drawable.add_circle),
            contentDescription = "Add new battery schedule",
            modifier = Modifier.size(24.dp)
        )
    }
}