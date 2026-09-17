package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.model.ArtworkType
import com.example.ui.components.DeveloperModeBackground
import com.example.ui.components.ModernBottomBar
import com.example.ui.screens.FavoritesScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.NowPlayingScreen
import com.example.ui.screens.PlaylistDetailScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.WelcomeScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.MusicPlayerViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val musicViewModel: MusicPlayerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isDarkMode by musicViewModel.isDarkMode.collectAsState()
            MyApplicationTheme(darkTheme = isDarkMode) {
                MainAppScreen(musicViewModel = musicViewModel)
            }
        }
    }
}

@Composable
fun MainAppScreen(
    musicViewModel: MusicPlayerViewModel,
    modifier: Modifier = Modifier
) {
    val showWelcomeScreen by musicViewModel.showWelcomeScreen.collectAsState()
    val isDarkMode by musicViewModel.isDarkMode.collectAsState()
    val selectedTab by musicViewModel.selectedTab.collectAsState()
    val isNowPlayingExpanded by musicViewModel.isNowPlayingExpanded.collectAsState()
    val isNowPlayingVisible by musicViewModel.isNowPlayingVisible.collectAsState()
    val selectedPlaylist by musicViewModel.selectedPlaylist.collectAsState()
    val isSettingsOpen by musicViewModel.isSettingsOpen.collectAsState()
    val audioSettings by musicViewModel.audioSettings.collectAsState()
    val userProfile by musicViewModel.userProfile.collectAsState()
    val userPlaylists by musicViewModel.userPlaylists.collectAsState()
    val tracks by musicViewModel.tracks.collectAsState()
    val currentTrack by musicViewModel.currentTrack.collectAsState()
    val isPlaying by musicViewModel.isPlaying.collectAsState()
    val currentPositionSeconds by musicViewModel.currentPositionSeconds.collectAsState()
    val isShuffle by musicViewModel.isShuffle.collectAsState()
    val isRepeat by musicViewModel.isRepeat.collectAsState()
    val repeatMode by musicViewModel.repeatMode.collectAsState()
    val recentlyListenedTracks by musicViewModel.recentlyListenedTracks.collectAsState()
    val followedArtists by musicViewModel.followedArtists.collectAsState()
    val searchQuery by musicViewModel.searchQuery.collectAsState()

    var showSearch by remember { mutableStateOf(false) }

    // Intercept back button gracefully
    BackHandler(enabled = isNowPlayingExpanded || showSearch || selectedPlaylist != null || isSettingsOpen || selectedTab != 0) {
        when {
            isNowPlayingExpanded -> musicViewModel.collapseNowPlaying()
            showSearch -> showSearch = false
            selectedPlaylist != null -> musicViewModel.closePlaylist()
            isSettingsOpen -> musicViewModel.closeSettings()
            selectedTab != 0 -> musicViewModel.selectTab(0)
        }
    }

    if (showWelcomeScreen) {
        WelcomeScreen(
            onGetStarted = { musicViewModel.dismissWelcome() },
            onSignIn = { email, password -> musicViewModel.loginUser(email, password) },
            onSignUp = { name, email, password, handle, genres ->
                musicViewModel.registerUser(name, email, password, handle, genres)
            },
            modifier = modifier.fillMaxSize()
        )
    } else {
        val isDevMode = audioSettings.developerMode
        val baseBg = if (isDarkMode) Color(0xFF0D0F14) else Color(0xFFF9FAFB)

        Box(
            modifier = modifier
                .fillMaxSize()
                .background(if (isDevMode) Color.Black else baseBg)
        ) {
            if (isDevMode) {
                DeveloperModeBackground(modifier = Modifier.fillMaxSize())
            }

            // Main screen content router
            when {
                selectedPlaylist != null -> {
                    PlaylistDetailScreen(
                        playlist = selectedPlaylist!!,
                        tracks = tracks,
                        currentTrack = currentTrack,
                        isPlaying = isPlaying,
                        onTrackSelected = { track -> musicViewModel.playTrack(track) },
                        onPlayAll = { musicViewModel.playPlaylist(selectedPlaylist!!) },
                        onShufflePlay = { musicViewModel.playPlaylist(selectedPlaylist!!, shuffle = true) },
                        onToggleFavorite = { id -> musicViewModel.toggleFavorite(id) },
                        onUpdatePlaylist = { updated -> musicViewModel.updatePlaylist(updated) },
                        onBack = { musicViewModel.closePlaylist() },
                        isDarkMode = isDarkMode,
                        isPlaylistLiked = selectedPlaylist!!.isLiked,
                        onToggleLikePlaylist = { id -> musicViewModel.toggleLikePlaylist(id) },
                        modifier = Modifier.fillMaxSize()
                    )
                }
                showSearch -> {
                    SearchScreen(
                        query = searchQuery,
                        onQueryChange = { q -> musicViewModel.searchMusicBrainz(q) },
                        tracks = tracks,
                        currentTrack = currentTrack,
                        isPlaying = isPlaying,
                        onTrackSelected = { track -> musicViewModel.playTrack(track) },
                        onBack = { showSearch = false },
                        isDarkMode = isDarkMode,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                isSettingsOpen -> {
                    SettingsScreen(
                        settings = audioSettings,
                        onSettingsChange = { s -> musicViewModel.updateAudioSettings(s) },
                        isDarkMode = isDarkMode,
                        onToggleDarkMode = { musicViewModel.toggleDarkMode(it) },
                        onBack = { musicViewModel.closeSettings() },
                        modifier = Modifier.fillMaxSize()
                    )
                }
                else -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        when (selectedTab) {
                            0 -> HomeScreen(
                                tracks = tracks,
                                currentTrack = currentTrack,
                                isPlaying = isPlaying,
                                onTrackSelected = { track -> musicViewModel.playTrack(track) },
                                onOpenSearch = { showSearch = true },
                                onToggleFavorite = { id -> musicViewModel.toggleFavorite(id) },
                                onOpenWelcome = { musicViewModel.openWelcomeScreen() },
                                onNavigateToProfile = { musicViewModel.selectTab(2) },
                                userProfile = userProfile,
                                userPlaylists = userPlaylists,
                                onCreatePlaylist = { name, desc, img, art ->
                                    musicViewModel.createPlaylist(name, desc, img, art)
                                },
                                onPlayPlaylist = { pl -> musicViewModel.playPlaylist(pl) },
                                onOpenPlaylist = { pl -> musicViewModel.openPlaylist(pl) },
                                onOpenSettings = { musicViewModel.openSettings() },
                                recentlyListenedTracks = recentlyListenedTracks,
                                onClearRecentlyListened = { musicViewModel.clearRecentlyListened() },
                                onRemoveFromRecentlyListened = { id -> musicViewModel.removeTrackFromRecentlyListened(id) },
                                onToggleLikePlaylist = { id -> musicViewModel.toggleLikePlaylist(id) },
                                onToggleFollowArtist = { artist -> musicViewModel.toggleFollowArtist(artist) },
                                isArtistFollowed = { artist -> musicViewModel.isArtistFollowed(artist) },
                                isDarkMode = isDarkMode,
                                isDeveloperMode = isDevMode,
                                modifier = Modifier.fillMaxSize()
                            )
                            1 -> FavoritesScreen(
                                tracks = tracks,
                                currentTrack = currentTrack,
                                isPlaying = isPlaying,
                                onTrackSelected = { track -> musicViewModel.playTrack(track) },
                                isDarkMode = isDarkMode,
                                modifier = Modifier.fillMaxSize()
                            )
                            2 -> ProfileScreen(
                                isDarkMode = isDarkMode,
                                onToggleDarkMode = { musicViewModel.toggleDarkMode(it) },
                                userProfile = userProfile,
                                onUpdateProfile = { name, handle, bio, avatarId, customUri ->
                                    musicViewModel.updateUserProfile(name, handle, bio, avatarId, customUri)
                                },
                                audioSettings = audioSettings,
                                onUpdateAudioSettings = { musicViewModel.updateAudioSettings(it) },
                                onOpenFullSettings = { musicViewModel.openSettings() },
                                onLogOut = { musicViewModel.logOut() },
                                likedPlaylists = userPlaylists.filter { it.isLiked },
                                userPlaylists = userPlaylists,
                                followedArtists = followedArtists,
                                allTracks = tracks,
                                onPlaylistSelected = { pl -> musicViewModel.openPlaylist(pl) },
                                onCreatePlaylist = { name, desc ->
                                    musicViewModel.createPlaylist(name, desc, null, ArtworkType.APHEX_TWIN)
                                },
                                onToggleLikePlaylist = { id -> musicViewModel.toggleLikePlaylist(id) },
                                onToggleFollowArtist = { artist -> musicViewModel.toggleFollowArtist(artist) },
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        // Bottom Navigation Bar with integrated Mini Player
                        ModernBottomBar(
                            currentTrack = currentTrack,
                            isPlaying = isPlaying,
                            isVisible = isNowPlayingVisible,
                            selectedTab = selectedTab,
                            onTabSelected = { tab -> musicViewModel.selectTab(tab) },
                            onPlayPauseToggle = { musicViewModel.togglePlayPause() },
                            onExpandNowPlaying = { musicViewModel.expandNowPlaying() },
                            onStopPlayback = { musicViewModel.stopPlayback() },
                            isDarkMode = isDarkMode,
                            modifier = Modifier.align(Alignment.BottomCenter)
                        )
                    }
                }
            }

            // Fullscreen Now Playing Overlay
            AnimatedVisibility(
                visible = isNowPlayingExpanded,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                modifier = Modifier.fillMaxSize()
            ) {
                NowPlayingScreen(
                    track = currentTrack,
                    isPlaying = isPlaying,
                    currentPositionSeconds = currentPositionSeconds,
                    isShuffle = isShuffle,
                    isRepeat = isRepeat,
                    repeatMode = repeatMode,
                    isDarkMode = isDarkMode,
                    onCollapse = { musicViewModel.collapseNowPlaying() },
                    onPlayPauseToggle = { musicViewModel.togglePlayPause() },
                    onNextTrack = { musicViewModel.nextTrack() },
                    onPreviousTrack = { musicViewModel.previousTrack() },
                    onSeek = { fraction -> musicViewModel.seekTo(fraction) },
                    onToggleShuffle = { musicViewModel.toggleShuffle() },
                    onToggleRepeat = { musicViewModel.toggleRepeat() },
                    isArtistFollowed = musicViewModel.isArtistFollowed(currentTrack.artist),
                    onToggleFollowArtist = { artist -> musicViewModel.toggleFollowArtist(artist) },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
