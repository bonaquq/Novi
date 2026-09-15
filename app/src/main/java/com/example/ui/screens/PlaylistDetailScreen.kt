package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.model.ArtworkType
import com.example.model.Track
import com.example.model.UserPlaylist
import com.example.ui.components.TrackArtworkDisplay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistDetailScreen(
    playlist: UserPlaylist,
    tracks: List<Track>,
    currentTrack: Track,
    isPlaying: Boolean,
    onTrackSelected: (Track) -> Unit,
    onPlayAll: () -> Unit,
    onShufflePlay: () -> Unit,
    onToggleFavorite: (String) -> Unit,
    onUpdatePlaylist: (UserPlaylist) -> Unit,
    onBack: () -> Unit,
    isDarkMode: Boolean,
    modifier: Modifier = Modifier
) {
    var showAddSongsSheet by remember { mutableStateOf(false) }
    var showEditSheet by remember { mutableStateOf(false) }
    val editSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val addSongsSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Edit states
    var editName by remember(playlist.name) { mutableStateOf(playlist.name) }
    var editDesc by remember(playlist.description) { mutableStateOf(playlist.description) }
    var editImageUri by remember(playlist.customImageUri) { mutableStateOf(playlist.customImageUri) }
    var editArtworkType by remember(playlist.artworkType) { mutableStateOf(playlist.artworkType) }

    // Photo picker for custom playlist image cover
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri: Uri? ->
            if (uri != null) {
                editImageUri = uri.toString()
                onUpdatePlaylist(
                    playlist.copy(
                        customImageUri = uri.toString()
                    )
                )
            }
        }
    )

    val playlistTracks = remember(playlist.trackIds, tracks) {
        if (playlist.trackIds.isNotEmpty()) {
            val trackMap = tracks.associateBy { it.id }
            playlist.trackIds.mapNotNull { trackMap[it] }
        } else {
            // Default sample tracks for this playlist if empty
            tracks.take(4)
        }
    }

    val totalDurationSeconds = remember(playlistTracks) {
        playlistTracks.sumOf { it.durationSeconds }
    }
    val totalMinutes = totalDurationSeconds / 60
    val totalSeconds = totalDurationSeconds % 60
    val durationText = if (totalMinutes > 0) "${totalMinutes}m ${totalSeconds}s" else "${totalSeconds}s"

    val baseScreenBg = if (isDarkMode) Color(0xFF0D0F14) else Color(0xFFF9FAFB)
    val cardBg = if (isDarkMode) Color(0xFF161922) else Color.White
    val borderColor = if (isDarkMode) Color(0xFF2D323F) else Color(0xFFE5E7EB)
    val primaryText = if (isDarkMode) Color.White else Color(0xFF111827)
    val secondaryText = if (isDarkMode) Color(0xFF9CA3AF) else Color(0xFF6B7280)
    val accentEmerald = if (isDarkMode) Color(0xFF10B981) else Color(0xFF00A86B)

    val startColor = Color(playlist.coverGradientStart)
    val endColor = Color(playlist.coverGradientEnd)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(baseScreenBg)
    ) {
        // --- 1. Immersive Blurred Playlist Background with Top-To-Down Gradient ---
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            if (!playlist.customImageUri.isNullOrBlank()) {
                // Blurred custom cover photo
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(playlist.customImageUri)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .scale(1.2f)
                        .blur(32.dp)
                )
            } else {
                // Dynamic atmospheric vector/gradient canvas blurred
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .scale(1.2f)
                        .blur(36.dp)
                ) {
                    TrackArtworkDisplay(
                        artworkType = playlist.artworkType,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            // Top-to-Down Gradient Overlay for readability
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = if (isDarkMode) {
                                listOf(
                                    startColor.copy(alpha = 0.45f),
                                    baseScreenBg.copy(alpha = 0.82f),
                                    baseScreenBg.copy(alpha = 0.96f),
                                    baseScreenBg
                                )
                            } else {
                                listOf(
                                    startColor.copy(alpha = 0.35f),
                                    baseScreenBg.copy(alpha = 0.85f),
                                    baseScreenBg.copy(alpha = 0.97f),
                                    baseScreenBg
                                )
                            }
                        )
                    )
            )
        }

        // --- 2. Foreground Playlist Content ---
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
            contentPadding = PaddingValues(bottom = 140.dp)
        ) {
            // Top Navigation Bar
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background((if (isDarkMode) Color.Black else Color.White).copy(alpha = 0.4f))
                            .testTag("playlist_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = primaryText
                        )
                    }

                    IconButton(
                        onClick = {
                            editName = playlist.name
                            editDesc = playlist.description
                            editImageUri = playlist.customImageUri
                            editArtworkType = playlist.artworkType
                            showEditSheet = true
                        },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background((if (isDarkMode) Color.Black else Color.White).copy(alpha = 0.4f))
                            .testTag("playlist_edit_details_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Playlist",
                            tint = primaryText
                        )
                    }
                }
            }

            // Playlist Hero Banner Header
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Large Cover Art Card with clickable change affordance
                    Box(
                        modifier = Modifier
                            .size(200.dp)
                            .shadow(16.dp, RoundedCornerShape(20.dp))
                            .clip(RoundedCornerShape(20.dp))
                            .border(1.5.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(20.dp))
                            .background(cardBg)
                            .clickable {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            }
                            .testTag("playlist_hero_cover"),
                        contentAlignment = Alignment.Center
                    ) {
                        TrackArtworkDisplay(
                            artworkType = playlist.artworkType,
                            customImageUri = playlist.customImageUri,
                            modifier = Modifier.fillMaxSize()
                        )

                        // Subtle camera icon overlay badge at bottom-right
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(8.dp)
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.65f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddPhotoAlternate,
                                contentDescription = "Change Cover",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Playlist Title
                    Text(
                        text = playlist.name,
                        color = primaryText,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.testTag("playlist_detail_title")
                    )

                    if (playlist.description.isNotBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = playlist.description,
                            color = secondaryText,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Metadata info
                    Text(
                        text = "${playlistTracks.size} tracks • $durationText",
                        color = accentEmerald,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Action Buttons (Play All & Shuffle)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = onPlayAll,
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .testTag("playlist_play_all_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = accentEmerald
                            ),
                            shape = RoundedCornerShape(25.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Play",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        OutlinedButton(
                            onClick = onShufflePlay,
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .testTag("playlist_shuffle_button"),
                            shape = RoundedCornerShape(25.dp),
                            border = BorderStroke(1.5.dp, borderColor),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = primaryText
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shuffle,
                                contentDescription = null,
                                tint = primaryText
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Shuffle",
                                color = primaryText,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        // Add Song Button
                        IconButton(
                            onClick = { showAddSongsSheet = true },
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .border(1.dp, borderColor, CircleShape)
                                .background(cardBg)
                                .testTag("playlist_add_songs_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Songs",
                                tint = accentEmerald
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
            }

            // Playlist Tracks Section
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Tracks",
                        color = primaryText,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${playlistTracks.size} songs",
                        color = secondaryText,
                        fontSize = 13.sp
                    )
                }
            }

            if (playlistTracks.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = null,
                            tint = secondaryText,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No songs in this playlist yet",
                            color = primaryText,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Tap + to add songs from your library",
                            color = secondaryText,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { showAddSongsSheet = true },
                            colors = ButtonDefaults.buttonColors(containerColor = accentEmerald)
                        ) {
                            Text("Add Songs", color = Color.White)
                        }
                    }
                }
            } else {
                itemsIndexed(playlistTracks) { index, track ->
                    val isCurrent = currentTrack.id == track.id
                    val titleColor = if (isCurrent) accentEmerald else primaryText

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onTrackSelected(track) }
                            .padding(horizontal = 20.dp, vertical = 8.dp)
                            .testTag("playlist_track_row_${track.id}"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            // Track Number or Active indicator
                            Text(
                                text = (index + 1).toString(),
                                color = if (isCurrent) accentEmerald else secondaryText,
                                fontSize = 14.sp,
                                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                                modifier = Modifier.width(20.dp)
                            )

                            // Artwork thumbnail
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .border(1.dp, borderColor, RoundedCornerShape(8.dp))
                                    .background(cardBg)
                            ) {
                                TrackArtworkDisplay(
                                    artworkType = track.artworkType,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            // Title & Artist
                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
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
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    if (track.isVerified) {
                                        Box(
                                            modifier = Modifier
                                                .size(12.dp)
                                                .clip(CircleShape)
                                                .background(accentEmerald),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Verified",
                                                tint = Color.White,
                                                modifier = Modifier.size(8.dp)
                                            )
                                        }
                                    }

                                    Text(
                                        text = "${track.artist} • ${track.formattedDuration}",
                                        color = secondaryText,
                                        fontSize = 12.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }

                        // Right actions (Favorite & Remove)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { onToggleFavorite(track.id) },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = if (track.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = "Favorite",
                                    tint = if (track.isFavorite) Color(0xFFEF4444) else secondaryText,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            IconButton(
                                onClick = {
                                    val updatedIds = playlist.trackIds.filter { it != track.id }
                                    onUpdatePlaylist(
                                        playlist.copy(
                                            trackIds = updatedIds,
                                            trackCount = updatedIds.size
                                        )
                                    )
                                },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Remove from Playlist",
                                    tint = secondaryText,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- 3. Edit Playlist Bottom Sheet ---
        if (showEditSheet) {
            ModalBottomSheet(
                onDismissRequest = { showEditSheet = false },
                sheetState = editSheetState,
                containerColor = if (isDarkMode) Color(0xFF161922) else Color.White,
                contentColor = primaryText
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 12.dp)
                        .padding(bottom = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Edit Playlist",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryText
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Cover Art preview with custom image picker
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .border(1.5.dp, borderColor, RoundedCornerShape(16.dp))
                            .clickable {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        TrackArtworkDisplay(
                            artworkType = editArtworkType,
                            customImageUri = editImageUri,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = accentEmerald),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Choose Custom Cover", fontSize = 13.sp)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Playlist Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = accentEmerald,
                            focusedLabelColor = accentEmerald
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = editDesc,
                        onValueChange = { editDesc = it },
                        label = { Text("Description") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = accentEmerald,
                            focusedLabelColor = accentEmerald
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            onUpdatePlaylist(
                                playlist.copy(
                                    name = editName.ifBlank { playlist.name },
                                    description = editDesc,
                                    customImageUri = editImageUri,
                                    artworkType = editArtworkType
                                )
                            )
                            showEditSheet = false
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = accentEmerald),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Text("Save Changes", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // --- 4. Add Songs to Playlist Bottom Sheet ---
        if (showAddSongsSheet) {
            ModalBottomSheet(
                onDismissRequest = { showAddSongsSheet = false },
                sheetState = addSongsSheetState,
                containerColor = if (isDarkMode) Color(0xFF161922) else Color.White,
                contentColor = primaryText
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                        .padding(bottom = 36.dp)
                ) {
                    Text(
                        text = "Add Songs to ${playlist.name}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryText
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    LazyColumn(
                        modifier = Modifier.height(360.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        itemsIndexed(tracks) { _, track ->
                            val isAlreadyInPlaylist = playlist.trackIds.contains(track.id)

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isAlreadyInPlaylist) accentEmerald.copy(alpha = 0.1f) else cardBg)
                                    .clickable {
                                        val newIds = if (isAlreadyInPlaylist) {
                                            playlist.trackIds.filter { it != track.id }
                                        } else {
                                            playlist.trackIds + track.id
                                        }
                                        onUpdatePlaylist(
                                            playlist.copy(
                                                trackIds = newIds,
                                                trackCount = newIds.size
                                            )
                                        )
                                    }
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                    ) {
                                        TrackArtworkDisplay(
                                            artworkType = track.artworkType,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }

                                    Column {
                                        Text(
                                            text = track.title,
                                            color = primaryText,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = "${track.artist} • ${track.formattedDuration}",
                                            color = secondaryText,
                                            fontSize = 12.sp
                                        )
                                    }
                                }

                                if (isAlreadyInPlaylist) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Added",
                                        tint = accentEmerald,
                                        modifier = Modifier.size(20.dp)
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Add",
                                        tint = secondaryText,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { showAddSongsSheet = false },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = accentEmerald),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Text("Done", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
