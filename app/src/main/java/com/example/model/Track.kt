package com.example.model

enum class ArtworkType {
    SONIC_YOUTH,
    DEPECHE_MODE,
    WOODZ,
    APHEX_TWIN,
    NELLY_MES,
    BOARDS_OF_CANADA,
    GORILLAZ
}

data class LyricLine(
    val timeSeconds: Int,
    val text: String
)

data class Track(
    val id: String,
    val title: String,
    val artist: String,
    val album: String,
    val releaseDate: String,
    val durationSeconds: Int,
    val genre: String = "IDM",
    val artworkType: ArtworkType = ArtworkType.SONIC_YOUTH,
    val isVerified: Boolean = true,
    val badgeLabel: String? = null,
    val lyrics: List<LyricLine> = emptyList(),
    val coverGradientStart: Long = 0xFF10B981,
    val coverGradientEnd: Long = 0xFF047857,
    val accentGlowColor: Long = 0xFF10B981,
    val baseFrequency: Float = 110f,
    val tempoBpm: Int = 120,
    val isFavorite: Boolean = false,
    val customArtworkUri: String? = null
) {
    val formattedDuration: String
        get() {
            val minutes = durationSeconds / 60
            val seconds = durationSeconds % 60
            return "%d:%02d".format(minutes, seconds)
        }
}

enum class RepeatMode {
    OFF,
    ALL,
    ONE
}

data class UserProfile(
    val name: String = "",
    val handle: String = "",
    val bio: String = "Music lover & sound explorer",
    val avatarId: Int = 1,
    val customAvatarUri: String? = null,
    val email: String = "",
    val isLoggedIn: Boolean = false,
    val favoriteGenres: List<String> = emptyList(),
    val memberSince: String = "September 2026"
)

data class UserPlaylist(
    val id: String,
    val name: String,
    val description: String,
    val trackCount: Int,
    val coverGradientStart: Long = 0xFF10B981,
    val coverGradientEnd: Long = 0xFF047857,
    val artworkType: ArtworkType = ArtworkType.APHEX_TWIN,
    val customImageUri: String? = null,
    val trackIds: List<String> = emptyList()
)

data class EssentialCard(
    val id: String,
    val title: String,
    val subtitle: String,
    val badgeText: String,
    val isLive: Boolean,
    val artworkType: ArtworkType,
    val genre: String,
    val trackId: String
)

object SampleMusicData {
    val categories = listOf("All", "IDM", "Rock", "Pop", "Alternative", "Electronic", "Ambient")

    val defaultPlaylists = emptyList<UserPlaylist>()

    val boneLyrics = listOf(
        LyricLine(0, "Dry as a bone, take it to heart"),
        LyricLine(7, "Don't look back, you're the one"),
        LyricLine(15, "That's alright, straight from the bone"),
        LyricLine(24, "Walk on by, look to the left"),
        LyricLine(33, "Touched by none, that's alright"),
        LyricLine(42, "That's alright, dark as ink"),
        LyricLine(52, "Echoes in the quiet street"),
        LyricLine(62, "Shadows dancing at our feet"),
        LyricLine(72, "Walk on by, look to the left"),
        LyricLine(82, "Dry as a bone, take it to heart")
    )

    val tracks = listOf(
        Track(
            id = "t1",
            title = "Bone",
            artist = "Sonic Youth",
            album = "Goo (Deluxe)",
            releaseDate = "26 June 1990",
            durationSeconds = 237,
            genre = "Rock",
            artworkType = ArtworkType.SONIC_YOUTH,
            isVerified = true,
            badgeLabel = "BONE",
            lyrics = boneLyrics,
            coverGradientStart = 0xFF111827,
            coverGradientEnd = 0xFF374151,
            accentGlowColor = 0xFF10B981,
            baseFrequency = 130.81f, // C3
            tempoBpm = 124,
            isFavorite = true
        ),
        Track(
            id = "t2",
            title = "Review",
            artist = "Depeche Mode",
            album = "Violator",
            releaseDate = "19 March 1990",
            durationSeconds = 252,
            genre = "IDM",
            artworkType = ArtworkType.DEPECHE_MODE,
            isVerified = true,
            badgeLabel = "REVIEW",
            lyrics = listOf(
                LyricLine(0, "Words like violence, break the silence"),
                LyricLine(10, "Come crashing in into my little world"),
                LyricLine(20, "Painful to me, pierce right through me"),
                LyricLine(30, "Can't you understand, oh my little girl"),
                LyricLine(42, "All I ever wanted, all I ever needed"),
                LyricLine(54, "Is here in my arms")
            ),
            coverGradientStart = 0xFF3B82F6,
            coverGradientEnd = 0xFFEF4444,
            accentGlowColor = 0xFF3B82F6,
            baseFrequency = 110.0f, // A2
            tempoBpm = 120
        ),
        Track(
            id = "t3",
            title = "Drowning",
            artist = "Woodz",
            album = "OO-LI",
            releaseDate = "26 April 2023",
            durationSeconds = 245,
            genre = "Alternative",
            artworkType = ArtworkType.WOODZ,
            isVerified = true,
            badgeLabel = "DROWNING",
            lyrics = listOf(
                LyricLine(0, "I'm drowning deep in the blue sky"),
                LyricLine(12, "Memories floating as the waves go by"),
                LyricLine(24, "Can you hear my silent call"),
                LyricLine(36, "Catch me before I take the fall"),
                LyricLine(48, "Drowning in your endless light")
            ),
            coverGradientStart = 0xFF0284C7,
            coverGradientEnd = 0xFF0369A1,
            accentGlowColor = 0xFF38BDF8,
            baseFrequency = 146.83f, // D3
            tempoBpm = 128
        ),
        Track(
            id = "t4",
            title = "Windowlicker",
            artist = "Aphex Twin",
            album = "Windowlicker EP",
            releaseDate = "22 March 1999",
            durationSeconds = 247,
            genre = "IDM",
            artworkType = ArtworkType.APHEX_TWIN,
            isVerified = true,
            badgeLabel = "LIVE",
            lyrics = listOf(
                LyricLine(0, "[Experimental vocal synthesis intro]"),
                LyricLine(14, "[Algorithmic breakbeat cascade]"),
                LyricLine(32, "J'aime faire des croquettes au chien"),
                LyricLine(50, "[Liquid glitch synthesizer lead]"),
                LyricLine(70, "[Melodic ambient crescendo]")
            ),
            coverGradientStart = 0xFF10B981,
            coverGradientEnd = 0xFF059669,
            accentGlowColor = 0xFF10B981,
            baseFrequency = 98.0f,
            tempoBpm = 128
        ),
        Track(
            id = "t5",
            title = "Pulse Matrix",
            artist = "Nelly Mes",
            album = "Modulation",
            releaseDate = "15 August 2024",
            durationSeconds = 210,
            genre = "IDM",
            artworkType = ArtworkType.NELLY_MES,
            isVerified = true,
            badgeLabel = "NEW",
            lyrics = listOf(
                LyricLine(0, "Turn the dial, find the pulse"),
                LyricLine(14, "Frequency rising in the room"),
                LyricLine(28, "Feel the beat ignite the room"),
                LyricLine(44, "Sync the rhythm, watch it bloom")
            ),
            coverGradientStart = 0xFF8B5CF6,
            coverGradientEnd = 0xFF6D28D9,
            accentGlowColor = 0xFF8B5CF6,
            baseFrequency = 123.47f,
            tempoBpm = 132
        ),
        Track(
            id = "t6",
            title = "Roygbiv",
            artist = "Boards of Canada",
            album = "Music Has the Right to Children",
            releaseDate = "20 April 1998",
            durationSeconds = 151,
            genre = "IDM",
            artworkType = ArtworkType.BOARDS_OF_CANADA,
            isVerified = true,
            badgeLabel = "CLASSIC",
            lyrics = listOf(
                LyricLine(0, "[Warm analog synth harmony]"),
                LyricLine(18, "[Vintage vocal tape snippet]"),
                LyricLine(36, "Red, orange, yellow, green, blue, indigo, violet"),
                LyricLine(60, "[Nostalgic bass groove]")
            ),
            coverGradientStart = 0xFFF59E0B,
            coverGradientEnd = 0xFFD97706,
            accentGlowColor = 0xFFF59E0B,
            baseFrequency = 87.3f,
            tempoBpm = 84
        ),
        Track(
            id = "t7",
            title = "Feel Good Inc",
            artist = "Gorillaz",
            album = "Demon Days",
            releaseDate = "09 May 2005",
            durationSeconds = 223,
            genre = "Alternative",
            artworkType = ArtworkType.GORILLAZ,
            isVerified = true,
            badgeLabel = "ESSENTIAL",
            lyrics = listOf(
                LyricLine(0, "City's breaking down on a camel's back"),
                LyricLine(10, "They just have to go, 'cause they don't know wack"),
                LyricLine(20, "So while you fill the streets, it's appealing to see"),
                LyricLine(30, "You won't get undercounted 'cause you're bad and free"),
                LyricLine(40, "Windmill, windmill for the land"),
                LyricLine(50, "Learn forever, hand in hand")
            ),
            coverGradientStart = 0xFF10B981,
            coverGradientEnd = 0xFF0F172A,
            accentGlowColor = 0xFF10B981,
            baseFrequency = 103.83f,
            tempoBpm = 138
        ),
        Track(
            id = "t8",
            title = "Midnight Horizon",
            artist = "Solar Fields",
            album = "Origin",
            releaseDate = "14 Feb 2022",
            durationSeconds = 280,
            genre = "Ambient",
            artworkType = ArtworkType.BOARDS_OF_CANADA,
            isVerified = true,
            badgeLabel = "AMBIENT",
            lyrics = listOf(
                LyricLine(0, "[Subtle atmospheric pads]"),
                LyricLine(30, "[Subharmonic drone swell]"),
                LyricLine(70, "[Shimmering delay resonance]"),
                LyricLine(120, "[Deep drift through stellar wind]")
            ),
            coverGradientStart = 0xFF064E3B,
            coverGradientEnd = 0xFF022C22,
            accentGlowColor = 0xFF10B981,
            baseFrequency = 82.41f,
            tempoBpm = 72
        ),
        Track(
            id = "t9",
            title = "Neon Velocity",
            artist = "Kavinsky",
            album = "Reborn",
            releaseDate = "25 Mar 2022",
            durationSeconds = 215,
            genre = "Electronic",
            artworkType = ArtworkType.DEPECHE_MODE,
            isVerified = true,
            badgeLabel = "SYNTH",
            lyrics = listOf(
                LyricLine(0, "Driving through the neon glow"),
                LyricLine(15, "Speeding where the shadows go"),
                LyricLine(35, "Electric heart in overdrive"),
                LyricLine(55, "Midnight makes the night alive")
            ),
            coverGradientStart = 0xFFDC2626,
            coverGradientEnd = 0xFF7F1D1D,
            accentGlowColor = 0xFFEF4444,
            baseFrequency = 130.81f,
            tempoBpm = 126
        ),
        Track(
            id = "t10",
            title = "Starlight Bloom",
            artist = "Luna Nova",
            album = "Prism Pop",
            releaseDate = "10 Jan 2024",
            durationSeconds = 198,
            genre = "Pop",
            artworkType = ArtworkType.WOODZ,
            isVerified = true,
            badgeLabel = "POP",
            lyrics = listOf(
                LyricLine(0, "Catch a falling star tonight"),
                LyricLine(12, "Hold it till the morning light"),
                LyricLine(24, "Dancing on a silver beam"),
                LyricLine(36, "Living inside a velvet dream")
            ),
            coverGradientStart = 0xFFEC4899,
            coverGradientEnd = 0xFF831843,
            accentGlowColor = 0xFFF472B6,
            baseFrequency = 146.83f,
            tempoBpm = 120
        )
    )

    val essentialCards = listOf(
        EssentialCard(
            id = "e1",
            title = "Aphex Twin",
            subtitle = "Live performance",
            badgeText = "• LIVE",
            isLive = true,
            artworkType = ArtworkType.APHEX_TWIN,
            genre = "IDM",
            trackId = "t4"
        ),
        EssentialCard(
            id = "e2",
            title = "Nelly Mes",
            subtitle = "Album presentation",
            badgeText = "NEW",
            isLive = false,
            artworkType = ArtworkType.NELLY_MES,
            genre = "IDM",
            trackId = "t5"
        ),
        EssentialCard(
            id = "e3",
            title = "Boards of Canada",
            subtitle = "Studio session",
            badgeText = "HOT",
            isLive = false,
            artworkType = ArtworkType.BOARDS_OF_CANADA,
            genre = "IDM",
            trackId = "t6"
        )
    )
}
