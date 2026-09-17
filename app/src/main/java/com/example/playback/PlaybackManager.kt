package com.example.playback

import android.content.Context
import android.media.audiofx.BassBoost
import android.media.audiofx.Equalizer
import android.net.Uri
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.model.Track
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages audio playback using Media3 ExoPlayer with support for queues, seek,
 * Equalizer, BassBoost audio effects, and state observation.
 *
 * @property context The application context used to build ExoPlayer and access media content.
 */
@Singleton
class PlaybackManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    val player: ExoPlayer = ExoPlayer.Builder(context)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                .setUsage(C.USAGE_MEDIA)
                .build(),
            true
        )
        .setHandleAudioBecomingNoisy(true)
        .build()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentTrack = MutableStateFlow<Track?>(null)
    val currentTrack: StateFlow<Track?> = _currentTrack.asStateFlow()

    private val _positionMs = MutableStateFlow(0L)
    val positionMs: StateFlow<Long> = _positionMs.asStateFlow()

    private val _durationMs = MutableStateFlow(0L)
    val durationMs: StateFlow<Long> = _durationMs.asStateFlow()

    private val _isBuffering = MutableStateFlow(false)
    val isBuffering: StateFlow<Boolean> = _isBuffering.asStateFlow()

    private var currentQueue: List<Track> = emptyList()

    private var equalizer: Equalizer? = null
    private var bassBoost: BassBoost? = null
    private var tickerJob: Job? = null

    init {
        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _isPlaying.value = isPlaying
                if (isPlaying) {
                    startTicker()
                } else {
                    stopTicker()
                }
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                _isBuffering.value = (playbackState == Player.STATE_BUFFERING)
                val duration = player.duration
                _durationMs.value = if (duration > 0) duration else 0L

                if (playbackState == Player.STATE_READY) {
                    attachAudioEffects(player.audioSessionId)
                }
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                val index = player.currentMediaItemIndex
                val track = currentQueue.getOrNull(index)
                if (track != null) {
                    _currentTrack.value = track
                }
                _positionMs.value = player.currentPosition.coerceAtLeast(0L)
                val duration = player.duration
                _durationMs.value = if (duration > 0) duration else 0L
            }
        })
    }

    /**
     * Plays a single track, setting it as the sole item in the playback queue.
     */
    fun play(track: Track) {
        playQueue(listOf(track), 0)
    }

    /**
     * Sets a queue of tracks and immediately starts playback from [startIndex].
     */
    fun playQueue(tracks: List<Track>, startIndex: Int = 0) {
        if (tracks.isEmpty()) return

        currentQueue = tracks
        val safeIndex = startIndex.coerceIn(0, tracks.lastIndex)
        _currentTrack.value = tracks[safeIndex]

        val mediaItems = tracks.map { buildMediaItem(it) }

        player.setMediaItems(mediaItems, safeIndex, 0L)
        player.prepare()
        player.play()
    }

    fun pause() {
        player.pause()
    }

    fun resume() {
        player.play()
    }

    fun togglePlayPause() {
        if (player.isPlaying) {
            pause()
        } else {
            resume()
        }
    }

    fun seekTo(ms: Long) {
        player.seekTo(ms.coerceAtLeast(0L))
        _positionMs.value = ms.coerceAtLeast(0L)
    }

    fun next() {
        if (player.hasNextMediaItem()) {
            player.seekToNextMediaItem()
        }
    }

    fun previous() {
        if (player.currentPosition > 3000L || !player.hasPreviousMediaItem()) {
            player.seekTo(0L)
        } else {
            player.seekToPreviousMediaItem()
        }
    }

    fun setShuffle(enabled: Boolean) {
        player.shuffleModeEnabled = enabled
    }

    fun setRepeatMode(mode: Int) {
        player.repeatMode = mode
    }

    /**
     * Attaches or updates [Equalizer] and [BassBoost] effects on the current ExoPlayer audioSessionId.
     */
    private fun attachAudioEffects(sessionId: Int) {
        if (sessionId == C.AUDIO_SESSION_ID_UNSET || sessionId == 0) return

        try {
            if (equalizer == null) {
                equalizer = Equalizer(0, sessionId).apply {
                    enabled = true
                }
            }
        } catch (_: Exception) {
            equalizer = null
        }

        try {
            if (bassBoost == null) {
                bassBoost = BassBoost(0, sessionId).apply {
                    enabled = true
                }
            }
        } catch (_: Exception) {
            bassBoost = null
        }
    }

    /**
     * Sets the bass boost strength level.
     *
     * @param level Strength value between 0 and 1000.
     */
    fun setBassBoost(level: Short) {
        try {
            attachAudioEffects(player.audioSessionId)
            bassBoost?.apply {
                if (!enabled) enabled = true
                setStrength(level.coerceIn(0, 1000))
            }
        } catch (_: Exception) {
        }
    }

    /**
     * Sets the gain level for the given equalizer band.
     *
     * @param band The band index.
     * @param level Gain in millibels (e.g. -1500 to +1500 mB).
     */
    fun setEqualizerBand(band: Short, level: Short) {
        try {
            attachAudioEffects(player.audioSessionId)
            equalizer?.apply {
                if (!enabled) enabled = true
                if (band in 0 until numberOfBands) {
                    setBandLevel(band, level)
                }
            }
        } catch (_: Exception) {
        }
    }

    private fun startTicker() {
        tickerJob?.cancel()
        tickerJob = scope.launch {
            while (isActive) {
                _positionMs.value = player.currentPosition.coerceAtLeast(0L)
                val duration = player.duration
                if (duration > 0L) {
                    _durationMs.value = duration
                }
                delay(500L)
            }
        }
    }

    private fun stopTicker() {
        tickerJob?.cancel()
        tickerJob = null
        _positionMs.value = player.currentPosition.coerceAtLeast(0L)
    }

    private fun buildMediaItem(track: Track): MediaItem {
        val uri = when {
            track.filePath.startsWith("content://") -> Uri.parse(track.filePath)
            track.filePath.isNotBlank() -> Uri.fromFile(File(track.filePath))
            track.id.startsWith("content://") -> Uri.parse(track.id)
            track.id.toLongOrNull() != null -> Uri.parse("content://media/external/audio/media/${track.id}")
            else -> Uri.EMPTY
        }

        val metadataBuilder = MediaMetadata.Builder()
            .setTitle(track.title)
            .setArtist(track.artist)
            .setAlbumTitle(track.album)

        val artUri = track.artworkUri ?: track.customArtworkUri
        if (!artUri.isNullOrBlank()) {
            metadataBuilder.setArtworkUri(Uri.parse(artUri))
        }

        return MediaItem.Builder()
            .setMediaId(track.id)
            .setUri(uri)
            .setMediaMetadata(metadataBuilder.build())
            .build()
    }

    fun release() {
        stopTicker()
        scope.cancel()
        try {
            equalizer?.release()
            equalizer = null
            bassBoost?.release()
            bassBoost = null
        } catch (_: Exception) {
        }
        player.release()
    }
}
