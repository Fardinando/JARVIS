package com.fernando.jarvis

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import com.fernando.jarvis.ui.JarvisNavigation
import com.fernando.jarvis.ui.theme.JarvisTheme
import com.fernando.jarvis.engine.IntentEngine
import com.fernando.jarvis.engine.ActionEngine
import com.fernando.jarvis.engine.PermissionEngine
import com.fernando.jarvis.ai.AIService
import com.fernando.jarvis.voice.WakeWordService
import com.fernando.jarvis.voice.TTSEngine

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val tts = TTSEngine(this)
        val aiService = AIService()
        val intentEngine = IntentEngine()
        val permissionEngine = PermissionEngine(this)
        val actionEngine = ActionEngine(this, permissionEngine)

        setContent {
            JarvisTheme {
                val engines = remember {
                    JarvisEngines(
                        ai = aiService,
                        intent = intentEngine,
                        permission = permissionEngine,
                        action = actionEngine,
                        tts = tts,
                    )
                }
                JarvisNavigation(engines)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        TTSEngine.shutdown()
    }
}

data class JarvisEngines(
    val ai: AIService,
    val intent: IntentEngine,
    val permission: PermissionEngine,
    val action: ActionEngine,
    val tts: TTSEngine,
)
