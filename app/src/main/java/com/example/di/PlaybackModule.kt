package com.example.di

import android.content.Context
import com.example.playback.PlaybackManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module that provides the application-wide [PlaybackManager] singleton.
 */
@Module
@InstallIn(SingletonComponent::class)
object PlaybackModule {

    @Provides
    @Singleton
    fun providePlaybackManager(
        @ApplicationContext context: Context
    ): PlaybackManager {
        return PlaybackManager(context)
    }
}
