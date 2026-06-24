package com.fernando.jarvis.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fernando.jarvis.ui.theme.JarvisColors
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun OrbView(
    state: String,
    onPress: () -> Unit,
    size: Int = 140,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "orb")

    val pulse by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = when (state) {
            "idle" -> 1.06f
            "listening" -> 1.15f
            "speaking" -> 1.08f
            "processing" -> 0.95f
            "error" -> 1.02f
            else -> 1f
        },
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = when (state) {
                    "idle" -> 2000
                    "listening" -> 800
                    "speaking" -> 600
                    "processing" -> 2000
                    "error" -> 300
                    else -> 1500
                },
                easing = LinearEasing,
            ),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "pulse",
    )

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = when (state) {
            "error" -> 0.8f
            else -> 0.3f
        },
        targetValue = when (state) {
            "error" -> 0.2f
            else -> 0.6f
        },
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = when (state) {
                    "idle" -> 2000
                    "listening" -> 800
                    "processing" -> 1500
                    "error" -> 300
                    else -> 1000
                },
                easing = LinearEasing,
            ),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "glow",
    )

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "rotation",
    )

    val stateConfig = when (state) {
        "idle" -> JarvisColors.Neon to "JARVIS ONLINE"
        "listening" -> JarvisColors.Energy to "OUVINDO..."
        "processing" -> JarvisColors.Purple to "PROCESSANDO..."
        "speaking" -> JarvisColors.Neon to "FALANDO..."
        "error" -> JarvisColors.Red to "ERRO"
        "executing" -> JarvisColors.Success to "EXECUTANDO..."
        else -> JarvisColors.Neon to "JARVIS ONLINE"
    }

    val color = stateConfig.first
    val label = stateConfig.second

    Column(
        modifier = Modifier.clickable { onPress() },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier.size(size.dp),
            contentAlignment = Alignment.Center,
        ) {
            // Outer glow
            Box(
                modifier = Modifier
                    .size((size + 40).dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = glowAlpha * 0.5f))
            )

            // Processing ring
            if (state == "processing") {
                Canvas(modifier = Modifier.size((size + 20).dp)) {
                    val angle = rotation * kotlin.math.PI / 180f
                    drawArc(
                        brush = Brush.sweepGradient(
                            listOf(color, color.copy(alpha = 0.3f), color),
                        ),
                        startAngle = rotation,
                        sweepAngle = 270f,
                        useCenter = false,
                        style = Stroke(width = 2.dp.toPx()),
                    )
                }
            }

            // Main orb
            Box(
                modifier = Modifier
                    .size(size.dp)
                    .scale(pulse)
                    .clip(CircleShape)
                    .background(JarvisColors.Deep)
                    .border(2.dp, color, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val cx = size.width / 2
                    val cy = size.height / 2
                    val r = size.minDimension / 4

                    // Inner glow gradient
                    drawCircle(
                        brush = Brush.radialGradient(
                            listOf(color.copy(alpha = 0.15f), Color.Transparent),
                            radius = r,
                        ),
                        radius = r,
                        center = Offset(cx, cy),
                    )
                }

                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(color)
                )
            }
        }

        Text(
            text = label,
            fontSize = 11.sp,
            color = color,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 3.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 16.dp),
        )
    }
}
