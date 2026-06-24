package com.fernando.jarvis.system

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.net.wifi.WifiManager
import android.provider.Settings
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SystemController(private val context: Context) {

    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

    private val timeFormat = SimpleDateFormat("HH:mm", Locale("pt", "BR"))
    private val dateFormat = SimpleDateFormat("EEEE, dd 'de' MMMM 'de' yyyy", Locale("pt", "BR"))

    fun setMediaVolume(level: Int) {
        val max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val scaled = (level * max) / 100
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, scaled, 0)
    }

    fun getMediaVolume(): Int {
        val max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val current = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        return (current * 100) / max
    }

    fun toggleWifi(enable: Boolean) {
        wifiManager.isWifiEnabled = enable
    }

    fun isWifiEnabled(): Boolean = wifiManager.isWifiEnabled

    fun setScreenBrightness(level: Int) {
        try {
            Settings.System.putInt(
                context.contentResolver,
                Settings.System.SCREEN_BRIGHTNESS,
                (level * 255) / 100
            )
        } catch (_: Exception) {}
    }

    fun getBatteryLevel(): Int = BatteryOptimizer.getCurrentLevel()

    fun getCurrentTime(): String = timeFormat.format(Date())
    fun getCurrentDate(): String = dateFormat.format(Date())

    fun isHeadsetConnected(): Boolean {
        return audioManager.isWiredHeadsetOn || audioManager.isBluetoothA2dpOn
    }
}
