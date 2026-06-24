package com.fernando.jarvis.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fernando.jarvis.ui.components.SoundWave
import com.fernando.jarvis.ui.theme.JarvisColors
import kotlinx.coroutines.launch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send

data class ChatMessage(
    val id: Long,
    val text: String,
    val isUser: Boolean,
    val isError: Boolean = false,
)

@Composable
fun ChatScreen(
    onSend: (String) -> String,
    onBack: () -> Unit,
) {
    val messages = remember { mutableStateListOf<ChatMessage>() }
    var input by remember { mutableStateOf("") }
    var isTyping by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
    var idCounter = remember { 1L }

    LaunchedEffect(Unit) {
        messages.add(
            ChatMessage(
                id = 0,
                text = "JARVIS TERMINAL v4.0\nDigite um comando ou converse com a IA.",
                isUser = false,
            )
        )
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            scope.launch { listState.animateScrollToItem(messages.lastIndex) }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(JarvisColors.Deep)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Voltar",
                    tint = JarvisColors.Neon,
                )
            }
            Text(
                text = "TERMINAL",
                fontSize = 12.sp,
                color = JarvisColors.TextPrimary,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 4.sp,
                fontWeight = FontWeight.Bold,
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(JarvisColors.CardBorder)
                .padding(horizontal = 16.dp)
        )

        SoundWave(
            active = isTyping,
            color = JarvisColors.Purple,
        )

        // Messages
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(messages, key = { it.id }) { msg ->
                MessageBubble(msg)
            }
        }

        // Typing indicator
        if (isTyping) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(JarvisColors.Purple, shape = RoundedCornerShape(3.dp))
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "JARVIS processando...",
                    fontSize = 10.sp,
                    color = JarvisColors.Purple,
                    fontFamily = FontFamily.Monospace,
                )
            }
        }

        // Input
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(JarvisColors.Space)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextField(
                value = input,
                onValueChange = { input = it },
                placeholder = {
                    Text(
                        "Digite seu comando...",
                        color = JarvisColors.TextMuted,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp,
                    )
                },
                modifier = Modifier.weight(1f),
                textStyle = androidx.compose.ui.text.TextStyle(
                    color = JarvisColors.TextPrimary,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 13.sp,
                ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = JarvisColors.Card,
                    unfocusedContainerColor = JarvisColors.Card,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = JarvisColors.Neon,
                ),
                shape = RoundedCornerShape(8.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(
                    onSend = {
                        if (input.isNotBlank() && !isTyping) {
                            val text = input.trim()
                            input = ""
                            keyboardController?.hide()

                            messages.add(
                                ChatMessage(idCounter++, text, true)
                            )

                            isTyping = true
                            val reply = onSend(text)
                            isTyping = false

                            messages.add(
                                ChatMessage(idCounter++, reply, false)
                            )
                        }
                    }
                ),
                singleLine = true,
            )

            Spacer(Modifier.width(8.dp))

            IconButton(
                onClick = {
                    if (input.isNotBlank() && !isTyping) {
                        val text = input.trim()
                        input = ""
                        keyboardController?.hide()

                        messages.add(
                            ChatMessage(idCounter++, text, true)
                        )

                        isTyping = true
                        val reply = onSend(text)
                        isTyping = false

                        messages.add(
                            ChatMessage(idCounter++, reply, false)
                        )
                    }
                },
                modifier = Modifier
                    .background(JarvisColors.Neon, RoundedCornerShape(8.dp))
                    .padding(4.dp),
            ) {
                Icon(
                    Icons.Default.Send,
                    contentDescription = "Enviar",
                    tint = JarvisColors.Deep,
                )
            }
        }
    }
}

@Composable
private fun MessageBubble(msg: ChatMessage) {
    val isUser = msg.isUser
    val isError = msg.isError
    val isSystem = msg.id == 0L

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
    ) {
        if (!isUser && !isSystem) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(JarvisColors.NeonGlow, RoundedCornerShape(14.dp))
                    .padding(2.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    "J",
                    color = JarvisColors.Neon,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                )
            }
            Spacer(Modifier.width(8.dp))
        }

        Column(
            modifier = Modifier
                .widthIn(max = if (isSystem) 320.dp else 260.dp)
                .background(
                    when {
                        isSystem -> Color.Transparent
                        isError -> JarvisColors.RedGlow
                        isUser -> JarvisColors.NeonGlow
                        else -> JarvisColors.Card
                    },
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(10.dp)
        ) {
            if (!isSystem) {
                Text(
                    text = if (isUser) "VOCE" else "JARVIS",
                    fontSize = 8.sp,
                    color = if (isUser) JarvisColors.Energy else JarvisColors.Neon,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 2.sp,
                )
                Spacer(Modifier.height(4.dp))
            }
            Text(
                text = msg.text,
                fontSize = 13.sp,
                color = if (isError) JarvisColors.Red else JarvisColors.TextPrimary,
                fontFamily = FontFamily.Monospace,
                lineHeight = 18.sp,
            )
        }
    }
}
