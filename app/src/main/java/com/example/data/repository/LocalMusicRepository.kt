package com.example.data.repository

import com.example.data.local.LocalMusicScanner
import com.example.data.local.LocalTrackDao
import com.example.data.local.LocalTrackEntity
import com.example.model.ArtworkType
import com.example.model.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository responsible for scanning, persisting, and retrieving local audio files.
 *
 * Coordinates between [LocalMusicScanner] to query device audio files and [LocalTrackDao]
 * for Room database persistence and observation.
 *
 * @property scanner The [LocalMusicScanner] used to scan audio files from the device.
 * @property dao The [LocalTrackDao] used for caching and querying local tracks.
 */
@Singleton
class LocalMusicRepository @Inject constructor(
    private val scanner: LocalMusicScanner,
    private val dao: LocalTrackDao
) {

    /**
     * Refreshes the local audio cache.
     *
     * Scans for media files via [scanner], clears existing cached entries in [dao],
     * inserts the fresh track entities, and returns the total count of loaded tracks.
     *
     * @return [Result] containing the count of tracks loaded, or [Result.failure] if an error occurred.
     */
    suspend fun refresh(): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val scannedTracks = scanner.scanAudioFiles()
            val entities = scannedTracks.map { it.toEntity() }
            dao.deleteAll()
            dao.insertAll(entities)
            Result.success(entities.size)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Observes all locally stored tracks sorted alphabetically by title.
     *
     * @return A [Flow] emitting the updated list of [Track] domain models.
     */
    fun getAllTracks(): Flow<List<Track>> {
        return dao.getAllTracks().map { entities ->
            entities.map { it.toTrack() }
        }
    }

    /**
     * Searches for local tracks matching the specified query string across title, artist, or album.
     *
     * @param query The search filter keyword.
     * @return A [Flow] emitting the filtered list of [Track] domain models.
     */
    fun searchTracks(query: String): Flow<List<Track>> {
        return dao.searchTracks(query).map { entities ->
            entities.map { it.toTrack() }
        }
    }

    /**
     * Maps a [LocalTrackEntity] database model to the application's [Track] domain model.
     */
    private fun LocalTrackEntity.toTrack(): Track {
        val artworkType = when (albumId % 7) {
            0L -> ArtworkType.SONIC_YOUTH
            1L -> ArtworkType.DEPECHE_MODE
            2L -> ArtworkType.WOODZ
            3L -> ArtworkType.APHEX_TWIN
            4L -> ArtworkType.NELLY_MES
            5L -> ArtworkType.BOARDS_OF_CANADA
            else -> ArtworkType.GORILLAZ
        }

        return Track(
            id = id,
            title = title,
            artist = artist,
            album = album,
            releaseDate = if (dateAdded > 0L) {
                java.text.SimpleDateFormat("yyyy", java.util.Locale.getDefault())
                    .format(java.util.Date(dateAdded * 1000L))
            } else {
                "Unknown"
            },
            durationSeconds = (durationMs / 1000).toInt(),
            genre = "Local Audio",
            artworkType = artworkType,
            isVerified = false,
            coverGradientStart = 0xFF10B981,
            coverGradientEnd = 0xFF047857,
            accentGlowColor = 0xFF10B981,
            customArtworkUri = artworkUri,
            albumId = albumId,
            filePath = filePath,
            mimeType = mimeType,
            sizeBytes = sizeBytes,
            dateAdded = dateAdded,
            artworkUri = artworkUri
        )
    }

    /**
     * Maps an application [Track] domain model to a [LocalTrackEntity] database model.
     */
    private fun Track.toEntity(): LocalTrackEntity {
        return LocalTrackEntity(
            id = id,
            title = title,
            artist = artist,
            album = album,
            albumId = albumId,
            durationMs = durationSeconds * 1000L,
            filePath = filePath,
            mimeType = mimeType,
            sizeBytes = sizeBytes,
            dateAdded = dateAdded,
            artworkUri = artworkUri ?: customArtworkUri
        )
    }
}
