package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
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

class MainActivity : ComponentActivity() {

    private val viewModel: MusicPlayerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isDarkMode by viewModel.isDarkMode.collectAsState()
            MyApplicationTheme(darkTheme = isDarkMode) {
                MusicPlayerApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MusicPlayerApp(
    viewModel: MusicPlayerViewModel,
    modifier: Modifier = Modifier
) {
    val tracks by viewModel.tracks.collectAsState()
    val currentTrack by viewModel.currentTrack.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val currentPositionSeconds by viewModel.currentPositionSeconds.collectAsState()
    val isShuffle by viewModel.isShuffle.collectAsState()
    val isRepeat by viewModel.isRepeat.collectAsState()
    val isNowPlayingExpanded by viewModel.isNowPlayingExpanded.collectAsState()
    val isNowPlayingVisible by viewModel.isNowPlayingVisible.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val showWelcomeScreen by viewModel.showWelcomeScreen.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()

    val isSettingsOpen by viewModel.isSettingsOpen.collectAsState()
    val selectedPlaylist by viewModel.selectedPlaylist.collectAsState()

    var isSearchActive by remember { mutableStateOf(false) }

    // Handle back button when Now Playing is full screen, Playlist is open, Settings open, or Search is active
    BackHandler(enabled = isNowPlayingExpanded || selectedPlaylist != null || isSettingsOpen || isSearchActive || showWelcomeScreen) {
        when {
            isNowPlayingExpanded -> viewModel.collapseNowPlaying()
            selectedPlaylist != null -> viewModel.closePlaylist()
            isSettingsOpen -> viewModel.closeSettings()
            isSearchActive -> isSearchActive = false
            showWelcomeScreen -> viewModel.dismissWelcome()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(if (isDarkMode) Color(0xFF0D0F14) else Color(0xFFF9FAFB))
    ) {
        // Screen 1: Welcome / Onboarding Screen ("Music without borders")
        if (showWelcomeScreen) {
            WelcomeScreen(
                onGetStarted = { viewModel.dismissWelcome() },
                onSignIn = { email, password ->
                    viewModel.loginUser(email, password)
                },
                onSignUp = { name, email, password, handle, genres ->
                    viewModel.registerUser(name, email, password, handle, genres)
                }
            )
        } else {
            // Main App Content with Bottom Bar
            val userProfile by viewModel.userProfile.collectAsState()
            val userPlaylists by viewModel.userPlaylists.collectAsState()
            val audioSettings by viewModel.audioSettings.collectAsState()
            val repeatMode by viewModel.repeatMode.collectAsState()

            Box(modifier = Modifier.fillMaxSize()) {
                if (selectedPlaylist != null) {
                    PlaylistDetailScreen(
                        playlist = selectedPlaylist!!,
                        tracks = tracks,
                        currentTrack = currentTrack,
                        isPlaying = isPlaying,
                        onTrackSelected = {
                            viewModel.playTrack(it)
                        },
                        onPlayAll = {
                            viewModel.playPlaylist(selectedPlaylist!!, shuffle = false)
                        },
                        onShufflePlay = {
                            viewModel.playPlaylist(selectedPlaylist!!, shuffle = true)
                        },
                        onToggleFavorite = {
                            viewModel.toggleFavorite(it)
                        },
                        onUpdatePlaylist = {
                            viewModel.updatePlaylist(it)
                        },
                        onBack = {
                            viewModel.closePlaylist()
                        },
                        isDarkMode = isDarkMode
                    )
                } else if (isSettingsOpen) {
                    SettingsScreen(
                        settings = audioSettings,
                        onSettingsChange = { viewModel.updateAudioSettings(it) },
                        isDarkMode = isDarkMode,
                        onToggleDarkMode = { viewModel.toggleDarkMode(it) },
                        onBack = { viewModel.closeSettings() }
                    )
                } else if (isSearchActive) {
                    SearchScreen(
                        query = searchQuery,
                        onQueryChange = { viewModel.setSearchQuery(it) },
                        tracks = tracks,
                        currentTrack = currentTrack,
                        isPlaying = isPlaying,
                        onTrackSelected = {
                            viewModel.playTrack(it)
                            // Play in the playing bar without full expansion as requested
                        },
                        onBack = { isSearchActive = false },
                        isDarkMode = isDarkMode
                    )
                } else {
                    when (selectedTab) {
                        0 -> {
                            // Screen 2: Home Screen (Playlists & Recently Listened)
                            HomeScreen(
                                tracks = tracks,
                                currentTrack = currentTrack,
                                isPlaying = isPlaying,
                                onTrackSelected = {
                                    viewModel.playTrack(it)
                                    // Plays directly in the playing bar
                                },
                                onOpenSearch = { isSearchActive = true },
                                onToggleFavorite = { viewModel.toggleFavorite(it) },
                                onOpenWelcome = { viewModel.showWelcome() },
                                onNavigateToProfile = { viewModel.selectTab(2) },
                                userProfile = userProfile,
                                userPlaylists = userPlaylists,
                                onCreatePlaylist = { name, desc, imageUri, artworkType ->
                                    viewModel.createPlaylist(name, desc, imageUri, artworkType)
                                },
                                onPlayPlaylist = { playlist ->
                                    viewModel.playPlaylist(playlist)
                                },
                                onOpenPlaylist = { playlist ->
                                    viewModel.openPlaylist(playlist)
                                },
                                onOpenSettings = { viewModel.openSettings() },
                                isDarkMode = isDarkMode
                            )
                        }
                        1 -> {
                            FavoritesScreen(
                                tracks = tracks,
                                currentTrack = currentTrack,
                                isPlaying = isPlaying,
                                onTrackSelected = {
                                    viewModel.playTrack(it)
                                },
                                isDarkMode = isDarkMode
                            )
                        }
                        2 -> {
                            ProfileScreen(
                                isDarkMode = isDarkMode,
                                onToggleDarkMode = { viewModel.toggleDarkMode(it) },
                                userProfile = userProfile,
                                onUpdateProfile = { name, handle, bio, avatarId, customAvatarUri ->
                                    viewModel.updateProfile(name, handle, bio, avatarId, customAvatarUri)
                                },
                                audioSettings = audioSettings,
                                onUpdateAudioSettings = { viewModel.updateAudioSettings(it) },
                                onOpenFullSettings = { viewModel.openSettings() },
                                onLogOut = { viewModel.logOut() }
                            )
                        }
                    }
                }

                // Bottom Navigation Bar with Floating Mini Player
                if (!isNowPlayingExpanded && !isSettingsOpen) {
                    ModernBottomBar(
                        currentTrack = currentTrack,
                        isPlaying = isPlaying,
                        isVisible = isNowPlayingVisible,
                        selectedTab = if (isSearchActive) -1 else selectedTab,
                        onTabSelected = { tabIndex ->
                            isSearchActive = false
                            viewModel.closeSettings()
                            viewModel.selectTab(tabIndex)
                        },
                        onPlayPauseToggle = { viewModel.togglePlayPause() },
                        onExpandNowPlaying = { viewModel.expandNowPlaying() },
                        onStopPlayback = { viewModel.dismissNowPlayingBar() },
                        isDarkMode = isDarkMode,
                        modifier = Modifier.align(Alignment.BottomCenter)
                    )
                }
            }
        }

        // Screen 3: Fullscreen Now Playing / Lyrics Screen (Smooth Slide In/Out Transition)
        val repeatMode by viewModel.repeatMode.collectAsState()
        AnimatedContent(
            targetState = isNowPlayingExpanded,
            transitionSpec = {
                (slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    )
                ) + fadeIn(animationSpec = tween(300, easing = FastOutSlowInEasing))) togetherWith
                        (slideOutVertically(
                            targetOffsetY = { it },
                            animationSpec = tween(280, easing = FastOutSlowInEasing)
                        ) + fadeOut(animationSpec = tween(200, easing = FastOutSlowInEasing)))
            },
            label = "now_playing_transition"
        ) { expanded ->
            if (expanded) {
                NowPlayingScreen(
                    track = currentTrack,
                    isPlaying = isPlaying,
                    currentPositionSeconds = currentPositionSeconds,
                    isShuffle = isShuffle,
                    isRepeat = isRepeat,
                    repeatMode = repeatMode,
                    isDarkMode = isDarkMode,
                    onCollapse = { viewModel.collapseNowPlaying() },
                    onPlayPauseToggle = { viewModel.togglePlayPause() },
                    onNextTrack = { viewModel.nextTrack() },
                    onPreviousTrack = { viewModel.previousTrack() },
                    onSeek = { viewModel.seekTo(it) },
                    onToggleShuffle = { viewModel.toggleShuffle() },
                    onToggleRepeat = { viewModel.toggleRepeat() }
                )
            }
        }
    }
}
