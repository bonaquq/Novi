package com.example.playback

import android.content.Intent
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * MediaSessionService exposing the ExoPlayer session to the Android system
 * for foreground media playback notifications, lockscreen controls, and Android Auto.
 */
@AndroidEntryPoint
class NoviMediaSessionService : MediaSessionService() {

    @Inject
    lateinit var playbackManager: PlaybackManager

    private var mediaSession: MediaSession? = null

    override fun onCreate() {
        super.onCreate()
        if (!::playbackManager.isInitialized) {
            playbackManager = PlaybackManager(applicationContext)
        }
        mediaSession = MediaSession.Builder(this, playbackManager.player).build()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        val player = mediaSession?.player
        if (player == null || !player.playWhenReady || player.mediaItemCount == 0) {
            stopSelf()
        }
    }

    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        super.onDestroy()
    }
}
