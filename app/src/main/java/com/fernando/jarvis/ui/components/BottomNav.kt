package com.fernando.jarvis.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fernando.jarvis.ui.theme.JarvisColors

@Composable
fun BottomNav(
    onChat: () -> Unit,
    onSettings: () -> Unit,
    onSpeak: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(JarvisColors.Space)
            .padding(bottom = 12.dp, top = 10.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        NavButton("\uD83C\uDF99\uFE0F", "FALAR", onClick = onSpeak)
        NavButton("\uD83D\uDCAC", "CHAT", isCenter = true, onClick = onChat)
        NavButton("\u2699\uFE0F", "CONFIG", onClick = onSettings)
    }
}

@Composable
private fun NavButton(
    icon: String,
    label: String,
    isCenter: Boolean = false,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .then(
                if (isCenter) Modifier
                    .background(JarvisColors.NeonGlow, RoundedCornerShape(12.dp))
                    .padding(horizontal = 24.dp, vertical = 10.dp)
                else Modifier.padding(8.dp)
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            icon,
            fontSize = if (isCenter) 24.sp else 20.sp,
            textAlign = TextAlign.Center,
        )
        Text(
            label,
            fontSize = 9.sp,
            color = JarvisColors.TextSecondary,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 2.sp,
            modifier = Modifier.padding(top = 4.dp),
        )
    }
}
