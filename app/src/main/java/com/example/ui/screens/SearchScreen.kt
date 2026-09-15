package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Track

@Composable
fun SearchScreen(
    query: String,
    onQueryChange: (String) -> Unit,
    tracks: List<Track>,
    currentTrack: Track,
    isPlaying: Boolean,
    onTrackSelected: (Track) -> Unit,
    onBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    isDarkMode: Boolean = false
) {
    val filteredTracks = if (query.isBlank()) {
        tracks
    } else {
        tracks.filter {
            it.title.contains(query, ignoreCase = true) ||
            it.artist.contains(query, ignoreCase = true) ||
            it.album.contains(query, ignoreCase = true) ||
            it.genre.contains(query, ignoreCase = true)
        }
    }

    val bgColor = if (isDarkMode) Color(0xFF0D0F14) else Color(0xFFF9FAFB)
    val textPrimary = if (isDarkMode) Color.White else Color(0xFF111827)
    val inputBg = if (isDarkMode) Color(0xFF1A1D24) else Color.White
    val borderColor = if (isDarkMode) Color(0xFF2D323F) else Color(0xFFE5E7EB)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(bgColor)
            .statusBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Search Input Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (onBack != null) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier
                                .size(40.dp)
                                .testTag("search_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = textPrimary
                            )
                        }
                    }

                    Text(
                        text = "Find Music",
                        color = textPrimary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_text_field"),
                    placeholder = {
                        Text("Search songs, artists, or genres...", color = Color(0xFF9CA3AF))
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = if (isDarkMode) Color(0xFF9CA3AF) else Color(0xFF6B7280)
                        )
                    },
                    trailingIcon = {
                        if (query.isNotEmpty()) {
                            IconButton(onClick = { onQueryChange("") }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear",
                                    tint = if (isDarkMode) Color(0xFF9CA3AF) else Color(0xFF6B7280)
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = inputBg,
                        unfocusedContainerColor = inputBg,
                        focusedBorderColor = Color(0xFF00A86B),
                        unfocusedBorderColor = borderColor,
                        focusedTextColor = textPrimary,
                        unfocusedTextColor = textPrimary
                    )
                )
            }

            // Results List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 120.dp)
            ) {
                if (filteredTracks.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No tracks found for \"$query\"",
                                color = Color(0xFF9CA3AF),
                                fontSize = 15.sp
                            )
                        }
                    }
                } else {
                    items(filteredTracks) { track ->
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
}
