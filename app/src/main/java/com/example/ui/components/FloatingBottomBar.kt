package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.model.Track

/**
 * Bottom Navigation Bar & Floating Mini Player (matching Screenshot 2).
 * 3 Tabs: Home, Favorites, Profile.
 */
@Composable
fun ModernBottomBar(
    currentTrack: Track?,
    isPlaying: Boolean,
    selectedTab: Int, // 0: Home, 1: Favorites, 2: Profile
    onTabSelected: (Int) -> Unit,
    onPlayPauseToggle: () -> Unit,
    onExpandNowPlaying: () -> Unit,
    modifier: Modifier = Modifier,
    isDarkMode: Boolean = false
) {
    val barBg = if (isDarkMode) Color(0xFF1A1D24) else Color.White
    val borderColor = if (isDarkMode) Color(0xFF2D323F) else Color(0xFFE5E7EB)
    val activeColor = if (isDarkMode) Color.White else Color(0xFF111827)
    val inactiveColor = if (isDarkMode) Color(0xFF6B7280) else Color(0xFF9CA3AF)
    val titleColor = if (isDarkMode) Color.White else Color(0xFF111827)
    val artistColor = if (isDarkMode) Color(0xFF9CA3AF) else Color(0xFF6B7280)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Floating Mini Player Capsule (floats right above navigation bar)
        if (currentTrack != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .shadow(8.dp, RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, borderColor, RoundedCornerShape(16.dp))
                    .background(barBg)
                    .clickable { onExpandNowPlaying() }
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .testTag("mini_player")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        // Mini Album Artwork
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isDarkMode) Color(0xFF252A34) else Color(0xFFF3F4F6))
                        ) {
                            TrackArtworkDisplay(
                                artworkType = currentTrack.artworkType,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        // Title & Verified Artist
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = currentTrack.title,
                                color = titleColor,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                if (currentTrack.isVerified) {
                                    Box(
                                        modifier = Modifier
                                            .size(12.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF00A86B)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(8.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = currentTrack.artist,
                                    color = artistColor,
                                    fontSize = 12.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

                    // Mini Play/Pause button (Emerald Green, Screenshot 2/3 style)
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF00A86B))
                            .clickable { onPlayPauseToggle() }
                            .testTag("mini_player_play_pause"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // Bottom Navigation Bar: Home, Favorites, Profile (Screenshot 2)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(6.dp, RoundedCornerShape(24.dp))
                .clip(RoundedCornerShape(24.dp))
                .border(1.dp, borderColor, RoundedCornerShape(24.dp))
                .background(barBg)
                .padding(vertical = 10.dp, horizontal = 24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Tab 0: Home
                val isHome = selectedTab == 0
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable { onTabSelected(0) }
                        .padding(horizontal = 12.dp, vertical = 2.dp)
                        .testTag("tab_home")
                ) {
                    Icon(
                        imageVector = if (isHome) Icons.Filled.Home else Icons.Outlined.Home,
                        contentDescription = "Home",
                        tint = if (isHome) activeColor else inactiveColor,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Home",
                        color = if (isHome) activeColor else inactiveColor,
                        fontSize = 11.sp,
                        fontWeight = if (isHome) FontWeight.Bold else FontWeight.Normal
                    )
                }

                // Tab 1: Favorites
                val isFav = selectedTab == 1
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable { onTabSelected(1) }
                        .padding(horizontal = 12.dp, vertical = 2.dp)
                        .testTag("tab_favorites")
                ) {
                    Icon(
                        imageVector = if (isFav) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Favorites",
                        tint = if (isFav) activeColor else inactiveColor,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Favorites",
                        color = if (isFav) activeColor else inactiveColor,
                        fontSize = 11.sp,
                        fontWeight = if (isFav) FontWeight.Bold else FontWeight.Normal
                    )
                }

                // Tab 2: Profile
                val isProfile = selectedTab == 2
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable { onTabSelected(2) }
                        .padding(horizontal = 12.dp, vertical = 2.dp)
                        .testTag("tab_profile")
                ) {
                    Icon(
                        imageVector = if (isProfile) Icons.Filled.Person else Icons.Outlined.Person,
                        contentDescription = "Profile",
                        tint = if (isProfile) activeColor else inactiveColor,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Profile",
                        color = if (isProfile) activeColor else inactiveColor,
                        fontSize = 11.sp,
                        fontWeight = if (isProfile) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}
