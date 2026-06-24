package com.fernando.jarvis.engine

data class ParsedIntent(
    val category: IntentCategory,
    val action: String,
    val target: String?,
    val params: Map<String, String>,
)

enum class IntentCategory {
    COMMUNICATION,
    SYSTEM,
    AUTOMATION,
    MEDIA,
    FILES,
    PRODUCTIVITY,
    APP_CONTROL,
    QUERY,
    UNKNOWN,
}

class IntentEngine {

    fun parse(text: String): ParsedIntent {
        val lower = text.lowercase().trim()

        if (lower.startsWith("[hora]") || lower.contains("que horas") || lower == "hora") {
            return ParsedIntent(IntentCategory.QUERY, "time", null, emptyMap())
        }
        if (lower.startsWith("[data]") || lower.contains("que dia") || lower == "data") {
            return ParsedIntent(IntentCategory.QUERY, "date", null, emptyMap())
        }
        if (lower.startsWith("[bateria]") || lower.contains("bateria")) {
            return ParsedIntent(IntentCategory.QUERY, "battery", null, emptyMap())
        }

        val alarmRegex = Regex("""\[ALARME:\s*(\d+):(\d+)\s*"([^"]+)"\]""", RegexOption.IGNORE_CASE)
        alarmRegex.find(lower)?.let {
            return ParsedIntent(
                IntentCategory.PRODUCTIVITY, "alarm", null,
                mapOf("hour" to it.groupValues[1], "minute" to it.groupValues[2], "label" to it.groupValues[3])
            )
        }

        val timerRegex = Regex("""\[TIMER:\s*(\d+)\]""", RegexOption.IGNORE_CASE)
        timerRegex.find(lower)?.let {
            return ParsedIntent(
                IntentCategory.PRODUCTIVITY, "timer", null,
                mapOf("seconds" to it.groupValues[1])
            )
        }

        val msgRegex = Regex("""\[MENSAGEM:\s*([^\s]+)\s*"([^"]+)"\]""", RegexOption.IGNORE_CASE)
        msgRegex.find(lower)?.let {
            return ParsedIntent(
                IntentCategory.COMMUNICATION, "send_message", it.groupValues[1],
                mapOf("text" to it.groupValues[2])
            )
        }

        val appRegex = Regex("""\[APP:\s*([^\]]+)\]""", RegexOption.IGNORE_CASE)
        appRegex.find(lower)?.let {
            return ParsedIntent(
                IntentCategory.APP_CONTROL, "open", it.groupValues[1].trim(),
                emptyMap()
            )
        }

        val volumeRegex = Regex("""\[VOLUME:\s*(\d+)\]""", RegexOption.IGNORE_CASE)
        volumeRegex.find(lower)?.let {
            return ParsedIntent(
                IntentCategory.SYSTEM, "volume", null,
                mapOf("level" to it.groupValues[1])
            )
        }

        val wifiRegex = Regex("""\[WIFI:\s*(on|off|ligado|desligado)\]""", RegexOption.IGNORE_CASE)
        wifiRegex.find(lower)?.let {
            val value = if (it.groupValues[1].lowercase() in listOf("on", "ligado")) "on" else "off"
            return ParsedIntent(IntentCategory.SYSTEM, "wifi", null, mapOf("state" to value))
        }

        val callRegex = Regex("""\[CHAMADA:\s*([^\]]+)\]""", RegexOption.IGNORE_CASE)
        callRegex.find(lower)?.let {
            return ParsedIntent(
                IntentCategory.COMMUNICATION, "call", it.groupValues[1].trim(),
                emptyMap()
            )
        }

        return ParsedIntent(IntentCategory.UNKNOWN, "conversation", null, emptyMap())
    }
}
