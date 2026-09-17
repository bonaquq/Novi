package com.example.data.local

import android.content.Context
import android.provider.MediaStore
import com.example.model.ArtworkType
import com.example.model.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Scanner for querying and retrieving local audio files stored on the device via [MediaStore].
 *
 * Supports all OS-indexed audio formats including MP3, FLAC, OGG, OPUS, M4A, AAC, WAV, WMA, and AMR.
 *
 * @property context The application or activity [Context] used to access the [android.content.ContentResolver].
 */
class LocalMusicScanner(private val context: Context) {

    /**
     * Scans the device's external storage for audio tracks matching standard music criteria.
     *
     * Executes asynchronously on [Dispatchers.IO], querying [MediaStore.Audio.Media.EXTERNAL_CONTENT_URI]
     * for tracks where `IS_MUSIC != 0` and `DURATION > 5000` (greater than 5 seconds to filter out short sound effects).
     * Maps each resulting row to a [Track] model, and builds the corresponding album artwork URI.
     *
     * Supports all OS-indexed audio formats (mp3, flac, ogg, opus, m4a, aac, wav, wma, amr).
     * If the query fails, a [SecurityException] is thrown due to missing permissions, or the cursor is null,
     * this function safely returns an empty list.
     *
     * @return A list of scanned [Track] objects, or an empty list if none are found or an error occurs.
     */
    suspend fun scanAudioFiles(): List<Track> = withContext(Dispatchers.IO) {
        val tracks = mutableListOf<Track>()

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.MIME_TYPE
        )

        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0 AND ${MediaStore.Audio.Media.DURATION} > 5000"
        val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"

        try {
            val contentResolver = context.contentResolver
            val cursor = contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                sortOrder
            ) ?: return@withContext emptyList()

            cursor.use { c ->
                val idColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val titleColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val artistColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val albumColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                val albumIdColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
                val durationColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                val dataColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
                val sizeColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
                val dateAddedColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)
                val mimeTypeColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Media.MIME_TYPE)

                val dateFormat = SimpleDateFormat("yyyy", Locale.getDefault())

                while (c.moveToNext()) {
                    val id = c.getLong(idColumn).toString()
                    val title = c.getString(titleColumn)?.takeIf { it.isNotBlank() } ?: "Unknown Title"
                    val rawArtist = c.getString(artistColumn)
                    val artist = if (rawArtist.isNullOrBlank() || rawArtist == "<unknown>") "Unknown Artist" else rawArtist
                    val rawAlbum = c.getString(albumColumn)
                    val album = if (rawAlbum.isNullOrBlank() || rawAlbum == "<unknown>") "Unknown Album" else rawAlbum
                    val albumId = c.getLong(albumIdColumn)
                    val durationMs = c.getLong(durationColumn)
                    val durationSeconds = (durationMs / 1000).toInt()
                    val filePath = c.getString(dataColumn) ?: ""
                    val sizeBytes = c.getLong(sizeColumn)
                    val dateAdded = c.getLong(dateAddedColumn)
                    val mimeType = c.getString(mimeTypeColumn) ?: "audio/mpeg"

                    val artworkUri = "content://media/external/audio/albumart/$albumId"

                    val releaseDate = if (dateAdded > 0L) {
                        dateFormat.format(Date(dateAdded * 1000L))
                    } else {
                        "Unknown"
                    }

                    val artworkType = when (albumId % 7) {
                        0L -> ArtworkType.SONIC_YOUTH
                        1L -> ArtworkType.DEPECHE_MODE
                        2L -> ArtworkType.WOODZ
                        3L -> ArtworkType.APHEX_TWIN
                        4L -> ArtworkType.NELLY_MES
                        5L -> ArtworkType.BOARDS_OF_CANADA
                        else -> ArtworkType.GORILLAZ
                    }

                    tracks.add(
                        Track(
                            id = id,
                            title = title,
                            artist = artist,
                            album = album,
                            releaseDate = releaseDate,
                            durationSeconds = durationSeconds,
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
                    )
                }
            }
        } catch (e: SecurityException) {
            return@withContext emptyList()
        } catch (e: Exception) {
            return@withContext emptyList()
        }

        tracks
    }
}
