package com.fernando.jarvis.ai

import com.fernando.jarvis.JARVISApp
import com.fernando.jarvis.system.BatteryOptimizer
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LocalCommands {

    private val timeFormat = SimpleDateFormat("HH:mm", Locale("pt", "BR"))
    private val dateFormat = SimpleDateFormat("EEEE, dd 'de' MMMM 'de' yyyy", Locale("pt", "BR"))

    fun process(text: String): AIResponse {
        val lower = text.lowercase(Locale.ROOT).trim()

        if (lower.contains("hora") || lower.matches(Regex("que\\s*horas?\\s*sao"))) {
            return AIResponse(
                "Sao ${getCurrentTime()}.",
                "local",
                isLocal = true,
            )
        }

        if (lower.contains("dia") || lower.contains("data") || lower.matches(Regex("que\\s*div?a\\s*e\\s*hoje"))) {
            return AIResponse(
                "Hoje e ${getCurrentDate()}.",
                "local",
                isLocal = true,
            )
        }

        if (lower.contains("bateria") || lower.contains("carga")) {
            val level = getBatteryLevel()
            val status = when {
                level > 80 -> "Carga alta."
                level > 30 -> "Carga media."
                else -> "Carga baixa."
            }
            return AIResponse(
                "Nivel de bateria: $level%. $status",
                "local",
                isLocal = true,
            )
        }

        if (lower == "ajuda" || lower == "help" || lower == "comandos") {
            return AIResponse(
                """COMANDOS LOCAIS:
                |"hora" - Hora atual
                |"dia" / "data" - Data atual
                |"bateria" - Nivel de bateria
                |"ajuda" - Esta lista
                |
                |Para comandos com IA, configure API key no app.""".trimMargin(),
                "local",
                isLocal = true,
            )
        }

        if (lower.contains("obrigad") || lower.contains("valeu")) {
            return AIResponse("Sem problemas, Fernando.", "local", isLocal = true)
        }

        if (lower.contains("quem e voce") || lower.contains("seu nome")) {
            return AIResponse(
                "Sou JARVIS, seu sistema operacional pessoal. Centro de comando ativo.",
                "local",
                isLocal = true,
            )
        }

        return AIResponse(
            "API key nao configurada. Sem ela, apenas comandos locais funcionam.",
            "local",
            isLocal = true,
        )
    }

    fun getCurrentTime(): String = timeFormat.format(Date())
    fun getCurrentDate(): String = dateFormat.format(Date())

    fun getBatteryLevel(): Int {
        return BatteryOptimizer.getCurrentLevel()
    }
}
