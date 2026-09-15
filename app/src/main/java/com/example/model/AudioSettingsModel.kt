package com.example.model

data class EqualizerBand(
    val id: Int,
    val frequencyLabel: String,
    val gainDb: Float // -12f to +12f
)

enum class EqualizerPreset(val displayName: String, val gains: List<Float>) {
    FLAT("Flat", listOf(0f, 0f, 0f, 0f, 0f)),
    ACOUSTIC("Acoustic", listOf(4.5f, 3.0f, 1.0f, 3.5f, 4.0f)),
    JAZZ("Jazz", listOf(3.5f, 2.0f, -1.5f, 2.5f, 4.5f)),
    BASS_BOOST("Bass Boost", listOf(7.0f, 5.5f, 1.5f, 0.0f, -1.0f)),
    ELECTRONIC("Electronic", listOf(5.0f, 3.5f, -0.5f, 2.5f, 5.0f)),
    ROCK("Rock", listOf(5.5f, 3.0f, -1.0f, 3.0f, 5.5f)),
    VOCAL("Vocal", listOf(-1.5f, 2.0f, 6.0f, 3.5f, 1.0f)),
    POP("Pop", listOf(-1.0f, 2.0f, 5.0f, 2.5f, -1.0f)),
    CUSTOM("Custom", listOf(0f, 0f, 0f, 0f, 0f))
}

data class AudioAppSettings(
    val equalizerEnabled: Boolean = true,
    val selectedPreset: EqualizerPreset = EqualizerPreset.ACOUSTIC,
    val bands: List<EqualizerBand> = listOf(
        EqualizerBand(0, "60 Hz", 4.5f),
        EqualizerBand(1, "230 Hz", 3.0f),
        EqualizerBand(2, "910 Hz", 1.0f),
        EqualizerBand(3, "3.6 kHz", 3.5f),
        EqualizerBand(4, "14 kHz", 4.0f)
    ),
    val bassBoostAmount: Float = 0.65f, // 0f to 1f
    val virtualizerAmount: Float = 0.50f, // 0f to 1f
    val audioQuality: String = "Hi-Res Lossless (24-bit / 96kHz)",
    val language: String = "English (US)",
    val crossfadeEnabled: Boolean = true,
    val crossfadeDurationSeconds: Float = 4.0f,
    val gaplessPlayback: Boolean = true,
    val monoAudio: Boolean = false,
    val developerMode: Boolean = false
)
