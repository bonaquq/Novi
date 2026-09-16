package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.MusicAudioEngine
import com.example.data.local.AppDatabase
import com.example.data.repository.AuthResult
import com.example.data.repository.UserAccountRepository
import com.example.data.repository.MusicBrainzRepository
import com.example.model.ArtworkType
import com.example.model.AudioAppSettings
import com.example.model.RepeatMode
import com.example.model.SampleMusicData
import com.example.model.Track
import com.example.model.UserPlaylist
import com.example.model.UserProfile
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class MusicPlayerViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    val userAccountRepo = UserAccountRepository(database.userAccountDao())
    val musicBrainzRepo = MusicBrainzRepository()

    private val audioEngine = MusicAudioEngine(viewModelScope)

    private val _isMusicBrainzLoading = MutableStateFlow(false)
    val isMusicBrainzLoading: StateFlow<Boolean> = _isMusicBrainzLoading.asStateFlow()

    private val _tracks = MutableStateFlow(musicBrainzRepo.getCuratedMusicBrainzTracks("all"))
    val tracks: StateFlow<List<Track>> = _tracks.asStateFlow()

    private val _currentTrack = MutableStateFlow(
        musicBrainzRepo.getCuratedMusicBrainzTracks("all").first()
    )
    val currentTrack: StateFlow<Track> = _currentTrack.asStateFlow()

    // Recently listened tracks list (starts empty with all previous tracks removed)
    private val _recentlyListenedTracks = MutableStateFlow<List<Track>>(emptyList())
    val recentlyListenedTracks: StateFlow<List<Track>> = _recentlyListenedTracks.asStateFlow()

    fun clearRecentlyListened() {
        _recentlyListenedTracks.value = emptyList()
    }

    fun removeTrackFromRecentlyListened(trackId: String) {
        _recentlyListenedTracks.value = _recentlyListenedTracks.value.filter { it.id != trackId }
    }

    init {
        viewModelScope.launch {
            userAccountRepo.initializeDefaultAccountsIfNeeded()
            loadTracksFromMusicBrainz("all")
        }
    }

    fun loadTracksFromMusicBrainz(genreOrQuery: String = "all") {
        viewModelScope.launch {
            _isMusicBrainzLoading.value = true
            val result = if (genreOrQuery.equals("all", ignoreCase = true)) {
                musicBrainzRepo.getTracksByGenre("all")
            } else {
                musicBrainzRepo.getTracksByGenre(genreOrQuery)
            }

            result.onSuccess { fetchedTracks ->
                if (fetchedTracks.isNotEmpty()) {
                    _tracks.value = fetchedTracks
                    if (_tracks.value.none { it.id == _currentTrack.value.id }) {
                        _currentTrack.value = fetchedTracks.first()
                    }
                }
            }
            _isMusicBrainzLoading.value = false
        }
    }

    private var searchJob: Job? = null
    fun searchMusicBrainz(query: String) {
        _searchQuery.value = query
        searchJob?.cancel()
        if (query.isBlank()) {
            loadTracksFromMusicBrainz("all")
            return
        }

        searchJob = viewModelScope.launch {
            delay(500) // Debounce for MusicBrainz rate limits
            _isMusicBrainzLoading.value = true
            val result = musicBrainzRepo.getTracksByQuery(query)
            result.onSuccess { fetchedTracks ->
                _tracks.value = fetchedTracks
            }
            _isMusicBrainzLoading.value = false
        }
    }

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentPositionSeconds = MutableStateFlow(0)
    val currentPositionSeconds: StateFlow<Int> = _currentPositionSeconds.asStateFlow()

    private val _isShuffle = MutableStateFlow(false)
    val isShuffle: StateFlow<Boolean> = _isShuffle.asStateFlow()

    private val _repeatMode = MutableStateFlow(RepeatMode.OFF)
    val repeatMode: StateFlow<RepeatMode> = _repeatMode.asStateFlow()

    // Backward-compatible isRepeat
    val isRepeat: StateFlow<Boolean> = _repeatMode
        .map { it != RepeatMode.OFF }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    private val _isNowPlayingExpanded = MutableStateFlow(false)
    val isNowPlayingExpanded: StateFlow<Boolean> = _isNowPlayingExpanded.asStateFlow()

    // Controls whether the floating now playing bar is visible on screen (hidden initially until playback starts)
    private val _isNowPlayingVisible = MutableStateFlow(false)
    val isNowPlayingVisible: StateFlow<Boolean> = _isNowPlayingVisible.asStateFlow()

    // 0: Home, 1: Favorites, 2: Profile
    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Whether welcome/onboarding screen is shown (default true when opened)
    private val _showWelcomeScreen = MutableStateFlow(true)
    val showWelcomeScreen: StateFlow<Boolean> = _showWelcomeScreen.asStateFlow()

    // Dark / Light mode preference (Dark mode default)
    private val _isDarkMode = MutableStateFlow(true)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    // User Profile state
    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    // User Playlists state
    private val _userPlaylists = MutableStateFlow(SampleMusicData.defaultPlaylists)
    val userPlaylists: StateFlow<List<UserPlaylist>> = _userPlaylists.asStateFlow()

    // Audio Engine & Equalizer Settings state
    private val _audioSettings = MutableStateFlow(AudioAppSettings())
    val audioSettings: StateFlow<AudioAppSettings> = _audioSettings.asStateFlow()

    // Flag to open dedicated Settings Screen
    private val _isSettingsOpen = MutableStateFlow(false)
    val isSettingsOpen: StateFlow<Boolean> = _isSettingsOpen.asStateFlow()

    // Selected Playlist for viewing detail screen
    private val _selectedPlaylist = MutableStateFlow<UserPlaylist?>(null)
    val selectedPlaylist: StateFlow<UserPlaylist?> = _selectedPlaylist.asStateFlow()

    // Liked Playlists tracking (Favorites in Profile tab)
    private val _likedPlaylistIds = MutableStateFlow<Set<String>>(emptySet())
    val likedPlaylistIds: StateFlow<Set<String>> = _likedPlaylistIds.asStateFlow()

    // Followed Artists tracking (Artists in Profile tab)
    private val _followedArtists = MutableStateFlow<Set<String>>(emptySet())
    val followedArtists: StateFlow<Set<String>> = _followedArtists.asStateFlow()

    fun toggleLikePlaylist(playlistId: String) {
        val current = _likedPlaylistIds.value
        val isNowLiked = playlistId !in current
        _likedPlaylistIds.value = if (isNowLiked) current + playlistId else current - playlistId
        _userPlaylists.value = _userPlaylists.value.map {
            if (it.id == playlistId) it.copy(isLiked = isNowLiked) else it
        }
        if (_selectedPlaylist.value?.id == playlistId) {
            _selectedPlaylist.value = _selectedPlaylist.value?.copy(isLiked = isNowLiked)
        }
    }

    fun isPlaylistLiked(playlistId: String): Boolean {
        return playlistId in _likedPlaylistIds.value
    }

    fun toggleFollowArtist(artist: String) {
        val trimmed = artist.trim()
        if (trimmed.isBlank()) return
        val current = _followedArtists.value
        _followedArtists.value = if (trimmed in current) current - trimmed else current + trimmed
    }

    fun isArtistFollowed(artist: String): Boolean {
        return artist.trim() in _followedArtists.value
    }

    private var progressJob: Job? = null

    init {
        // App starts in stopped state; playback begins on user interaction
    }

    fun playTrack(track: Track) {
        _currentTrack.value = track
        _currentPositionSeconds.value = 0
        _isPlaying.value = true
        _isNowPlayingVisible.value = true
        _recentlyListenedTracks.value = listOf(track) + _recentlyListenedTracks.value.filter { it.id != track.id }
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
            _isNowPlayingVisible.value = true
            val track = _currentTrack.value
            audioEngine.play(track.baseFrequency, track.tempoBpm)
            startProgressTracking()
        }
    }

    fun stopPlayback() {
        _isPlaying.value = false
        audioEngine.pause()
        stopProgressTracking()
        _currentPositionSeconds.value = 0
    }

    fun dismissNowPlayingBar() {
        stopPlayback()
        _isNowPlayingVisible.value = false
    }

    fun showNowPlayingBar() {
        _isNowPlayingVisible.value = true
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

    fun updateUserProfile(
        name: String,
        handle: String,
        bio: String,
        avatarId: Int,
        customAvatarUri: String? = _userProfile.value.customAvatarUri
    ) {
        val updated = _userProfile.value.copy(
            name = name.ifBlank { "Audrey V." },
            handle = handle.ifBlank { "@audreymusic" },
            bio = bio,
            avatarId = avatarId,
            customAvatarUri = customAvatarUri
        )
        _userProfile.value = updated

        // Persist updates to DB
        viewModelScope.launch {
            if (updated.email.isNotBlank()) {
                userAccountRepo.updateProfile(
                    email = updated.email,
                    name = updated.name,
                    handle = updated.handle,
                    bio = updated.bio,
                    avatarId = updated.avatarId,
                    customAvatarUri = updated.customAvatarUri
                )
            }
        }
    }

    fun updateProfile(name: String, handle: String, bio: String, avatarId: Int, customAvatarUri: String? = _userProfile.value.customAvatarUri) {
        updateUserProfile(name, handle, bio, avatarId, customAvatarUri)
    }

    fun setCustomProfilePicture(uriString: String) {
        _userProfile.value = _userProfile.value.copy(customAvatarUri = uriString)
        viewModelScope.launch {
            if (_userProfile.value.email.isNotBlank()) {
                userAccountRepo.updateProfile(
                    email = _userProfile.value.email,
                    name = _userProfile.value.name,
                    handle = _userProfile.value.handle,
                    bio = _userProfile.value.bio,
                    avatarId = _userProfile.value.avatarId,
                    customAvatarUri = uriString
                )
            }
        }
    }

    fun removeCustomProfilePicture() {
        _userProfile.value = _userProfile.value.copy(customAvatarUri = null)
        viewModelScope.launch {
            if (_userProfile.value.email.isNotBlank()) {
                userAccountRepo.updateProfile(
                    email = _userProfile.value.email,
                    name = _userProfile.value.name,
                    handle = _userProfile.value.handle,
                    bio = _userProfile.value.bio,
                    avatarId = _userProfile.value.avatarId,
                    customAvatarUri = null
                )
            }
        }
    }

    suspend fun loginUser(email: String, password: String): String? {
        val result = userAccountRepo.login(email, password)
        return when (result) {
            is AuthResult.Success -> {
                val acc = result.account
                val genres = acc.favoriteGenres.split(",").filter { it.isNotBlank() }
                _userProfile.value = _userProfile.value.copy(
                    name = acc.name,
                    email = acc.email,
                    handle = acc.handle,
                    bio = acc.bio,
                    avatarId = acc.avatarId,
                    customAvatarUri = acc.customAvatarUri,
                    favoriteGenres = if (genres.isNotEmpty()) genres else listOf("IDM", "Electronic"),
                    memberSince = acc.memberSince,
                    isLoggedIn = true
                )
                dismissWelcome()
                null // null indicates success
            }
            is AuthResult.Error -> result.message
        }
    }

    suspend fun registerUser(
        name: String,
        email: String,
        password: String,
        handle: String = "",
        selectedGenres: List<String> = listOf("Electronic", "IDM"),
        customAvatarUri: String? = null
    ): String? {
        val result = userAccountRepo.registerAccount(
            name = name,
            email = email,
            password = password,
            handle = handle,
            genres = selectedGenres,
            customAvatarUri = customAvatarUri
        )
        return when (result) {
            is AuthResult.Success -> {
                val acc = result.account
                val genres = acc.favoriteGenres.split(",").filter { it.isNotBlank() }
                _userProfile.value = _userProfile.value.copy(
                    name = acc.name,
                    email = acc.email,
                    handle = acc.handle,
                    bio = acc.bio,
                    avatarId = acc.avatarId,
                    customAvatarUri = acc.customAvatarUri,
                    favoriteGenres = if (genres.isNotEmpty()) genres else selectedGenres,
                    memberSince = acc.memberSince,
                    isLoggedIn = true
                )
                dismissWelcome()
                null // null indicates success
            }
            is AuthResult.Error -> result.message
        }
    }

    fun logOut() {
        stopPlayback()
        _userProfile.value = _userProfile.value.copy(isLoggedIn = false)
        _showWelcomeScreen.value = true
    }

    fun openWelcomeScreen() {
        _showWelcomeScreen.value = true
    }

    fun openPlaylist(playlist: UserPlaylist) {
        _selectedPlaylist.value = playlist
    }

    fun closePlaylist() {
        _selectedPlaylist.value = null
    }

    fun updatePlaylist(updatedPlaylist: UserPlaylist) {
        _userPlaylists.value = _userPlaylists.value.map {
            if (it.id == updatedPlaylist.id) updatedPlaylist else it
        }
        if (_selectedPlaylist.value?.id == updatedPlaylist.id) {
            _selectedPlaylist.value = updatedPlaylist
        }
    }

    fun createPlaylist(
        name: String,
        description: String,
        customImageUri: String? = null,
        artworkType: ArtworkType = ArtworkType.APHEX_TWIN
    ) {
        val newId = "p_${System.currentTimeMillis()}"
        val gradients = listOf(
            0xFF10B981 to 0xFF047857,
            0xFF3B82F6 to 0xFF1D4ED8,
            0xFF8B5CF6 to 0xFF6D28D9,
            0xFFEC4899 to 0xFFBE185D,
            0xFFF59E0B to 0xFFB45309
        )
        val (start, end) = gradients.random()
        val newPlaylist = UserPlaylist(
            id = newId,
            name = name.ifBlank { "My Playlist" },
            description = description.ifBlank { "Created by ${_userProfile.value.name}" },
            trackCount = 0,
            coverGradientStart = start,
            coverGradientEnd = end,
            artworkType = artworkType,
            customImageUri = customImageUri,
            trackIds = emptyList()
        )
        _userPlaylists.value = listOf(newPlaylist) + _userPlaylists.value
    }

    fun playPlaylist(playlist: UserPlaylist, shuffle: Boolean = false) {
        val list = _tracks.value
        val playlistTracks = if (playlist.trackIds.isNotEmpty()) {
            val trackMap = list.associateBy { it.id }
            playlist.trackIds.mapNotNull { trackMap[it] }
        } else {
            list.take(4)
        }

        val targetTrack = if (shuffle) {
            playlistTracks.shuffled().firstOrNull() ?: list.firstOrNull()
        } else {
            playlistTracks.firstOrNull() ?: list.firstOrNull()
        }

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

    fun updateAudioSettings(settings: AudioAppSettings) {
        _audioSettings.value = settings
        if (settings.equalizerEnabled) {
            val bassBand = settings.bands.firstOrNull()?.gainDb ?: 0f
            val trebleBand = settings.bands.lastOrNull()?.gainDb ?: 0f
            val bassMult = 1.0f + (bassBand / 12f) * 0.8f + settings.bassBoostAmount * 0.5f
            val trebleMult = 1.0f + (trebleBand / 12f) * 0.8f
            audioEngine.updateEqualizer(bassMult, trebleMult)
        } else {
            audioEngine.updateEqualizer(1.0f, 1.0f)
        }
    }

    fun openSettings() {
        _isSettingsOpen.value = true
    }

    fun closeSettings() {
        _isSettingsOpen.value = false
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
