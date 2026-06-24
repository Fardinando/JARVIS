package com.fernando.jarvis.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fernando.jarvis.ui.components.HudHeader
import com.fernando.jarvis.ui.components.OrbView
import com.fernando.jarvis.ui.components.SoundWave
import com.fernando.jarvis.ui.components.StatusGrid
import com.fernando.jarvis.ui.components.BottomNav
import com.fernando.jarvis.ui.theme.JarvisColors
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MainScreen(
    orbState: String,
    battery: Int,
    lastReply: String,
    onOrbPress: () -> Unit,
    onNavigateToChat: () -> Unit,
    onNavigateToSettings: () -> Unit,
) {
    val time = remember { mutableStateOf(Date()) }
    val dateFormat = remember { SimpleDateFormat("HH:mm:ss", Locale("pt", "BR")) }

    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(1000)
            time.value = Date()
        }
    }

    val greeting = remember(time.value) {
        val h = time.value.hours
        when {
            h < 12 -> "Bom dia"
            h < 18 -> "Boa tarde"
            else -> "Boa noite"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(JarvisColors.Deep)
    ) {
        HudHeader(battery = battery)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(16.dp))

            Text(
                text = "$greeting, Fernando",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = JarvisColors.TextPrimary,
                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
            )

            Text(
                text = "Todos os sistemas operacionais.",
                fontSize = 12.sp,
                color = JarvisColors.TextSecondary,
                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                modifier = Modifier.padding(top = 4.dp),
            )

            SoundWave(
                active = orbState == "speaking" || orbState == "listening",
                color = if (orbState == "listening") JarvisColors.Energy else JarvisColors.Neon,
            )

            Spacer(Modifier.height(16.dp))

            OrbView(
                state = orbState,
                onPress = onOrbPress,
                size = 160,
            )

            if (lastReply.isNotEmpty()) {
                Spacer(Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            JarvisColors.Card,
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                        )
                        .padding(12.dp)
                ) {
                    Column {
                        Text(
                            text = "ULTIMA RESPOSTA",
                            fontSize = 9.sp,
                            color = JarvisColors.Neon,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                            letterSpacing = 2.sp,
                        )
                        Text(
                            text = lastReply,
                            fontSize = 13.sp,
                            color = JarvisColors.TextPrimary,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                            modifier = Modifier.padding(top = 6.dp),
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            StatusGrid()

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(JarvisColors.Card, shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                InfoItem("DATA", java.text.SimpleDateFormat("dd/MM", Locale("pt", "BR")).format(time.value))
                Divider()
                InfoItem("HORA", dateFormat.format(time.value))
                Divider()
                val batteryColor = when {
                    battery > 60 -> JarvisColors.Success
                    battery > 25 -> JarvisColors.Orange
                    else -> JarvisColors.Red
                }
                InfoItem("BATERIA", "$battery%", batteryColor)
            }

            Spacer(Modifier.height(24.dp))
        }

        BottomNav(
            onChat = onNavigateToChat,
            onSettings = onNavigateToSettings,
            onSpeak = onOrbPress,
        )
    }
}

@Composable
private fun InfoItem(label: String, value: String, color: androidx.compose.ui.graphics.Color = JarvisColors.Neon) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            fontSize = 8.sp,
            color = JarvisColors.TextMuted,
            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
            letterSpacing = 2.sp,
        )
        Text(
            text = value,
            fontSize = 14.sp,
            color = color,
            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun Divider() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(30.dp)
            .background(JarvisColors.CardBorder)
    )
}
