package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
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
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.SearchScreen
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
    val selectedTab by viewModel.selectedTab.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val showWelcomeScreen by viewModel.showWelcomeScreen.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()

    var isSearchActive by remember { mutableStateOf(false) }

    // Handle back button when Now Playing is full screen or Search is active
    BackHandler(enabled = isNowPlayingExpanded || isSearchActive || showWelcomeScreen) {
        when {
            isNowPlayingExpanded -> viewModel.collapseNowPlaying()
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
                onGetStarted = { viewModel.dismissWelcome() }
            )
        } else {
            // Main App Content with Bottom Bar
            Box(modifier = Modifier.fillMaxSize()) {
                if (isSearchActive) {
                    SearchScreen(
                        query = searchQuery,
                        onQueryChange = { viewModel.setSearchQuery(it) },
                        tracks = tracks,
                        currentTrack = currentTrack,
                        isPlaying = isPlaying,
                        onTrackSelected = {
                            viewModel.playTrack(it)
                            viewModel.expandNowPlaying()
                            isSearchActive = false
                        },
                        isDarkMode = isDarkMode
                    )
                } else {
                    when (selectedTab) {
                        0 -> {
                            // Screen 2: Home / Discover Screen (IDM Essentials & Recently Listened)
                            HomeScreen(
                                tracks = tracks,
                                currentTrack = currentTrack,
                                isPlaying = isPlaying,
                                onTrackSelected = {
                                    viewModel.playTrack(it)
                                    viewModel.expandNowPlaying()
                                },
                                onOpenSearch = { isSearchActive = true },
                                onToggleFavorite = { viewModel.toggleFavorite(it) },
                                onOpenWelcome = { viewModel.showWelcome() },
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
                                    viewModel.expandNowPlaying()
                                },
                                isDarkMode = isDarkMode
                            )
                        }
                        2 -> {
                            ProfileScreen(
                                isDarkMode = isDarkMode,
                                onToggleDarkMode = { viewModel.toggleDarkMode(it) }
                            )
                        }
                    }
                }

                // Bottom Navigation Bar with Floating Mini Player (Screenshot 2)
                if (!isNowPlayingExpanded) {
                    ModernBottomBar(
                        currentTrack = currentTrack,
                        isPlaying = isPlaying,
                        selectedTab = if (isSearchActive) -1 else selectedTab,
                        onTabSelected = { tabIndex ->
                            isSearchActive = false
                            viewModel.selectTab(tabIndex)
                        },
                        onPlayPauseToggle = { viewModel.togglePlayPause() },
                        onExpandNowPlaying = { viewModel.expandNowPlaying() },
                        isDarkMode = isDarkMode,
                        modifier = Modifier.align(Alignment.BottomCenter)
                    )
                }
            }
        }

        // Screen 3: Fullscreen Now Playing / Lyrics Screen (Slide In/Out Transition)
        AnimatedContent(
            targetState = isNowPlayingExpanded,
            transitionSpec = {
                (slideInVertically(initialOffsetY = { it }) + fadeIn()) togetherWith
                        (slideOutVertically(targetOffsetY = { it }) + fadeOut())
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
