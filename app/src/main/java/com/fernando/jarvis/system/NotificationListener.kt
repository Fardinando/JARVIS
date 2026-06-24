package com.fernando.jarvis.system

import android.accessibilityservice.AccessibilityService
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.fernando.jarvis.JARVISApp
import com.fernando.jarvis.voice.TTSEngine

@SuppressLint("OverrideAbstract")
class NotificationListener : NotificationListenerService() {

    private val tts: TTSEngine? get() = try {
        TTSEngine.get(this)
    } catch (_: Exception) {
        null
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val packageName = sbn.packageName
        val notification = sbn.notification
        val title = notification.extras?.getString("android.title") ?: ""
        val text = notification.extras?.getString("android.text") ?: ""

        if (title.isBlank() && text.isBlank()) return

        val appName = getAppName(packageName)
        val type = classifyNotification(packageName)

        val systemController = SystemController(this)
        val headsetConnected = systemController.isHeadsetConnected()

        if (headsetConnected) {
            val message = when (type) {
                "message" -> "Fernando, $title enviou mensagem pelo $appName. Quer que eu leia?"
                "social" -> "Fernando, voce recebeu uma notificacao do $appName."
                "urgent" -> "Alerta: $title - $text"
                "bank" -> "Notificacao bancaria do $appName."
                else -> "Notificacao: $appName - $title"
            }
            tts?.speakSync(message)
        }

        try {
            val telegram = JARVISApp.instance.telegramService
            telegram.send("NOTIFICACAO:\n* App: $appName\n* Titulo: $title\n* Tipo: $type")
        } catch (_: Exception) {}
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {}

    private fun getAppName(pkg: String): String {
        val names = mapOf(
            "com.whatsapp" to "WhatsApp",
            "org.telegram.messenger" to "Telegram",
            "com.discord" to "Discord",
            "com.instagram.android" to "Instagram",
            "com.spotify.music" to "Spotify",
            "com.google.android.gm" to "Gmail",
        )
        return names[pkg] ?: pkg.split(".").lastOrNull() ?: pkg
    }

    private fun classifyNotification(pkg: String): String {
        val p = pkg.lowercase()
        return when {
            p.contains("whatsapp") || p.contains("telegram") || p.contains("discord") || p.contains("signal") -> "message"
            p.contains("instagram") || p.contains("facebook") || p.contains("tiktok") -> "social"
            p.contains("nubank") || p.contains("banco") || p.contains("picpay") || p.contains("mercadopago") -> "bank"
            p.contains("alarm") || p.contains("phone") || p.contains("dialer") -> "urgent"
            else -> "other"
        }
    }
}
