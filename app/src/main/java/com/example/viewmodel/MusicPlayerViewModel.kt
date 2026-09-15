package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.ArtworkType
import com.example.model.RepeatMode
import com.example.model.SampleMusicData
import com.example.model.Track
import com.example.model.UserPlaylist
import com.example.model.UserProfile
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class MusicPlayerViewModel : ViewModel() {

    private val audioEngine = MusicAudioEngine(viewModelScope)

    private val _tracks = MutableStateFlow(SampleMusicData.tracks)
    val tracks: StateFlow<List<Track>> = _tracks.asStateFlow()

    // Default current track: Bone by Sonic Youth (from screenshot 2 and 3!)
    private val _currentTrack = MutableStateFlow(
        SampleMusicData.tracks.find { it.title == "Bone" } ?: SampleMusicData.tracks.first()
    )
    val currentTrack: StateFlow<Track> = _currentTrack.asStateFlow()

    private val _isPlaying = MutableStateFlow(true)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    // Default position: 24s so the highlighted lyric line from screenshot 3 ("Walk on by, look to the left") is active!
    private val _currentPositionSeconds = MutableStateFlow(24)
    val currentPositionSeconds: StateFlow<Int> = _currentPositionSeconds.asStateFlow()

    private val _isShuffle = MutableStateFlow(false)
    val isShuffle: StateFlow<Boolean> = _isShuffle.asStateFlow()

    private val _repeatMode = MutableStateFlow(RepeatMode.OFF)
    val repeatMode: StateFlow<RepeatMode> = _repeatMode.asStateFlow()

    // Backward-compatible isRepeat
    val isRepeat: StateFlow<Boolean> = MutableStateFlow(false).apply {
        // Will be updated via repeatMode
    }

    private val _isNowPlayingExpanded = MutableStateFlow(false)
    val isNowPlayingExpanded: StateFlow<Boolean> = _isNowPlayingExpanded.asStateFlow()

    // 0: Home, 1: Favorites, 2: Profile
    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Whether welcome/onboarding screen is shown
    private val _showWelcomeScreen = MutableStateFlow(false)
    val showWelcomeScreen: StateFlow<Boolean> = _showWelcomeScreen.asStateFlow()

    // Dark / Light mode preference
    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    // User Profile state
    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    // User Playlists state
    private val _userPlaylists = MutableStateFlow(SampleMusicData.defaultPlaylists)
    val userPlaylists: StateFlow<List<UserPlaylist>> = _userPlaylists.asStateFlow()

    private var progressJob: Job? = null

    init {
        // Start playback of initial track
        val initial = _currentTrack.value
        audioEngine.play(initial.baseFrequency, initial.tempoBpm)
        startProgressTracking()
    }

    fun playTrack(track: Track) {
        _currentTrack.value = track
        _currentPositionSeconds.value = 0
        _isPlaying.value = true
        audioEngine.play(track.baseFrequency, track.tempoBpm)
        startProgressTracking()
    }

    fun togglePlayPause() {
        if (_isPlaying.value) {
            _isPlaying.value = false
            audioEngine.pause()
            stopProgressTracking()
        } else {
            _isPlaying.value = true
            val track = _currentTrack.value
            audioEngine.play(track.baseFrequency, track.tempoBpm)
            startProgressTracking()
        }
    }

    fun nextTrack() {
        val list = _tracks.value
        val currentIndex = list.indexOfFirst { it.id == _currentTrack.value.id }
        val nextIndex = if (_isShuffle.value) {
            (list.indices).filter { it != currentIndex }.randomOrNull() ?: 0
        } else {
            (currentIndex + 1) % list.size
        }
        playTrack(list[nextIndex])
    }

    fun previousTrack() {
        if (_currentPositionSeconds.value > 3) {
            seekTo(0f)
            return
        }
        val list = _tracks.value
        val currentIndex = list.indexOfFirst { it.id == _currentTrack.value.id }
        val prevIndex = if (currentIndex - 1 < 0) list.size - 1 else currentIndex - 1
        playTrack(list[prevIndex])
    }

    fun seekTo(fraction: Float) {
        val totalSecs = _currentTrack.value.durationSeconds
        val targetSecs = (totalSecs * fraction.coerceIn(0f, 1f)).toInt()
        _currentPositionSeconds.value = targetSecs
    }

    fun toggleFavorite(trackId: String) {
        _tracks.value = _tracks.value.map {
            if (it.id == trackId) it.copy(isFavorite = !it.isFavorite) else it
        }
        if (_currentTrack.value.id == trackId) {
            _currentTrack.value = _currentTrack.value.copy(
                isFavorite = !_currentTrack.value.isFavorite
            )
        }
    }

    fun toggleShuffle() {
        _isShuffle.value = !_isShuffle.value
    }

    /**
     * Repeat button:
     * When pressed once: repeats once through the queue (RepeatMode.ALL).
     * When pressed once more: repeats the current song that's playing (RepeatMode.ONE).
     * When pressed again: off (RepeatMode.OFF).
     */
    fun toggleRepeat() {
        _repeatMode.value = when (_repeatMode.value) {
            RepeatMode.OFF -> RepeatMode.ALL
            RepeatMode.ALL -> RepeatMode.ONE
            RepeatMode.ONE -> RepeatMode.OFF
        }
    }

    fun setNowPlayingExpanded(expanded: Boolean) {
        _isNowPlayingExpanded.value = expanded
    }

    fun expandNowPlaying() {
        setNowPlayingExpanded(true)
    }

    fun collapseNowPlaying() {
        setNowPlayingExpanded(false)
    }

    fun selectTab(tab: Int) {
        _selectedTab.value = tab
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun showWelcome() {
        _showWelcomeScreen.value = true
    }

    fun dismissWelcome() {
        _showWelcomeScreen.value = false
    }

    fun toggleDarkMode(enabled: Boolean) {
        _isDarkMode.value = enabled
    }

    fun updateUserProfile(name: String, handle: String, bio: String, avatarId: Int) {
        _userProfile.value = _userProfile.value.copy(
            name = name.ifBlank { "Audrey V." },
            handle = handle.ifBlank { "@audreymusic" },
            bio = bio,
            avatarId = avatarId
        )
    }

    fun signIn(email: String, name: String) {
        _userProfile.value = _userProfile.value.copy(
            email = email.ifBlank { "user@novimusic.io" },
            name = name.ifBlank { email.substringBefore("@").replaceFirstChar { it.uppercase() } },
            isLoggedIn = true
        )
        dismissWelcome()
    }

    fun createPlaylist(name: String, description: String) {
        val newId = "p_${System.currentTimeMillis()}"
        val gradients = listOf(
            0xFF10B981 to 0xFF047857,
            0xFF3B82F6 to 0xFF1D4ED8,
            0xFF8B5CF6 to 0xFF6D28D9,
            0xFFEC4899 to 0xFFBE185D,
            0xFFF59E0B to 0xFFB45309
        )
        val (start, end) = gradients.random()
        val artworks = listOf(ArtworkType.APHEX_TWIN, ArtworkType.BOARDS_OF_CANADA, ArtworkType.DEPECHE_MODE, ArtworkType.WOODZ)
        val newPlaylist = UserPlaylist(
            id = newId,
            name = name.ifBlank { "My Playlist" },
            description = description.ifBlank { "Created by ${_userProfile.value.name}" },
            trackCount = 0,
            coverGradientStart = start,
            coverGradientEnd = end,
            artworkType = artworks.random(),
            trackIds = emptyList()
        )
        _userPlaylists.value = listOf(newPlaylist) + _userPlaylists.value
    }

    fun playPlaylist(playlist: UserPlaylist) {
        val list = _tracks.value
        val targetTrack = playlist.trackIds.firstNotNullOfOrNull { id -> list.find { it.id == id } }
            ?: list.firstOrNull()
        targetTrack?.let { playTrack(it) }
    }

    private fun startProgressTracking() {
        progressJob?.cancel()
        progressJob = viewModelScope.launch {
            while (isActive && _isPlaying.value) {
                delay(1000)
                val current = _currentPositionSeconds.value
                val total = _currentTrack.value.durationSeconds
                if (current + 1 >= total) {
                    when (_repeatMode.value) {
                        RepeatMode.ONE -> {
                            // Repeat the current song that's playing
                            _currentPositionSeconds.value = 0
                            val track = _currentTrack.value
                            audioEngine.play(track.baseFrequency, track.tempoBpm)
                        }
                        RepeatMode.ALL -> {
                            // Repeat through queue / wrap around
                            nextTrack()
                        }
                        RepeatMode.OFF -> {
                            val list = _tracks.value
                            val currentIndex = list.indexOfFirst { it.id == _currentTrack.value.id }
                            if (currentIndex < list.size - 1) {
                                nextTrack()
                            } else {
                                _isPlaying.value = false
                                _currentPositionSeconds.value = 0
                                audioEngine.stop()
                                stopProgressTracking()
                            }
                        }
                    }
                } else {
                    _currentPositionSeconds.value = current + 1
                }
            }
        }
    }

    private fun stopProgressTracking() {
        progressJob?.cancel()
        progressJob = null
    }

    override fun onCleared() {
        super.onCleared()
        audioEngine.stop()
    }
}
