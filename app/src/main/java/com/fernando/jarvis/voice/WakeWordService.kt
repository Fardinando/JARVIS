package com.fernando.jarvis.voice

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.fernando.jarvis.JARVISApp
import com.fernando.jarvis.MainActivity
import com.fernando.jarvis.system.BatteryOptimizer
import com.fernando.jarvis.system.PowerState
import kotlinx.coroutines.*

class WakeWordService : Service() {

    private var audioCapture: AudioCapture? = null
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var isRunning = false
    private var porcupine: Any? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        BatteryOptimizer.setState(PowerState.LISTENING)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIFICATION_ID, createNotification())
        isRunning = true
        scope.launch { startListening() }
        return START_STICKY
    }

    private fun startListening() {
        try {
            val clazz = Class.forName("ai.picovoice.porcupine.Porcupine")
            val builderMethod = clazz.getMethod("Builder")
            val builder = builderMethod.invoke(null)

            val builderClass = builder.javaClass
            builderClass.getMethod("setAccessKey", String::class.java)
                .invoke(builder, getAccessKey())

            builderClass.getMethod("setBuiltInKeyword", String::class.java)
                .invoke(builder, "jarvis")

            porcupine = builderClass.getMethod("build")
                .invoke(builder)

            val processMethod = porcupine!!::class.java
                .getMethod("process", ShortArray::class.java)

            val frameLength = porcupine!!::class.java
                .getMethod("frameLength")
                .invoke(porcupine) as Int

            audioCapture = AudioCapture()
            audioCapture!!.start(frameLength) { frame ->
                val result = processMethod.invoke(porcupine, frame) as Int
                if (result >= 0 && isRunning) {
                    onWakeWordDetected()
                }
            }
        } catch (e: Exception) {
            stopSelf()
        }
    }

    private fun getAccessKey(): String {
        return System.getenv("PICOVOICE_ACCESS_KEY")
            ?: System.getProperty("PICOVOICE_ACCESS_KEY")
            ?: BUILD_IN_KEY
    }

    private fun onWakeWordDetected() {
        BatteryOptimizer.setState(PowerState.ACTIVE)
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("wake_word", true)
        }
        startActivity(intent)
        SpeechRecognizer.startListening(this)
    }

    private fun createNotification(): Notification {
        val pendingIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )

        return NotificationCompat.Builder(this, JARVISApp.CHANNEL_FOREGROUND)
            .setContentTitle("JARVIS")
            .setContentText("Ouvindo...")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setSilent(true)
            .build()
    }

    override fun onDestroy() {
        isRunning = false
        audioCapture?.stop()
        try {
            porcupine?.let {
                it::class.java.getMethod("delete").invoke(it)
            }
        } catch (_: Exception) {}
        scope.cancel()
        BatteryOptimizer.setState(PowerState.DORMANT)
        super.onDestroy()
    }

    companion object {
        const val NOTIFICATION_ID = 1001
        private const val BUILD_IN_KEY = "YOUR_PICOVOICE_ACCESS_KEY"
    }
}
