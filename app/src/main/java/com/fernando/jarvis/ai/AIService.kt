package com.fernando.jarvis.ai

import com.fernando.jarvis.JARVISApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class AIResponse(
    val text: String,
    val model: String,
    val isLocal: Boolean = false,
)

class AIService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val models = listOf(
        AIModel("google/gemma-4-31b-it:free", 262144),
        AIModel("nvidia/nemotron-3-super-120b-a12b:free", 1048576),
        AIModel("openai/gpt-oss-120b:free", 131072),
        AIModel("google/gemma-4-26b-a4b-it:free", 262144),
        AIModel("meta-llama/llama-3.3-70b-instruct:free", 131072),
    )

    private val jsonMediaType = "application/json".toMediaType()
    private val localCommands = LocalCommands()

    suspend fun send(
        text: String,
        context: String? = null,
        apiKey: String? = null,
    ): AIResponse = withContext(Dispatchers.IO) {
        val key = apiKey
            ?: System.getenv("OPENROUTER_API_KEY")
            ?: return@withContext localCommands.process(text)

        for (model in models) {
            try {
                val response = callOpenRouter(model, text, key, context)
                if (response != null) return@withContext response
            } catch (_: Exception) {
                continue
            }
        }

        localCommands.process(text)
    }

    private fun callOpenRouter(
        model: AIModel,
        text: String,
        apiKey: String,
        context: String?,
    ): AIResponse? {
        val systemPrompt = buildSystemPrompt(context)

        val body = JSONObject().apply {
            put("model", model.id)
            put("messages", JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "system")
                    put("content", systemPrompt)
                })
                put(JSONObject().apply {
                    put("role", "user")
                    put("content", text)
                })
            })
            put("max_tokens", 512)
            put("temperature", 0.7)
        }

        val request = Request.Builder()
            .url("https://openrouter.ai/api/v1/chat/completions")
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("Content-Type", "application/json")
            .addHeader("HTTP-Referer", "https://jarvis.app")
            .addHeader("X-Title", "JARVIS OS")
            .post(body.toString().toRequestBody(jsonMediaType))
            .build()

        val response = client.newCall(request).execute()

        if (!response.isSuccessful) return null

        val json = JSONObject(response.body!!.string())
        val reply = json.getJSONArray("choices")
            .getJSONObject(0)
            .getJSONObject("message")
            .getString("content")

        return AIResponse(reply, model.id)
    }

    private fun buildSystemPrompt(context: String?): String {
        val memory = JARVISApp.instance.memoryEngine
        val recent = memory.getRecentHistory(5)
        val contextBlock = if (context != null) "\nContexto: $context" else ""

        return """
Voce e JARVIS, sistema operacional pessoal de Fernando.
Fale portugues brasileiro natural. Seja direto, curto e operacional.
Nunca diga "posso te ajudar". Use "Executado.", "Concluido.", "Processando.", "Acesso negado."
Voce tem acesso total ao dispositivo Android de Fernando.

Comandos especiais (use marcacao explicita):
[HORA] - horario atual
[DATA] - data atual
[BATERIA] - nivel da bateria
[ALARME: hora/minuto "label"] - criar alarme
[TIMER: segundos] - criar timer
[MENSAGEM: contato "texto"] - enviar mensagem
[APP: nome] - abrir aplicativo
[VOLUME: nivel] - ajustar volume 0-100
[BRILHO: nivel] - ajustar brilho 0-100
[WIFI: on/off] - controle wifi
[CHAMADA: contato] - fazer ligacao

Contexto atual:
Data: ${localCommands.getCurrentDate()}
Hora: ${localCommands.getCurrentTime()}
Bateria: ${localCommands.getBatteryLevel()}%
${contextBlock}
Ultimos comandos: $recent
        """.trimIndent()
    }
}
