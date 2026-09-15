package com.example.ui.screens

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import com.example.model.RepeatMode
import com.example.model.Track
import com.example.ui.components.TrackArtworkDisplay

@Composable
fun NowPlayingScreen(
    track: Track,
    isPlaying: Boolean,
    currentPositionSeconds: Int,
    isShuffle: Boolean,
    isRepeat: Boolean,
    repeatMode: RepeatMode = if (isRepeat) RepeatMode.ALL else RepeatMode.OFF,
    isDarkMode: Boolean = false,
    onToggleDarkMode: (() -> Unit)? = null,
    onCollapse: () -> Unit,
    onPlayPauseToggle: () -> Unit,
    onNextTrack: () -> Unit,
    onPreviousTrack: () -> Unit,
    onSeek: (Float) -> Unit,
    onToggleShuffle: () -> Unit,
    onToggleRepeat: () -> Unit,
    onDeleteTrack: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val listState = rememberLazyListState()

    var isSeeking by remember { mutableStateOf(false) }
    var seekPosition by remember { mutableFloatStateOf(0f) }

    val bgColor = if (isDarkMode) Color(0xFF0F1117) else Color(0xFFF9FAFB)
    val textPrimary = if (isDarkMode) Color.White else Color(0xFF111827)
    val textSecondary = if (isDarkMode) Color(0xFF9CA3AF) else Color(0xFF6B7280)
    val cardBorder = if (isDarkMode) Color(0xFF232836) else Color.White
    val cardBg = if (isDarkMode) Color(0xFF161922) else Color.White
    val buttonBorder = if (isDarkMode) Color(0xFF2D323F) else Color(0xFFE5E7EB)
    val buttonBg = if (isDarkMode) Color(0xFF161922) else Color.Transparent
    val capsuleBg = if (isDarkMode) Color(0xFF1A1E26) else Color(0xFF121418)

    val currentFraction = if (track.durationSeconds > 0) {
        (currentPositionSeconds.toFloat() / track.durationSeconds.toFloat()).coerceIn(0f, 1f)
    } else 0f

    // Find active lyric index
    val activeLyricIndex by remember(currentPositionSeconds, track.lyrics) {
        derivedStateOf {
            if (track.lyrics.isEmpty()) -1
            else {
                val index = track.lyrics.indexOfLast { it.timeSeconds <= currentPositionSeconds }
                if (index == -1) 0 else index
            }
        }
    }

    // Auto-scroll lyrics to keep active line in view
    LaunchedEffect(activeLyricIndex) {
        if (activeLyricIndex >= 0 && activeLyricIndex < track.lyrics.size) {
            listState.animateScrollToItem((activeLyricIndex - 1).coerceAtLeast(0))
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(bgColor)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Top Bar: Back Arrow (<), "Lyrics", Dark/Light toggle, Share (↗)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onCollapse,
                    modifier = Modifier
                        .size(40.dp)
                        .testTag("now_playing_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = textPrimary
                    )
                }

                Text(
                    text = "Lyrics",
                    color = textPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (onToggleDarkMode != null) {
                        IconButton(
                            onClick = onToggleDarkMode,
                            modifier = Modifier
                                .size(38.dp)
                                .background(buttonBg, RoundedCornerShape(10.dp))
                                .border(1.dp, buttonBorder, RoundedCornerShape(10.dp))
                                .clip(RoundedCornerShape(10.dp))
                                .testTag("now_playing_dark_mode_toggle")
                        ) {
                            Icon(
                                imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                                contentDescription = "Toggle Dark Mode",
                                tint = if (isDarkMode) Color(0xFFFBBF24) else Color(0xFF4B5563),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    IconButton(
                        onClick = {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, "Listening to ${track.title}")
                                putExtra(Intent.EXTRA_TEXT, "Listening to ${track.title} by ${track.artist} on Novi")
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share Track"))
                        },
                        modifier = Modifier
                            .size(38.dp)
                            .background(buttonBg, RoundedCornerShape(10.dp))
                            .border(1.dp, buttonBorder, RoundedCornerShape(10.dp))
                            .clip(RoundedCornerShape(10.dp))
                            .testTag("now_playing_share_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                            contentDescription = "Share",
                            tint = textPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 2. Framed Square Album Art Card
            Box(
                modifier = Modifier
                    .size(210.dp)
                    .shadow(12.dp, RoundedCornerShape(18.dp))
                    .border(3.dp, cardBorder, RoundedCornerShape(18.dp))
                    .clip(RoundedCornerShape(18.dp))
                    .background(cardBg)
            ) {
                TrackArtworkDisplay(
                    artworkType = track.artworkType,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 3. Artist Title & Badge Pill
            Text(
                text = track.artist,
                color = textPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Capsule badge pill (e.g. "BONE")
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (isDarkMode) Color(0xFF232836) else Color(0xFF111827))
                    .padding(horizontal = 14.dp, vertical = 4.dp)
            ) {
                Text(
                    text = track.badgeLabel ?: track.title.uppercase(),
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 4. Synchronized Karaoke Lyrics List (Scrollable & Click-to-Seek)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                if (track.lyrics.isNotEmpty()) {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        itemsIndexed(track.lyrics) { index, lyric ->
                            val isActive = index == activeLyricIndex
                            val targetColor = if (isActive) {
                                if (isDarkMode) Color.White else Color(0xFF111827)
                            } else {
                                if (isDarkMode) Color(0xFF6B7280) else Color(0xFF9CA3AF)
                            }
                            val lyricColor by animateColorAsState(
                                targetValue = targetColor,
                                animationSpec = tween(200),
                                label = "lyric_color"
                            )

                            Text(
                                text = lyric.text,
                                color = lyricColor,
                                fontSize = if (isActive) 17.sp else 15.sp,
                                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        if (track.durationSeconds > 0) {
                                            val fraction = (lyric.timeSeconds.toFloat() / track.durationSeconds.toFloat())
                                            onSeek(fraction)
                                        }
                                    }
                                    .padding(vertical = 4.dp, horizontal = 12.dp)
                            )
                        }
                        item {
                            Spacer(modifier = Modifier.height(60.dp))
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Instrumental Track",
                            color = textSecondary,
                            fontSize = 15.sp
                        )
                    }
                }
            }

            // Subtle scrubber progress slider
            Slider(
                value = if (isSeeking) seekPosition else currentFraction,
                onValueChange = {
                    isSeeking = true
                    seekPosition = it
                },
                onValueChangeFinished = {
                    onSeek(seekPosition)
                    isSeeking = false
                },
                colors = SliderDefaults.colors(
                    thumbColor = Color(0xFF10B981),
                    activeTrackColor = Color(0xFF10B981),
                    inactiveTrackColor = if (isDarkMode) Color(0xFF2D323F) else Color(0xFFE5E7EB)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 5. Bottom Dark Playback Capsule
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .shadow(12.dp, RoundedCornerShape(32.dp))
                    .clip(RoundedCornerShape(32.dp))
                    .background(capsuleBg)
                    .border(
                        1.dp,
                        if (isDarkMode) Color(0xFF2D323F) else Color.Transparent,
                        RoundedCornerShape(32.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Repeat Toggle Button
                    // Pressed once: Repeat queue once (RepeatMode.ALL)
                    // Pressed once more: Repeat current song playing (RepeatMode.ONE)
                    // Pressed again: Off (RepeatMode.OFF)
                    IconButton(
                        onClick = onToggleRepeat,
                        modifier = Modifier
                            .size(42.dp)
                            .testTag("now_playing_repeat_button")
                    ) {
                        Icon(
                            imageVector = when (repeatMode) {
                                RepeatMode.ONE -> Icons.Default.RepeatOne
                                else -> Icons.Default.Repeat
                            },
                            contentDescription = when (repeatMode) {
                                RepeatMode.ONE -> "Repeat Current Song"
                                RepeatMode.ALL -> "Repeat Queue"
                                RepeatMode.OFF -> "Repeat Off"
                            },
                            tint = when (repeatMode) {
                                RepeatMode.ONE, RepeatMode.ALL -> Color(0xFF10B981)
                                RepeatMode.OFF -> Color(0xFF9CA3AF)
                            },
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Previous Track Button
                    IconButton(
                        onClick = onPreviousTrack,
                        modifier = Modifier
                            .size(44.dp)
                            .testTag("now_playing_prev_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.SkipPrevious,
                            contentDescription = "Previous",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // Emerald Green Play/Pause Button Capsule
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF00A86B))
                            .clickable { onPlayPauseToggle() }
                            .testTag("now_playing_play_pause_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    // Next Track Button
                    IconButton(
                        onClick = onNextTrack,
                        modifier = Modifier
                            .size(44.dp)
                            .testTag("now_playing_next_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.SkipNext,
                            contentDescription = "Next",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // Shuffle Toggle Button
                    IconButton(
                        onClick = onToggleShuffle,
                        modifier = Modifier
                            .size(42.dp)
                            .testTag("now_playing_shuffle_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shuffle,
                            contentDescription = "Shuffle",
                            tint = if (isShuffle) Color(0xFF10B981) else Color(0xFF9CA3AF),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}
