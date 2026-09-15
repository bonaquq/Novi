package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Track

@Composable
fun FavoritesScreen(
    tracks: List<Track>,
    currentTrack: Track,
    isPlaying: Boolean,
    onTrackSelected: (Track) -> Unit,
    modifier: Modifier = Modifier,
    isDarkMode: Boolean = false
) {
    val favoriteTracks = tracks.filter { it.isFavorite }
    val bgColor = if (isDarkMode) Color(0xFF0D0F14) else Color(0xFFF9FAFB)
    val textPrimary = if (isDarkMode) Color.White else Color(0xFF111827)
    val textSecondary = if (isDarkMode) Color(0xFF9CA3AF) else Color(0xFF6B7280)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(bgColor)
            .statusBarsPadding()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 120.dp, top = 16.dp)
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "Favorites",
                            color = textPrimary,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "${favoriteTracks.size} loved tracks",
                        color = textSecondary,
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (favoriteTracks.isNotEmpty()) {
                        Button(
                            onClick = { onTrackSelected(favoriteTracks.first()) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00A86B)),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.height(44.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.size(6.dp))
                            Text("Play All", color = Color.White, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            if (favoriteTracks.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No favorite tracks yet.\nTap the heart icon on any track to save it here!",
                            color = Color(0xFF9CA3AF),
                            fontSize = 15.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                items(favoriteTracks) { track ->
                    val isCurrent = currentTrack.id == track.id
                    TrackRowItem(
                        track = track,
                        isCurrent = isCurrent,
                        isPlaying = isCurrent && isPlaying,
                        onTrackClick = { onTrackSelected(track) },
                        onMenuClick = {},
                        isDarkMode = isDarkMode
                    )
                }
            }
        }
    }
}
