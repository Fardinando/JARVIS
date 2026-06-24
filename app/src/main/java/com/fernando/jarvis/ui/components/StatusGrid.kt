package com.fernando.jarvis.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fernando.jarvis.ui.theme.JarvisColors

data class StatusItem(
    val label: String,
    val value: String,
    val color: androidx.compose.ui.graphics.Color,
)

@Composable
fun StatusGrid() {
    val items = listOf(
        StatusItem("SISTEMA", "ATIVO", JarvisColors.Success),
        StatusItem("IA ENGINE", "ONLINE", JarvisColors.Neon),
        StatusItem("NOTIFICACOES", "MONITORANDO", JarvisColors.Neon),
        StatusItem("MEMORIA", "ATIVO", JarvisColors.Purple),
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(JarvisColors.Card, shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        items.forEach { item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(item.color, shape = androidx.compose.foundation.shape.CircleShape)
                )
                Spacer(Modifier.width(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        item.label,
                        fontSize = 10.sp,
                        color = JarvisColors.TextSecondary,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp,
                    )
                    Text(
                        item.value,
                        fontSize = 10.sp,
                        color = item.color,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                    )
                }
            }
            if (item != items.last()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(JarvisColors.CardBorder)
                )
            }
        }
    }
}
