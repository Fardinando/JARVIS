package com.fernando.jarvis.engine

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.AlarmClock
import android.provider.Settings
import com.fernando.jarvis.JARVISApp
import com.fernando.jarvis.system.AppController
import com.fernando.jarvis.system.SystemController

data class ActionResult(
    val success: Boolean,
    val message: String,
)

class ActionEngine(
    private val context: Context,
    private val permissionEngine: PermissionEngine,
) {

    private val appController = AppController(context)
    private val systemController = SystemController(context)

    suspend fun execute(intent: ParsedIntent): ActionResult {
        when (intent.category) {
            IntentCategory.QUERY -> {
                return when (intent.action) {
                    "time" -> ActionResult(true, "Sao ${systemController.getCurrentTime()}.")
                    "date" -> ActionResult(true, "Hoje e ${systemController.getCurrentDate()}.")
                    "battery" -> {
                        val level = systemController.getBatteryLevel()
                        ActionResult(true, "Bateria: $level%.")
                    }
                    else -> ActionResult(false, "Comando nao reconhecido.")
                }
            }

            IntentCategory.PRODUCTIVITY -> {
                return when (intent.action) {
                    "alarm" -> {
                        val hour = intent.params["hour"]?.toIntOrNull() ?: return ActionResult(false, "Hora invalida.")
                        val minute = intent.params["minute"]?.toIntOrNull() ?: return ActionResult(false, "Minuto invalido.")
                        val label = intent.params["label"] ?: "Alarme"
                        setAlarm(hour, minute, label)
                    }
                    "timer" -> {
                        val seconds = intent.params["seconds"]?.toIntOrNull() ?: return ActionResult(false, "Tempo invalido.")
                        setTimer(seconds)
                    }
                    else -> ActionResult(false, "Acao de produtividade nao reconhecida.")
                }
            }

            IntentCategory.COMMUNICATION -> {
                val packageName = resolveContactApp(intent.target ?: "")
                val level = permissionEngine.check(packageName, intent.action)

                return when (level) {
                    PermissionLevel.BLOCKED -> ActionResult(false, "Acesso negado. Aplicativo bloqueado.")
                    PermissionLevel.BIOMETRY -> {
                        if (context is androidx.fragment.app.FragmentActivity &&
                            permissionEngine.requireBiometric(context)
                        ) {
                            executeAction(intent)
                        } else {
                            ActionResult(false, "Autenticacao necessaria.")
                        }
                    }
                    else -> executeAction(intent)
                }
            }

            IntentCategory.APP_CONTROL -> {
                val appName = intent.target ?: return ActionResult(false, "App nao especificado.")
                val packageName = appController.resolvePackage(appName)
                val level = permissionEngine.check(packageName, intent.action)

                return when (level) {
                    PermissionLevel.BLOCKED -> ActionResult(false, "Acesso negado. Aplicativo bloqueado.")
                    else -> {
                        appController.openApp(packageName)
                        ActionResult(true, "Abrindo $appName.")
                    }
                }
            }

            IntentCategory.SYSTEM -> {
                return when (intent.action) {
                    "volume" -> {
                        val level = intent.params["level"]?.toIntOrNull()
                        if (level != null) {
                            systemController.setMediaVolume(level)
                            ActionResult(true, "Volume ajustado para $level.")
                        } else {
                            ActionResult(false, "Nivel de volume invalido.")
                        }
                    }
                    "wifi" -> {
                        val state = intent.params["state"]
                        systemController.toggleWifi(state == "on")
                        ActionResult(true, if (state == "on") "WiFi ligado." else "WiFi desligado.")
                    }
                    else -> ActionResult(false, "Comando de sistema nao reconhecido.")
                }
            }

            else -> {
                return ActionResult(false, "Categoria nao implementada.")
            }
        }
    }

    private fun executeAction(intent: ParsedIntent): ActionResult {
        return when (intent.action) {
            "send_message" -> {
                val contact = intent.target ?: return ActionResult(false, "Contato nao especificado.")
                val text = intent.params["text"] ?: return ActionResult(false, "Texto nao especificado.")
                appController.openWhatsApp(contact, text)
                ActionResult(true, "Enviando mensagem para $contact.")
            }
            "call" -> {
                val contact = intent.target ?: return ActionResult(false, "Contato nao especificado.")
                appController.makeCall(contact)
                ActionResult(true, "Ligando para $contact.")
            }
            else -> ActionResult(false, "Acao nao reconhecida.")
        }
    }

    private fun setAlarm(hour: Int, minute: Int, label: String): ActionResult {
        val intent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
            putExtra(AlarmClock.EXTRA_HOUR, hour)
            putExtra(AlarmClock.EXTRA_MINUTES, minute)
            putExtra(AlarmClock.EXTRA_MESSAGE, label)
            putExtra(AlarmClock.EXTRA_SKIP_UI, false)
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
        return ActionResult(true, "Alarme configurado para $hour:${minute.toString().padStart(2, '0')}.")
    }

    private fun setTimer(seconds: Int): ActionResult {
        val intent = Intent(AlarmClock.ACTION_SET_TIMER).apply {
            putExtra(AlarmClock.EXTRA_LENGTH, seconds)
            putExtra(AlarmClock.EXTRA_SKIP_UI, false)
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
        return ActionResult(true, "Timer configurado para $seconds segundos.")
    }

    private fun resolveContactApp(contact: String): String {
        return "com.whatsapp"
    }
}
