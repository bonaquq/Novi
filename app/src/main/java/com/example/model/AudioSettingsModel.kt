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

data class DetailedStudioPreset(
    val name: String,
    val gains: List<Float> // 6 bands: 60Hz, 150Hz, 400Hz, 1KHz, 2.4KHz, 15KHz (-12f to +12f)
)

object DetailedEqualizerDefaults {
    val frequencyLabels = listOf("60Hz", "150Hz", "400Hz", "1KHz", "2.4KHz", "15KHz")

    val presets = listOf(
        DetailedStudioPreset("Acoustic", listOf(4.0f, 3.0f, 1.0f, 2.0f, 3.5f, 4.0f)),
        DetailedStudioPreset("Bass Booster", listOf(6.0f, 4.5f, 2.5f, 0.0f, -1.0f, -2.0f)),
        // Exactly matches user screenshot reference: lower bass, ramping up to +3dB plateau at 1K, 2.4K, 15K
        DetailedStudioPreset("Bass Reducer", listOf(-6.0f, -4.0f, -1.5f, 3.0f, 3.0f, 3.0f)),
        DetailedStudioPreset("Classical", listOf(4.5f, 3.0f, -1.0f, 2.0f, 3.0f, 3.5f)),
        DetailedStudioPreset("Dance", listOf(5.0f, 3.5f, 1.5f, 0.0f, 2.5f, 4.5f)),
        DetailedStudioPreset("Deep", listOf(6.0f, 4.0f, 2.0f, 0.5f, -1.5f, -3.0f)),
        DetailedStudioPreset("Electronic", listOf(5.5f, 4.0f, 0.5f, 1.5f, 3.0f, 5.0f)),
        DetailedStudioPreset("Flat", listOf(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f)),
        DetailedStudioPreset("Hip-Hop", listOf(5.5f, 4.5f, 1.0f, 2.0f, 1.5f, 3.5f)),
        DetailedStudioPreset("Jazz", listOf(3.5f, 2.5f, 0.0f, 1.5f, 2.5f, 4.0f)),
        DetailedStudioPreset("Latin", listOf(4.0f, 2.5f, -1.0f, 0.0f, 2.5f, 4.5f)),
        DetailedStudioPreset("Loudness", listOf(7.0f, 4.0f, -2.0f, -1.0f, 2.0f, 6.0f)),
        DetailedStudioPreset("Lounge", listOf(-3.0f, -1.5f, 1.0f, 3.0f, 2.0f, -1.0f)),
        DetailedStudioPreset("Piano", listOf(3.0f, 2.0f, 0.0f, 2.5f, 3.0f, 3.5f)),
        DetailedStudioPreset("Pop", listOf(-1.5f, 1.5f, 4.0f, 4.0f, 2.0f, -1.0f)),
        DetailedStudioPreset("R&B", listOf(5.0f, 4.0f, 1.5f, -1.0f, 3.0f, 4.5f)),
        DetailedStudioPreset("Rock", listOf(6.0f, 3.5f, -1.0f, 1.0f, 3.5f, 5.5f)),
        DetailedStudioPreset("Small Speakers", listOf(6.5f, 4.5f, 2.0f, 0.0f, -2.0f, -4.0f)),
        DetailedStudioPreset("Spoken Word", listOf(-4.0f, 0.0f, 4.5f, 5.0f, 2.0f, -2.0f)),
        DetailedStudioPreset("Treble Booster", listOf(-3.0f, -2.0f, 0.0f, 2.0f, 4.5f, 6.5f)),
        DetailedStudioPreset("Treble Reducer", listOf(3.0f, 2.0f, 1.0f, -1.0f, -3.5f, -6.0f)),
        DetailedStudioPreset("Vocal Booster", listOf(-2.0f, 0.5f, 4.5f, 5.0f, 3.0f, 0.5f))
    )

    fun createInitialDetailedBands(): List<EqualizerBand> {
        val bassReducer = presets.first { it.name == "Bass Reducer" }
        return frequencyLabels.mapIndexed { index, label ->
            EqualizerBand(index, label, bassReducer.gains[index])
        }
    }
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
    val detailedBands: List<EqualizerBand> = DetailedEqualizerDefaults.createInitialDetailedBands(),
    val detailedSelectedPreset: String = "Bass Reducer",
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
