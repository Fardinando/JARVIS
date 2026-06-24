package com.fernando.jarvis.telegram

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class TelegramService {

    private val botToken = "8636115536"
    private val chatId = "@fernandoaas"
    private val apiUrl = "https://api.telegram.org/bot$botToken/sendMessage"

    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    private val scope = CoroutineScope(Dispatchers.IO)
    private val jsonMediaType = "application/json".toMediaType()
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale("pt", "BR"))

    fun send(message: String) {
        scope.launch {
            try {
                val formatted = """
JARVIS UPDATE:

$message

_${dateFormat.format(Date())}_
                """.trimIndent()

                val json = JSONObject().apply {
                    put("chat_id", chatId)
                    put("text", formatted)
                    put("parse_mode", "Markdown")
                }

                val request = Request.Builder()
                    .url(apiUrl)
                    .post(json.toString().toRequestBody(jsonMediaType))
                    .build()

                client.newCall(request).execute()
            } catch (_: Exception) {}
        }
    }

    fun sendAction(action: String, status: String, result: String) {
        send("* Acao:* $action\n* Status:* $status\n* Resultado:* $result")
    }

    fun sendError(error: String, context: String = "") {
        send("* ERRO:* $error\n* Contexto:* $context")
    }

    fun sendLog(log: String) {
        send("* Log:* $log")
    }
}
