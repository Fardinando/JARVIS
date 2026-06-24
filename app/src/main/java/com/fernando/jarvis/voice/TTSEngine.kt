package com.fernando.jarvis.voice

import android.content.Context
import android.os.Build
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.speech.tts.Voice
import kotlinx.coroutines.CompletableDeferred
import java.util.Locale

class TTSEngine(context: Context) {

    private val tts: TextToSpeech = TextToSpeech(context) { status ->
        if (status == TextToSpeech.SUCCESS) {
            configure()
        }
    }

    private var isSpeaking = false
    private var ready = false

    private fun configure() {
        tts.language = Locale("pt", "BR")
        tts.setSpeechRate(0.85f)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val neuralVoice = tts.voices?.find { voice ->
                voice.locale.language == "pt" &&
                voice.features.contains(TextToSpeech.Engine.KEY_FEATURE_NETWORK_SYNTHESIS)
            }
            if (neuralVoice != null) {
                tts.voice = neuralVoice
            }
        }

        tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                isSpeaking = true
            }
            override fun onDone(utteranceId: String?) {
                isSpeaking = false
            }
            override fun onError(utteranceId: String?) {
                isSpeaking = false
            }
        })

        ready = true
    }

    suspend fun speak(text: String) {
        if (!ready) return
        val deferred = CompletableDeferred<Unit>()

        val result = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "utterance_${System.currentTimeMillis()}")
        } else {
            @Suppress("DEPRECATION")
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null)
        }

        if (result == TextToSpeech.SUCCESS) {
            deferred.await()
        }
    }

    fun speakSync(text: String) {
        if (!ready) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "utterance_${System.currentTimeMillis()}")
        } else {
            @Suppress("DEPRECATION")
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null)
        }
    }

    fun stop() {
        tts.stop()
        isSpeaking = false
    }

    fun isSpeaking(): Boolean = isSpeaking

    companion object {
        private var instance: TTSEngine? = null

        fun get(context: Context): TTSEngine {
            if (instance == null) {
                instance = TTSEngine(context.applicationContext)
            }
            return instance!!
        }

        fun shutdown() {
            instance?.tts?.shutdown()
            instance = null
        }
    }
}
