
package com.pzx.downloader.widget

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun NumberProgressBar(
    progress: Int,
    max: Int = 100,
    modifier: Modifier = Modifier,
    reachedColor: Color = Color(0xFF4291F1),
    unreachedColor: Color = Color(0xFFCCCCCC),
    textColor: Color = Color(0xFF4291F1),
    textSize: TextUnit = 12.sp,
    reachedHeight: Dp = 3.dp,
    unreachedHeight: Dp = 2.dp,
    suffix: String = "%",
    prefix: String = "",
    showProgressText: Boolean = true,
    onProgressChange: ((current: Int, max: Int) -> Unit)? = null
) {
    val textMeasurer = rememberTextMeasurer()
    var drawTextWidth by remember { mutableStateOf(0f) }

    // 计算进度百分比
    val progressPercent = if (max > 0) {
        (progress.toFloat() / max * 100).toInt()
    } else {
        0
    }

    val currentText = if (showProgressText) {
        "$prefix$progressPercent$suffix"
    } else {
        ""
    }

    // 测量文本宽度
    LaunchedEffect(currentText) {
        if (showProgressText) {
            val textLayoutResult = textMeasurer.measure(
                text = currentText,
                style = TextStyle(
                    fontSize = textSize,
                    color = textColor
                )
            )
            drawTextWidth = textLayoutResult.size.width.toFloat()
        }
    }

    // 回调通知进度变化
    LaunchedEffect(progress, max) {
        onProgressChange?.invoke(progress, max)
    }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(30.dp)
    ) {
        drawProgressBar(
            progress = progress,
            max = max,
            reachedColor = reachedColor,
            unreachedColor = unreachedColor,
            reachedHeight = reachedHeight,
            unreachedHeight = unreachedHeight,
            currentText = currentText,
            textColor = textColor,
            textSize = textSize,
            showProgressText = showProgressText,
            drawTextWidth = drawTextWidth
        )
    }
}

private fun DrawScope.drawProgressBar(
    progress: Int,
    max: Int,
    reachedColor: Color,
    unreachedColor: Color,
    reachedHeight: Dp,
    unreachedHeight: Dp,
    currentText: String,
    textColor: Color,
    textSize: TextUnit,
    showProgressText: Boolean,
    drawTextWidth: Float
) {
    val canvasWidth = size.width
    val canvasHeight = size.height

    // 计算进度条位置
    val reachedRatio = if (max > 0) progress.toFloat() / max else 0f
    val reachedWidth = if (showProgressText) {
        (canvasWidth - drawTextWidth - 20f) * reachedRatio
    } else {
        canvasWidth * reachedRatio
    }

    val centerY = canvasHeight / 2

    // 绘制未完成部分
    drawRect(
        color = unreachedColor,
        topLeft = Offset(
            x = if (showProgressText) drawTextWidth + 10f else 0f,
            y = centerY - unreachedHeight.toPx() / 2
        ),
        size = Size(
            width = if (showProgressText) canvasWidth - drawTextWidth - 10f else canvasWidth,
            height = unreachedHeight.toPx()
        )
    )

    // 绘制已完成部分
    if (progress > 0) {
        drawRect(
            color = reachedColor,
            topLeft = Offset(
                x = if (showProgressText) drawTextWidth + 10f else 0f,
                y = centerY - reachedHeight.toPx() / 2
            ),
            size = Size(
                width = reachedWidth,
                height = reachedHeight.toPx()
            )
        )
    }

    // 绘制文本
    if (showProgressText) {
        drawContext.canvas.nativeCanvas.drawText(
            currentText,
            0f,
            centerY - (android.graphics.Paint().also { paint ->
                paint.textSize = textSize.toPx()
                paint.color = textColor.toArgb()
            }.descent() + android.graphics.Paint().also { paint ->
                paint.textSize = textSize.toPx()
                paint.color = textColor.toArgb()
            }.ascent()) / 2,
            android.graphics.Paint().apply {
                this.textSize = textSize.toPx()
                color = textColor.toArgb()
                isAntiAlias = true
            }
        )
    }
}

// 带自动递增功能的进度条
@Composable
fun AutoIncrementProgressBar(
    max: Int = 100,
    modifier: Modifier = Modifier,
    reachedColor: Color = Color(0xFF4291F1),
    unreachedColor: Color = Color(0xFFCCCCCC),
    textColor: Color = Color(0xFF4291F1),
    textSize: TextUnit = 12.sp,
    reachedHeight: Dp = 3.dp,
    unreachedHeight: Dp = 2.dp,
    suffix: String = "%",
    prefix: String = "",
    showProgressText: Boolean = true,
    incrementBy: Int = 1,
    delayMillis: Long = 100L,
    onProgressChange: ((current: Int, max: Int) -> Unit)? = null
) {
    var progress by remember { mutableStateOf(0) }

    NumberProgressBar(
        progress = progress,
        max = max,
        modifier = modifier,
        reachedColor = reachedColor,
        unreachedColor = unreachedColor,
        textColor = textColor,
        textSize = textSize,
        reachedHeight = reachedHeight,
        unreachedHeight = unreachedHeight,
        suffix = suffix,
        prefix = prefix,
        showProgressText = showProgressText,
        onProgressChange = onProgressChange
    )

    // 自动递增进度
    LaunchedEffect(Unit) {
        while (progress < max) {
            delay(delayMillis)
            progress += incrementBy
            if (progress > max) progress = max
        }
    }
}
