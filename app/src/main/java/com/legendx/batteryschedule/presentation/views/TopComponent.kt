package com.legendx.batteryschedule.presentation.views

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import com.legendx.batteryschedule.utils.FontConfig

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopComponent(
    title: String,
    icon: Painter?,
    iconClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = title, style = MaterialTheme.typography.titleLarge,
                fontFamily = FontConfig.lexend_deca
            )
        },
        actions = {
            if (icon != null) {
                IconButton(
                    onClick = iconClick,
                    modifier = Modifier
                        .size(50.dp)
                        .padding(end = 16.dp)
                ) {
                    Icon(
                        painter = icon,
                        contentDescription = "Help",
                    )
                }
            }
        }
    )
}