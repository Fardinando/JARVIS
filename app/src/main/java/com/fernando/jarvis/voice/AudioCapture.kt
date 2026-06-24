package com.fernando.jarvis.voice

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import kotlinx.coroutines.*

class AudioCapture {

    private var audioRecord: AudioRecord? = null
    private var isCapturing = false
    private var job: Job? = null

    private val sampleRate = 16000

    fun start(frameLength: Int = 512, onFrame: (ShortArray) -> Unit) {
        if (isCapturing) return

        val bufferSize = AudioRecord.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
        ).coerceAtLeast(frameLength * 2)

        try {
            audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                sampleRate,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize,
            )

            if (audioRecord?.state != AudioRecord.STATE_INITIALIZED) {
                audioRecord?.release()
                audioRecord = null
                return
            }

            audioRecord?.startRecording()
            isCapturing = true

            job = CoroutineScope(Dispatchers.IO).launch {
                val buffer = ShortArray(frameLength)
                while (isCapturing && audioRecord != null) {
                    val read = audioRecord?.read(buffer, 0, buffer.size) ?: 0
                    if (read > 0) {
                        onFrame(buffer)
                    }
                }
            }
        } catch (_: Exception) {}
    }

    fun stop() {
        isCapturing = false
        job?.cancel()
        try {
            audioRecord?.stop()
            audioRecord?.release()
        } catch (_: Exception) {}
        audioRecord = null
    }
}
