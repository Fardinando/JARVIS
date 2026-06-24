package com.fernando.jarvis.system

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings

class AppController(private val context: Context) {

    private val packageManager = context.packageManager

    private val knownApps = mapOf(
        "whatsapp" to "com.whatsapp",
        "telegram" to "org.telegram.messenger",
        "discord" to "com.discord",
        "instagram" to "com.instagram.android",
        "spotify" to "com.spotify.music",
        "youtube" to "com.google.android.youtube",
        "chrome" to "com.android.chrome",
        "nubank" to "com.nubank",
        "gmail" to "com.google.android.gm",
        "mapas" to "com.google.android.apps.maps",
        "fotos" to "com.google.android.apps.photos",
        "relogio" to "com.google.android.deskclock",
        "configuracoes" to "com.android.settings",
        "calculadora" to "com.google.android.calculator",
        "camera" to "com.android.camera",
    )

    fun resolvePackage(name: String): String {
        val lower = name.lowercase().trim()
        return knownApps[lower] ?: lower
    }

    fun openApp(packageName: String): Boolean {
        return try {
            val intent = packageManager.getLaunchIntentForPackage(packageName)
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                true
            } else {
                val playIntent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("market://details?id=$packageName")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(playIntent)
                false
            }
        } catch (_: Exception) {
            false
        }
    }

    fun openWhatsApp(contact: String, message: String): Boolean {
        return try {
            val uri = Uri.parse("https://api.whatsapp.com/send?phone=$contact&text=${Uri.encode(message)}")
            val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            true
        } catch (_: Exception) {
            false
        }
    }

    fun openTelegram(contact: String, message: String): Boolean {
        return try {
            val intent = Intent(Intent.ACTION_SEND).apply {
                setPackage("org.telegram.messenger")
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, message)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            true
        } catch (_: Exception) {
            false
        }
    }

    fun makeCall(number: String): Boolean {
        return try {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$number")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            true
        } catch (_: Exception) {
            false
        }
    }

    fun isAppInstalled(packageName: String): Boolean {
        return try {
            packageManager.getPackageInfo(packageName, 0)
            true
        } catch (_: PackageManager.NameNotFoundException) {
            false
        }
    }

    fun getInstalledApps(): List<String> {
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        return packageManager.queryIntentActivities(intent, 0)
            .map { it.activityInfo.packageName }
            .distinct()
    }
}
