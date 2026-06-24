package com.fernando.jarvis.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fernando.jarvis.JarvisEngines
import com.fernando.jarvis.ui.theme.JarvisColors
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    engines: JarvisEngines,
    onBack: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    var apiKey by remember { mutableStateOf("") }
    var saved by remember { mutableStateOf(false) }
    var testing by remember { mutableStateOf(false) }
    var testResult by remember { mutableStateOf<String?>(null) }
    var ttsEnabled by remember { mutableStateOf(true) }
    var showKey by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(JarvisColors.Deep)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextButton(onClick = onBack) {
                Text(
                    "\u2190 VOLTAR",
                    color = JarvisColors.Neon,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp,
                )
            }
            Spacer(Modifier.weight(1f))
            Text(
                text = "CONFIGURACOES",
                fontSize = 12.sp,
                color = JarvisColors.TextPrimary,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 4.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.weight(1f))
            Spacer(Modifier.width(60.dp))
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(JarvisColors.CardBorder)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
        ) {
            Text(
                text = "OPENROUTER API KEY",
                fontSize = 10.sp,
                color = JarvisColors.Neon,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 3.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "Chave para acessar a IA.\nObtenha em openrouter.ai/keys",
                fontSize = 11.sp,
                color = JarvisColors.TextSecondary,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(top = 4.dp, bottom = 12.dp),
            )

            OutlinedTextField(
                value = apiKey,
                onValueChange = { apiKey = it; saved = false; testResult = null },
                placeholder = {
                    Text(
                        "sk-or-v1-...",
                        color = JarvisColors.TextMuted,
                        fontFamily = FontFamily.Monospace,
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                textStyle = androidx.compose.ui.text.TextStyle(
                    color = JarvisColors.TextPrimary,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 13.sp,
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = JarvisColors.Neon,
                    unfocusedBorderColor = JarvisColors.CardBorder,
                    focusedContainerColor = JarvisColors.Card,
                    unfocusedContainerColor = JarvisColors.Card,
                    cursorColor = JarvisColors.Neon,
                ),
                shape = RoundedCornerShape(8.dp),
                singleLine = true,
                visualTransformation = if (showKey) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    TextButton(onClick = { showKey = !showKey }) {
                        Text(
                            if (showKey) "OCULTAR" else "MOSTRAR",
                            color = JarvisColors.TextMuted,
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace,
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
            )

            Spacer(Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = {
                        scope.launch {
                            saved = true
                            kotlinx.coroutines.delay(2000)
                            saved = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = JarvisColors.Neon),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                ) {
                    Text(
                        if (saved) "SALVO!" else "SALVAR",
                        color = JarvisColors.Deep,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                    )
                }

                OutlinedButton(
                    onClick = {
                        testing = true
                        testResult = null
                        scope.launch {
                            kotlinx.coroutines.delay(1000)
                            testResult = if (apiKey.startsWith("sk-or-")) "success" else "error"
                            testing = false
                        }
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = JarvisColors.Neon),
                    enabled = !testing,
                ) {
                    if (testing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = JarvisColors.Neon,
                            strokeWidth = 2.dp,
                        )
                    } else {
                        Text(
                            when (testResult) {
                                "success" -> "VALIDA!"
                                "error" -> "INVALIDA"
                                else -> "TESTAR"
                            },
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                        )
                    }
                }
            }

            if (testResult == "success") {
                Text(
                    "Chave valida.",
                    color = JarvisColors.Success,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(top = 8.dp),
                )
            } else if (testResult == "error") {
                Text(
                    "Chave invalida ou sem conexao.",
                    color = JarvisColors.Red,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = "VOZ (TTS)",
                fontSize = 10.sp,
                color = JarvisColors.Neon,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 3.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp),
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { ttsEnabled = !ttsEnabled }
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Box(
                    modifier = Modifier
                        .width(48.dp)
                        .height(26.dp)
                        .background(
                            if (ttsEnabled) JarvisColors.NeonGlow else JarvisColors.Card,
                            RoundedCornerShape(13.dp)
                        )
                        .padding(horizontal = 3.dp),
                    contentAlignment = if (ttsEnabled) Alignment.CenterEnd else Alignment.CenterStart,
                ) {
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .background(
                                if (ttsEnabled) JarvisColors.Neon else JarvisColors.TextMuted,
                                RoundedCornerShape(9.dp)
                            )
                    )
                }
                Text(
                    if (ttsEnabled) "ATIVADO" else "DESATIVADO",
                    color = JarvisColors.TextPrimary,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = "COMANDOS LOCAIS",
                fontSize = 10.sp,
                color = JarvisColors.Neon,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 3.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp),
            )

            val commands = listOf(
                "\"hora\"" to "Hora atual",
                "\"dia\" / \"data\"" to "Data atual",
                "\"bateria\"" to "Nivel de bateria",
                "\"ajuda\"" to "Lista de comandos",
                "\"obrigado\"" to "Agradecer",
            )

            commands.forEach { (cmd, desc) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        cmd,
                        color = JarvisColors.Neon,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                    )
                    Text(
                        desc,
                        color = JarvisColors.TextSecondary,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = "SISTEMA",
                fontSize = 10.sp,
                color = JarvisColors.Neon,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 3.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp),
            )

            listOf(
                "VERSAO" to "4.0.0",
                "MODELO IA" to "Gemma 4 / Nemotron / Llama",
                "PROVEDOR" to "OpenRouter",
                "LINGUAGEM" to "Kotlin Nativo",
            ).forEach { (label, value) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        label,
                        color = JarvisColors.TextMuted,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                    )
                    Text(
                        value,
                        color = JarvisColors.TextPrimary,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                    )
                }
            }
        }
    }
}
