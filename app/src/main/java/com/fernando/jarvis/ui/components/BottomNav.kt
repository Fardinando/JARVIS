package com.fernando.jarvis.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fernando.jarvis.ui.theme.JarvisColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Settings

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
        NavButton(Icons.Default.Mic, "FALAR", onClick = onSpeak)
        NavButton(
            icon = Icons.Default.Chat,
            label = "CHAT",
            isCenter = true,
            onClick = onChat,
        )
        NavButton(Icons.Default.Settings, "CONFIG", onClick = onSettings)
    }
}

@Composable
private fun NavButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
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
        Icon(
            icon,
            contentDescription = label,
            tint = JarvisColors.TextPrimary,
            modifier = Modifier.size(if (isCenter) 24.dp else 20.dp),
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
