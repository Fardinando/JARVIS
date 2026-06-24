package com.fernando.jarvis.system

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import com.fernando.jarvis.JARVISApp

enum class PowerState {
    DORMANT,
    LISTENING,
    PROCESSING,
    ACTIVE,
}

object BatteryOptimizer {

    var currentState = PowerState.DORMANT
        private set

    private var currentLevel = 100

    fun setState(state: PowerState) {
        currentState = state
    }

    fun getCurrentLevel(): Int {
        try {
            val context = JARVISApp.instance
            val intent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
            if (intent != null) {
                val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                if (level >= 0 && scale > 0) {
                    currentLevel = (level * 100 / scale)
                }
            }
        } catch (_: Exception) {}
        return currentLevel
    }

    fun getPowerSaveMode(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val powerManager = JARVISApp.instance.getSystemService(Context.POWER_SERVICE)
                as? android.os.PowerManager
            return powerManager?.isPowerSaveMode == true
        }
        return currentLevel < 20
    }

    fun shouldProcess(): Boolean {
        return when (currentState) {
            PowerState.DORMANT -> false
            PowerState.LISTENING -> true
            PowerState.PROCESSING -> true
            PowerState.ACTIVE -> true
        }
    }
}
