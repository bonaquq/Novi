package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

/**
 * Real-time dynamic audio synthesizer and playback engine.
 * Generates rhythmic electronic basslines and beat pulses matching the selected track.
 */
class MusicAudioEngine(private val scope: CoroutineScope) {

    private var audioTrack: AudioTrack? = null
    private var playbackJob: Job? = null
    private var isPlaying = false
    private var currentFrequency = 110f
    private var currentBpm = 140

    private var bassGain: Float = 1.0f
    private var trebleGain: Float = 1.0f

    init {
        initAudioTrack()
    }

    fun updateEqualizer(bassGainMult: Float, trebleGainMult: Float) {
        this.bassGain = bassGainMult.coerceIn(0.2f, 3.0f)
        this.trebleGain = trebleGainMult.coerceIn(0.2f, 3.0f)
    }

    private fun initAudioTrack() {
        try {
            val sampleRate = 44100
            val minBufferSize = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )
            val bufferSize = (minBufferSize * 2).coerceAtLeast(4096)

            audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(bufferSize)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun play(baseFrequency: Float, bpm: Int) {
        currentFrequency = baseFrequency
        currentBpm = bpm
        isPlaying = true

        try {
            if (audioTrack == null || audioTrack?.state != AudioTrack.STATE_INITIALIZED) {
                initAudioTrack()
            }
            audioTrack?.play()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        playbackJob?.cancel()
        playbackJob = scope.launch(Dispatchers.Default) {
            val sampleRate = 44100
            val bufferSize = 2048
            val buffer = ShortArray(bufferSize)
            var sampleIndex = 0L

            val samplesPerBeat = (sampleRate * 60.0 / currentBpm).toInt().coerceAtLeast(1)

            while (isActive && isPlaying) {
                for (i in 0 until bufferSize) {
                    val time = sampleIndex.toDouble() / sampleRate
                    val beatPosition = (sampleIndex % samplesPerBeat).toDouble() / samplesPerBeat
                    val beatIndex = ((sampleIndex / samplesPerBeat) % 8).toInt()

                    // Drum kick on beats 0, 4 and snare on 2, 6
                    val isKick = (beatIndex == 0 || beatIndex == 4 || beatIndex == 6)
                    val kickEnvelope = if (isKick) exp(-beatPosition * 8.0) else 0.0
                    val kickFreq = 140.0 * exp(-beatPosition * 12.0) + 45.0
                    val kickWave = sin(2.0 * PI * kickFreq * time) * kickEnvelope

                    // Wobble LFO for bassline
                    val lfoRate = when (beatIndex) {
                        0, 1 -> 4.0 // quarter wobble
                        2, 3 -> 8.0 // 8th note wobble
                        else -> 12.0 // triplet wobble
                    }
                    val lfo = (sin(2.0 * PI * lfoRate * time) + 1.0) * 0.5

                    // Harmonic pitch sequence
                    val pitchMultiplier = when (beatIndex) {
                        0, 1 -> 1.0
                        2 -> 1.189 // minor 3rd
                        3 -> 1.335 // 4th
                        4, 5 -> 1.0
                        6 -> 1.498 // 5th
                        else -> 1.122 // Major 2nd
                    }
                    val f = currentFrequency * pitchMultiplier

                    // Rich sub + saw harmonic synth with dynamic EQ gain
                    val sub = sin(2.0 * PI * f * time) * bassGain
                    val saw = (sin(2.0 * PI * (f * 2) * time) * 0.5 + sin(2.0 * PI * (f * 3) * time) * 0.25) * trebleGain
                    val bassSynth = (sub * 0.6 + saw * 0.4 * lfo) * 0.45

                    // Combined sample with gentle limiter
                    val sample = (kickWave * 0.45 * bassGain + bassSynth * 0.55).coerceIn(-0.95, 0.95)
                    buffer[i] = (sample * Short.MAX_VALUE).toInt().toShort()
                    sampleIndex++
                }

                try {
                    audioTrack?.write(buffer, 0, bufferSize)
                } catch (e: Exception) {
                    break
                }
            }
        }
    }

    fun pause() {
        isPlaying = false
        playbackJob?.cancel()
        playbackJob = null
        try {
            audioTrack?.pause()
            audioTrack?.flush()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun stop() {
        pause()
        try {
            audioTrack?.stop()
            audioTrack?.release()
            audioTrack = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
