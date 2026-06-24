package com.fernando.jarvis.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fernando.jarvis.ui.theme.JarvisColors
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HudHeader(battery: Int) {
    val time = remember { mutableStateOf(Date()) }

    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(1000)
            time.value = Date()
        }
    }

    val timeStr = remember(time.value) {
        SimpleDateFormat("HH:mm:ss", Locale("pt", "BR")).format(time.value)
    }
    val dateStr = remember(time.value) {
        SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR")).format(time.value)
    }

    val batteryColor = when {
        battery > 60 -> JarvisColors.Success
        battery > 25 -> JarvisColors.Orange
        else -> JarvisColors.Red
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "SYS",
                    fontSize = 9.sp,
                    color = JarvisColors.TextMuted,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 2.sp,
                )
                Text(
                    timeStr,
                    fontSize = 14.sp,
                    color = JarvisColors.Neon,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    "JARVIS OS",
                    fontSize = 12.sp,
                    color = JarvisColors.TextPrimary,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 6.sp,
                    fontWeight = FontWeight.ExtraBold,
                )
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .background(JarvisColors.Success, shape = androidx.compose.foundation.shape.CircleShape)
                        .padding(top = 4.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.End,
            ) {
                Text(
                    "BAT",
                    fontSize = 9.sp,
                    color = JarvisColors.TextMuted,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 2.sp,
                )
                Text(
                    "$battery%",
                    fontSize = 14.sp,
                    color = batteryColor,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(JarvisColors.CardBorder)
                .padding(vertical = 6.dp)
        )

        Text(
            dateStr,
            fontSize = 9.sp,
            color = JarvisColors.TextMuted,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.fillMaxWidth(),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        )
    }
}
