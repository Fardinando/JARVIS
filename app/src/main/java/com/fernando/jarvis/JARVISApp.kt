package com.fernando.jarvis

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.fernando.jarvis.memory.MemoryEngine
import com.fernando.jarvis.telegram.TelegramService

class JARVISApp : Application() {

    lateinit var memoryEngine: MemoryEngine
        private set
    lateinit var telegramService: TelegramService
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        createNotificationChannels()
        memoryEngine = MemoryEngine(this)
        telegramService = TelegramService()
        telegramService.send("JARVIS v4.0 iniciado \u2705")
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(NotificationManager::class.java)

            val foreground = NotificationChannel(
                CHANNEL_FOREGROUND,
                "JARVIS Ativo",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Servico em segundo plano do JARVIS"
                setSound(null, null)
            }

            val alert = NotificationChannel(
                CHANNEL_ALERTS,
                "Alertas JARVIS",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificacoes e alertas do JARVIS"
            }

            manager.createNotificationChannel(foreground)
            manager.createNotificationChannel(alert)
        }
    }

    companion object {
        const val CHANNEL_FOREGROUND = "jarvis_foreground"
        const val CHANNEL_ALERTS = "jarvis_alerts"

        lateinit var instance: JARVISApp
            private set
    }
}
