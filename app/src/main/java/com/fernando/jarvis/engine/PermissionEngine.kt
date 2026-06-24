package com.fernando.jarvis.engine

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

enum class PermissionLevel {
    FREE,
    CONFIRM,
    BLOCKED,
    BIOMETRY,
}

data class AppPermission(
    val packageName: String,
    val level: PermissionLevel,
)

class PermissionEngine(private val context: Context) {

    private val appPermissions = mutableMapOf<String, PermissionLevel>()

    init {
        appPermissions["com.whatsapp"] = PermissionLevel.FREE
        appPermissions["com.spotify.music"] = PermissionLevel.FREE
        appPermissions["com.google.android.youtube"] = PermissionLevel.FREE
        appPermissions["com.nubank"] = PermissionLevel.BLOCKED
        appPermissions["org.telegram.messenger"] = PermissionLevel.FREE
        appPermissions["com.discord"] = PermissionLevel.FREE
        appPermissions["com.instagram.android"] = PermissionLevel.FREE
        appPermissions["com.android.chrome"] = PermissionLevel.FREE
    }

    fun check(appPackage: String, action: String): PermissionLevel {
        return appPermissions[appPackage] ?: PermissionLevel.CONFIRM
    }

    fun setPermission(packageName: String, level: PermissionLevel) {
        appPermissions[packageName] = level
    }

    fun getAllPermissions(): Map<String, PermissionLevel> = appPermissions.toMap()

    suspend fun requireBiometric(activity: FragmentActivity): Boolean {
        val biometricManager = BiometricManager.from(context)
        return when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                promptBiometric(activity)
            }
            else -> false
        }
    }

    private suspend fun promptBiometric(activity: FragmentActivity): Boolean {
        return suspendCancellableCoroutine { continuation ->
            val executor = ContextCompat.getMainExecutor(context)
            val prompt = BiometricPrompt(
                activity,
                executor,
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        continuation.resume(true)
                    }
                    override fun onAuthenticationFailed() {
                        continuation.resume(false)
                    }
                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        continuation.resume(false)
                    }
                }
            )

            val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle("Autenticacao necessaria")
                .setSubtitle("Acao restrita requer verificacao")
                .setNegativeButtonText("Cancelar")
                .build()

            prompt.authenticate(promptInfo)
        }
    }
}
