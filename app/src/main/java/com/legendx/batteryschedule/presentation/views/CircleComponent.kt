package com.legendx.batteryschedule.presentation.views

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.legendx.batteryschedule.utils.FontConfig


@Composable
fun CircleComponent(
    text: String,
    textColor: Color,
    mainColor: Color,
    percentage: Int,
    size: Dp,
    modifier: Modifier = Modifier,
    textSize: TextUnit = 16.sp,
    strokeWith: Dp = 8.dp,
    strokeColor: Color? = null
) {
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val sweepAngle = (percentage.toFloat() / 100) * 360

            drawArc(
                color = mainColor,
                startAngle = 0f,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(
                    width = strokeWith.toPx(),
                    cap = StrokeCap.Round
                )
            )

            drawArc(
                color = strokeColor ?: mainColor.copy(alpha = 0.3f),
                startAngle = sweepAngle,
                sweepAngle = 360 - sweepAngle,
                useCenter = false,
                style = Stroke(width = strokeWith.toPx())
            )
        }

        Text(
            text = text,
            color = textColor,
            fontSize = textSize,
            textAlign = TextAlign.Center,
            fontFamily = FontConfig.lexend_deca
        )
    }
}
