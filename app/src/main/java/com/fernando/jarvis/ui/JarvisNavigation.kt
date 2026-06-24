package com.fernando.jarvis.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.fernando.jarvis.JarvisEngines
import com.fernando.jarvis.ai.AIResponse
import com.fernando.jarvis.engine.ActionResult
import com.fernando.jarvis.ui.screens.ChatScreen
import com.fernando.jarvis.ui.screens.MainScreen
import com.fernando.jarvis.ui.screens.SettingsScreen

sealed class Screen(val route: String) {
    data object Main : Screen("main")
    data object Chat : Screen("chat")
    data object Settings : Screen("settings")
}

@Composable
fun JarvisNavigation(engines: JarvisEngines) {
    val navController = rememberNavController()
    var orbState by remember { mutableStateOf("idle") }
    var lastReply by remember { mutableStateOf("") }
    var battery by remember { mutableStateOf(100) }

    NavHost(navController = navController, startDestination = Screen.Main.route) {
        composable(Screen.Main.route) {
            MainScreen(
                orbState = orbState,
                battery = battery,
                lastReply = lastReply,
                onOrbPress = {
                    orbState = "listening"
                    engines.tts.speakSync("Sim, Fernando?")
                    orbState = "idle"
                },
                onNavigateToChat = { navController.navigate(Screen.Chat.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
            )
        }

        composable(Screen.Chat.route) {
            ChatScreen(
                onSend = { text ->
                    orbState = "processing"
                    val aiResult = engines.ai.send(text)
                    val intent = engines.intent.parse(aiResult.text)
                    val result: ActionResult

                    if (intent.category != com.fernando.jarvis.engine.IntentCategory.UNKNOWN) {
                        result = engines.action.execute(intent)
                        lastReply = result.message
                    } else {
                        lastReply = aiResult.text
                        result = ActionResult(true, aiResult.text)
                    }

                    if (aiResult.isLocal) {
                        lastReply = aiResult.text
                    }

                    engines.tts.speakSync(lastReply)
                    orbState = "idle"
                    lastReply
                },
                onBack = { navController.popBackStack() },
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                engines = engines,
                onBack = { navController.popBackStack() },
            )
        }
    }
}
