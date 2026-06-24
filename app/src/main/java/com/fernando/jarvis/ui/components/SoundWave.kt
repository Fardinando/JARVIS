package com.fernando.jarvis.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.fernando.jarvis.ui.theme.JarvisColors
import kotlin.random.Random

@Composable
fun SoundWave(
    active: Boolean,
    color: Color = JarvisColors.Neon,
    barCount: Int = 32,
    height: Int = 50,
) {
    if (!active) {
        Spacer(Modifier.height(height.dp))
        return
    }

    val infiniteTransition = rememberInfiniteTransition(label = "wave")

    val barHeights = remember(barCount) {
        (0 until barCount).map { index ->
            infiniteTransition.animateFloat(
                initialValue = 0.1f,
                targetValue = Random.nextFloat() * 0.7f + 0.3f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = (Random.nextInt(100) + 150),
                        easing = LinearEasing,
                    ),
                    repeatMode = RepeatMode.Reverse,
                ),
                label = "bar_$index",
            )
        }
    }

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(height.dp)
    ) {
        val barWidth = size.width / barCount
        val maxHeight = size.height

        barHeights.forEachIndexed { index, animValue ->
            val barHeight = maxHeight * animValue
            val x = index * barWidth + 1
            drawRect(
                color = color.copy(alpha = if (active) 0.8f else 0.2f),
                topLeft = Offset(x, maxHeight - barHeight),
                size = androidx.compose.ui.geometry.Size(
                    width = barWidth - 2,
                    height = barHeight,
                ),
            )
        }
    }
}
