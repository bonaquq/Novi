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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.EssentialCard
import com.example.model.SampleMusicData
import com.example.model.Track
import com.example.ui.components.TrackArtworkDisplay
import com.example.ui.components.UserAvatarView

@Composable
fun HomeScreen(
    tracks: List<Track>,
    currentTrack: Track,
    isPlaying: Boolean,
    onTrackSelected: (Track) -> Unit,
    onOpenSearch: () -> Unit,
    onToggleFavorite: (String) -> Unit,
    onOpenWelcome: () -> Unit,
    modifier: Modifier = Modifier,
    isDarkMode: Boolean = false
) {
    var selectedCategory by remember { mutableStateOf("IDM") }
    val categories = SampleMusicData.categories
    val essentialCards = SampleMusicData.essentialCards

    val screenBg = if (isDarkMode) Color(0xFF0D0F14) else Color(0xFFF9FAFB)
    val buttonBg = if (isDarkMode) Color(0xFF1A1D24) else Color.White
    val borderColor = if (isDarkMode) Color(0xFF2D323F) else Color(0xFFE5E7EB)
    val iconTint = if (isDarkMode) Color.White else Color(0xFF111827)
    val textPrimary = if (isDarkMode) Color.White else Color(0xFF111827)

    var showNotificationsDialog by remember { mutableStateOf(false) }
    var selectedMenuTrack by remember { mutableStateOf<Track?>(null) }

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
            // 1. Top Header Bar: User Avatar & Right Outlined Action Buttons (Screenshot 2)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left: User Avatar (Blonde hair girl, Screenshot 2)
                    UserAvatarView(
                        modifier = Modifier
                            .size(42.dp)
                            .testTag("user_avatar")
                            .clickable { onOpenWelcome() }
                    )

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

            // 2. Category Filter Chips (Horizontal row: All, IDM, Rock, Pop, etc., Screenshot 2)
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
                        val categoryTextColor = if (isSelected) textPrimary else if (isDarkMode) Color(0xFF6B7280) else Color(0xFF9CA3AF)
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

                            // Underline indicator for selected category (Screenshot 2: IDM selected)
                            Box(
                                modifier = Modifier
                                    .width(26.dp)
                                    .height(2.5.dp)
                                    .background(if (isSelected) textPrimary else Color.Transparent)
                            )
                        }
                    }
                }
            }

            // 3. IDM Essentials Section (Title with emerald circular refresh button + horizontal cards, Screenshot 2)
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
                        text = "$selectedCategory Essentials",
                        color = textPrimary,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold
                    )

                    // Emerald Green circular refresh button (Screenshot 2)
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF00A86B))
                            .clickable {
                                // Refresh / shuffle essentials
                                val nextCategory = categories[(categories.indexOf(selectedCategory) + 1) % categories.size]
                                selectedCategory = nextCategory
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Horizontal Carousel of Cards (Aphex Twin, Nelly Mes, etc.)
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(essentialCards) { card ->
                        EssentialCardItem(
                            card = card,
                            onCardClick = {
                                val targetTrack = tracks.find { it.id == card.trackId } ?: tracks.first()
                                onTrackSelected(targetTrack)
                            }
                        )
                    }
                }
            }

            // 4. Recently Listened Section (Screenshot 2)
            item {
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Recently listened",
                    color = textPrimary,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))
            }

            // List of Tracks (Bone by Sonic Youth, Review by Depeche Mode, Drowning by Woodz...)
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

        // Context Menu Dialog for Track Options
        selectedMenuTrack?.let { track ->
            AlertDialog(
                onDismissRequest = { selectedMenuTrack = null },
                title = { Text(track.title, fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = if (track.isFavorite) "Remove from Favorites" else "Add to Favorites",
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
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedMenuTrack = null }
                                .padding(vertical = 8.dp)
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = { selectedMenuTrack = null }) {
                        Text("Close")
                    }
                }
            )
        }

        // Notifications Modal Dialog
        if (showNotificationsDialog) {
            AlertDialog(
                onDismissRequest = { showNotificationsDialog = false },
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
                        Text("Got it")
                    }
                }
            )
        }
    }
}

/**
 * Essential Card (Aphex Twin / Nelly Mes / Boards of Canada) as shown in Screenshot 2.
 */
@Composable
fun EssentialCardItem(
    card: EssentialCard,
    onCardClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(width = 150.dp, height = 180.dp)
            .shadow(4.dp, RoundedCornerShape(16.dp))
            .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .clickable { onCardClick() }
    ) {
        // Center Line-Art Illustration
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 54.dp, top = 28.dp, start = 12.dp, end = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            TrackArtworkDisplay(
                artworkType = card.artworkType,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Top Left Badge ("• LIVE" or "NEW") (Screenshot 2)
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
                if (card.isLive) {
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEF4444))
                    )
                }
                Text(
                    text = if (card.isLive) "LIVE" else card.badgeText,
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Bottom Dark Banner: Artist Title, Subtitle & Diagonal Arrow (Screenshot 2)
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
                        text = card.title,
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = card.subtitle,
                        color = Color(0xFF9CA3AF),
                        fontSize = 10.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Diagonal Arrow Icon (Screenshot 2)
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
 * Individual Track item in Recently Listened (Screenshot 2).
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
    val titleColor = if (isCurrent) Color(0xFF00A86B) else if (isDarkMode) Color.White else Color(0xFF111827)
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
            // Artwork thumbnail (Screenshot 2)
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
                    // Green Verified Circle Checkmark Badge (Screenshot 2)
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

        // More options button (⋮) (Screenshot 2)
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
