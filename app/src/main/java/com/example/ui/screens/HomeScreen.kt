package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlaylistPlay
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.SampleMusicData
import com.example.model.Track
import com.example.model.UserPlaylist
import com.example.model.UserProfile
import com.example.ui.components.TrackArtworkDisplay
import com.example.ui.components.UserAvatarView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    tracks: List<Track>,
    currentTrack: Track,
    isPlaying: Boolean,
    onTrackSelected: (Track) -> Unit,
    onOpenSearch: () -> Unit,
    onToggleFavorite: (String) -> Unit,
    onOpenWelcome: () -> Unit,
    onNavigateToProfile: () -> Unit,
    userProfile: UserProfile = UserProfile(),
    userPlaylists: List<UserPlaylist> = SampleMusicData.defaultPlaylists,
    onCreatePlaylist: (name: String, description: String) -> Unit = { _, _ -> },
    onPlayPlaylist: (UserPlaylist) -> Unit = {},
    onOpenSettings: () -> Unit = {},
    modifier: Modifier = Modifier,
    isDarkMode: Boolean = false
) {
    var selectedCategory by remember { mutableStateOf("All") }
    val categories = SampleMusicData.categories

    val screenBg = if (isDarkMode) Color(0xFF0D0F14) else Color(0xFFF9FAFB)
    val buttonBg = if (isDarkMode) Color(0xFF1A1D24) else Color.White
    val borderColor = if (isDarkMode) Color(0xFF2D323F) else Color(0xFFE5E7EB)
    val iconTint = if (isDarkMode) Color.White else Color(0xFF111827)
    val textPrimary = if (isDarkMode) Color.White else Color(0xFF111827)
    val textSecondary = if (isDarkMode) Color(0xFF9CA3AF) else Color(0xFF6B7280)

    var showNotificationsDialog by remember { mutableStateOf(false) }
    var selectedMenuTrack by remember { mutableStateOf<Track?>(null) }
    var showAccountMenu by remember { mutableStateOf(false) }
    var activeAccountDialogType by remember { mutableStateOf<String?>(null) }
    var showCreatePlaylistSheet by remember { mutableStateOf(false) }
    var newPlaylistName by remember { mutableStateOf("") }
    var newPlaylistDesc by remember { mutableStateOf("") }
    val playlistSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Filter tracks based on category if not All
    val filteredTracks = remember(selectedCategory, tracks) {
        if (selectedCategory == "All") tracks
        else tracks.filter { it.genre.equals(selectedCategory, ignoreCase = true) }.ifEmpty { tracks }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(screenBg)
            .statusBarsPadding()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {
            // 1. Top Header Bar: User Avatar & App Name "Novi" (capital N) + Search & Notifications
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left: User Profile Avatar + App Name "Novi"
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box {
                            UserAvatarView(
                                modifier = Modifier
                                    .size(42.dp)
                                    .testTag("user_avatar")
                                    .clickable { showAccountMenu = true },
                                avatarId = userProfile.avatarId,
                                borderColor = borderColor
                            )

                            // Dropdown options when avatar is clicked:
                            // Settings, Account, Updates, Recents, Support, About
                            DropdownMenu(
                                expanded = showAccountMenu,
                                onDismissRequest = { showAccountMenu = false },
                                modifier = Modifier.background(if (isDarkMode) Color(0xFF1A1D24) else Color.White)
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Account (${userProfile.name})", color = textPrimary) },
                                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF10B981)) },
                                    onClick = {
                                        showAccountMenu = false
                                        onNavigateToProfile()
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Settings & Audio", color = textPrimary) },
                                    leadingIcon = { Icon(Icons.Default.Settings, contentDescription = null, tint = Color(0xFF10B981)) },
                                    onClick = {
                                        showAccountMenu = false
                                        onOpenSettings()
                                    },
                                    modifier = Modifier.testTag("account_menu_settings")
                                )
                                DropdownMenuItem(
                                    text = { Text("Updates", color = textPrimary) },
                                    leadingIcon = { Icon(Icons.Default.SystemUpdate, contentDescription = null, tint = iconTint) },
                                    onClick = {
                                        showAccountMenu = false
                                        activeAccountDialogType = "Updates"
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Recents", color = textPrimary) },
                                    leadingIcon = { Icon(Icons.Default.History, contentDescription = null, tint = iconTint) },
                                    onClick = {
                                        showAccountMenu = false
                                        activeAccountDialogType = "Recents"
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Support", color = textPrimary) },
                                    leadingIcon = { Icon(Icons.Default.HelpOutline, contentDescription = null, tint = iconTint) },
                                    onClick = {
                                        showAccountMenu = false
                                        activeAccountDialogType = "Support"
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("About Novi", color = textPrimary) },
                                    leadingIcon = { Icon(Icons.Default.Info, contentDescription = null, tint = iconTint) },
                                    onClick = {
                                        showAccountMenu = false
                                        activeAccountDialogType = "About"
                                    }
                                )
                            }
                        }

                        // App Name "Novi" (Capital N) next to user profile
                        Text(
                            text = "Novi",
                            color = textPrimary,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = (-0.5).sp,
                            modifier = Modifier.testTag("app_branding_novi")
                        )
                    }

                    // Right: Outlined Action Buttons: Search and Notifications
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Search Button
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .border(1.2.dp, borderColor, RoundedCornerShape(12.dp))
                                .background(buttonBg)
                                .clickable { onOpenSearch() }
                                .testTag("search_icon_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = iconTint,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Notification Button
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .border(1.2.dp, borderColor, RoundedCornerShape(12.dp))
                                .background(buttonBg)
                                .clickable { showNotificationsDialog = true }
                                .testTag("notifications_icon_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsNone,
                                contentDescription = "Notifications",
                                tint = iconTint,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            // 2. Category Filter Chips (Horizontal row: All, IDM, Rock, Pop, etc.)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    categories.forEach { category ->
                        val isSelected = category == selectedCategory
                        val categoryTextColor = if (isSelected) {
                            if (isDarkMode) Color(0xFF10B981) else Color(0xFF00A86B)
                        } else if (isDarkMode) Color(0xFF9CA3AF) else Color(0xFF6B7280)

                        Column(
                            modifier = Modifier
                                .clickable { selectedCategory = category }
                                .padding(vertical = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = category,
                                color = categoryTextColor,
                                fontSize = 16.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            // Underline indicator for selected category
                            Box(
                                modifier = Modifier
                                    .width(26.dp)
                                    .height(2.5.dp)
                                    .background(if (isSelected) (if (isDarkMode) Color(0xFF10B981) else Color(0xFF00A86B)) else Color.Transparent)
                            )
                        }
                    }
                }
            }

            // 3. Playlists Section (Renamed from IDM Essentials)
            item {
                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Playlists",
                        color = textPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    // Add / Create Playlist Button (+)
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(if (isDarkMode) Color(0xFF10B981) else Color(0xFF00A86B))
                            .clickable {
                                newPlaylistName = ""
                                newPlaylistDesc = ""
                                showCreatePlaylistSheet = true
                            }
                            .testTag("create_playlist_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Create Playlist",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Horizontal Carousel of Playlists
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(userPlaylists) { playlist ->
                        PlaylistItemCard(
                            playlist = playlist,
                            onPlay = { onPlayPlaylist(playlist) },
                            isDarkMode = isDarkMode
                        )
                    }
                }
            }

            // 4. Filtered Songs / Recently Listened Section
            item {
                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (selectedCategory == "All") "Recently listened" else "$selectedCategory Tracks",
                        color = textPrimary,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "${filteredTracks.size} tracks",
                        color = textSecondary,
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
            }

            // List of Tracks
            items(filteredTracks) { track ->
                val isCurrent = currentTrack.id == track.id
                TrackRowItem(
                    track = track,
                    isCurrent = isCurrent,
                    isPlaying = isCurrent && isPlaying,
                    onTrackClick = { onTrackSelected(track) },
                    onMenuClick = { selectedMenuTrack = track },
                    isDarkMode = isDarkMode
                )
            }
        }

        // Create Playlist Modal Bottom Sheet
        if (showCreatePlaylistSheet) {
            ModalBottomSheet(
                onDismissRequest = { showCreatePlaylistSheet = false },
                sheetState = playlistSheetState,
                containerColor = if (isDarkMode) Color(0xFF161922) else Color.White,
                contentColor = textPrimary
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 36.dp)
                ) {
                    Text(
                        text = "New Playlist",
                        color = textPrimary,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Give your custom playlist a name and mood description",
                        color = textSecondary,
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    OutlinedTextField(
                        value = newPlaylistName,
                        onValueChange = { newPlaylistName = it },
                        label = { Text("Playlist Name") },
                        placeholder = { Text("e.g. Late Night Synths") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF10B981),
                            unfocusedBorderColor = borderColor,
                            focusedTextColor = textPrimary,
                            unfocusedTextColor = textPrimary,
                            focusedLabelColor = Color(0xFF10B981),
                            unfocusedLabelColor = textSecondary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("playlist_name_input")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = newPlaylistDesc,
                        onValueChange = { newPlaylistDesc = it },
                        label = { Text("Description") },
                        placeholder = { Text("e.g. Ambient textures and chill rhythms") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF10B981),
                            unfocusedBorderColor = borderColor,
                            focusedTextColor = textPrimary,
                            unfocusedTextColor = textPrimary,
                            focusedLabelColor = Color(0xFF10B981),
                            unfocusedLabelColor = textSecondary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("playlist_desc_input")
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            if (newPlaylistName.isNotBlank()) {
                                onCreatePlaylist(newPlaylistName, newPlaylistDesc)
                                showCreatePlaylistSheet = false
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("submit_create_playlist"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF00A86B)
                        ),
                        shape = RoundedCornerShape(25.dp)
                    ) {
                        Text(
                            text = "Create Playlist",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // Account Options Dialog (Settings, Updates, Recents, Support, About)
        activeAccountDialogType?.let { dialogType ->
            AlertDialog(
                onDismissRequest = { activeAccountDialogType = null },
                containerColor = if (isDarkMode) Color(0xFF1A1D24) else Color.White,
                titleContentColor = textPrimary,
                textContentColor = textSecondary,
                title = { Text(dialogType, fontWeight = FontWeight.Bold) },
                text = {
                    when (dialogType) {
                        "Settings" -> Text("Audio quality is set to Lossless 24-bit/96kHz. Equalizer and crossfade effects active.")
                        "Updates" -> Text("Novi is up to date (v0.1.0-beta-build.1). What's new: Interactive Equalizer with curve graphs & presets, Lossless streaming, gapless playback, mono audio, and dark theme.")
                        "Recents" -> Text("Recent activity: Played 'Bone' by Sonic Youth, updated playlist 'Chill IDM', shared lyrics.")
                        "Support" -> Text("Need help? Reach out to support@novimusic.io or visit our Community Discord.")
                        "About" -> Text("Novi is music, unfinished — on purpose. A beta app for discovery and sharing, built with your feedback.")
                        else -> Text("Information unavailable.")
                    }
                },
                confirmButton = {
                    TextButton(onClick = { activeAccountDialogType = null }) {
                        Text("Done", color = Color(0xFF10B981))
                    }
                }
            )
        }

        // Context Menu Dialog for Track Options
        selectedMenuTrack?.let { track ->
            AlertDialog(
                onDismissRequest = { selectedMenuTrack = null },
                containerColor = if (isDarkMode) Color(0xFF1A1D24) else Color.White,
                titleContentColor = textPrimary,
                textContentColor = textPrimary,
                title = { Text(track.title, fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = if (track.isFavorite) "Remove from Favorites" else "Add to Favorites",
                            color = textPrimary,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onToggleFavorite(track.id)
                                    selectedMenuTrack = null
                                }
                                .padding(vertical = 8.dp)
                        )
                        Text(
                            text = "Play Next",
                            color = textPrimary,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onTrackSelected(track)
                                    selectedMenuTrack = null
                                }
                                .padding(vertical = 8.dp)
                        )
                        Text(
                            text = "View Artist: ${track.artist}",
                            color = textPrimary,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedMenuTrack = null }
                                .padding(vertical = 8.dp)
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = { selectedMenuTrack = null }) {
                        Text("Close", color = Color(0xFF10B981))
                    }
                }
            )
        }

        // Notifications Modal Dialog
        if (showNotificationsDialog) {
            AlertDialog(
                onDismissRequest = { showNotificationsDialog = false },
                containerColor = if (isDarkMode) Color(0xFF1A1D24) else Color.White,
                titleContentColor = textPrimary,
                textContentColor = textSecondary,
                title = { Text("Notifications", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("• Sonic Youth: New live remastered sessions available in lossless.")
                        Text("• Aphex Twin: Added 3 new IDM essentials to your weekly mix.")
                        Text("• Woodz: Trending at #1 in Alternative charts.")
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showNotificationsDialog = false }) {
                        Text("Got it", color = Color(0xFF10B981))
                    }
                }
            )
        }
    }
}

/**
 * Custom Playlist Card for the Playlists section.
 */
@Composable
fun PlaylistItemCard(
    playlist: UserPlaylist,
    onPlay: () -> Unit,
    modifier: Modifier = Modifier,
    isDarkMode: Boolean = false
) {
    val cardBg = if (isDarkMode) Color(0xFF1A1D24) else Color.White
    val borderCol = if (isDarkMode) Color(0xFF2D323F) else Color(0xFFE5E7EB)

    Box(
        modifier = modifier
            .size(width = 154.dp, height = 186.dp)
            .shadow(4.dp, RoundedCornerShape(16.dp))
            .border(1.dp, borderCol, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(cardBg)
            .clickable { onPlay() }
            .testTag("playlist_card_${playlist.id}")
    ) {
        // Center Line-Art Illustration with soft gradient background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 54.dp, top = 26.dp, start = 12.dp, end = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            TrackArtworkDisplay(
                artworkType = playlist.artworkType,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Top Left Badge ("PLAYLIST")
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(10.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF111827))
                .padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PlaylistPlay,
                    contentDescription = null,
                    tint = Color(0xFF10B981),
                    modifier = Modifier.size(12.dp)
                )
                Text(
                    text = "PLAYLIST",
                    color = Color.White,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Bottom Dark Banner: Playlist Title, Subtitle & Play Icon
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color(0xFF111827))
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = playlist.name,
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = playlist.description,
                        color = Color(0xFF9CA3AF),
                        fontSize = 10.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(13.dp)
                )
            }
        }
    }
}

/**
 * Individual Track item in Recently Listened.
 */
@Composable
fun TrackRowItem(
    track: Track,
    isCurrent: Boolean,
    isPlaying: Boolean,
    onTrackClick: () -> Unit,
    onMenuClick: () -> Unit,
    modifier: Modifier = Modifier,
    isDarkMode: Boolean = false
) {
    val thumbBorder = if (isDarkMode) Color(0xFF2D323F) else Color(0xFFE5E7EB)
    val thumbBg = if (isDarkMode) Color(0xFF1A1D24) else Color.White
    val titleColor = if (isCurrent) (if (isDarkMode) Color(0xFF10B981) else Color(0xFF00A86B)) else if (isDarkMode) Color.White else Color(0xFF111827)
    val artistColor = if (isDarkMode) Color(0xFF9CA3AF) else Color(0xFF6B7280)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onTrackClick() }
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.weight(1f)
        ) {
            // Artwork thumbnail
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .border(1.dp, thumbBorder, RoundedCornerShape(10.dp))
                    .background(thumbBg)
            ) {
                TrackArtworkDisplay(
                    artworkType = track.artworkType,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Track Details
            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(
                    text = track.title,
                    color = titleColor,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    // Green Verified Circle Checkmark Badge
                    if (track.isVerified) {
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF00A86B)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Verified",
                                tint = Color.White,
                                modifier = Modifier.size(10.dp)
                            )
                        }
                    }

                    Text(
                        text = track.artist,
                        color = artistColor,
                        fontSize = 13.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        // More options button (⋮)
        IconButton(
            onClick = onMenuClick,
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "Options",
                tint = Color(0xFF9CA3AF),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
