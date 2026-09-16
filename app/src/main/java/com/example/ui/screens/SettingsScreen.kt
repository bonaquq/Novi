package com.example.ui.screens

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AudioAppSettings
import com.example.ui.components.DevPasscodeDialog
import com.example.ui.components.EqualizerView

@Composable
fun SettingsScreen(
    settings: AudioAppSettings,
    onSettingsChange: (AudioAppSettings) -> Unit,
    isDarkMode: Boolean,
    onToggleDarkMode: (Boolean) -> Unit,
    onBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var showQualityDialog by remember { mutableStateOf(false) }
    var showDevPasscodeDialog by remember { mutableStateOf(false) }

    val isDevMode = settings.developerMode
    val bgColor = if (isDevMode) Color.Transparent else if (isDarkMode) Color(0xFF0D0F14) else Color(0xFFF9FAFB)
    val cardBg = if (isDevMode) Color(0xCC161922) else if (isDarkMode) Color(0xFF1A1D24) else Color.White
    val borderColor = if (isDevMode) Color(0x33FFFFFF) else if (isDarkMode) Color(0xFF2D323F) else Color(0xFFE5E7EB)
    val dividerColor = if (isDevMode) Color(0x22FFFFFF) else if (isDarkMode) Color(0xFF2D323F) else Color(0xFFF3F4F6)
    val primaryTextColor = if (isDarkMode || isDevMode) Color.White else Color(0xFF111827)
    val secondaryTextColor = if (isDarkMode || isDevMode) Color(0xFF9CA3AF) else Color(0xFF6B7280)
    val accentEmerald = if (isDarkMode || isDevMode) Color(0xFF10B981) else Color(0xFF00A86B)

    val audioQualityOptions = listOf(
        "Normal (160 kbps)" to "Efficient data usage, AAC standard",
        "High (320 kbps)" to "Rich stereo depth, CD-grade clarity",
        "Hi-Res Lossless (24-bit / 96kHz)" to "Audiophile studio sound resolution",
        "Studio Master (24-bit / 192kHz)" to "Uncompressed bit-perfect master direct"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(bgColor)
            .statusBarsPadding()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 20.dp,
                end = 20.dp,
                top = 16.dp,
                bottom = 120.dp
            )
        ) {
            // Header Top Bar
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (onBack != null) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                                .background(cardBg)
                                .testTag("settings_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = primaryTextColor
                            )
                        }
                    }

                    Column {
                        Text(
                            text = "Settings & Audio",
                            color = primaryTextColor,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // 1. Interactive Equalizer Section
            item {
                Text(
                    text = "EQUALIZER & SOUND FX",
                    color = secondaryTextColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                EqualizerView(
                    settings = settings,
                    onSettingsChange = onSettingsChange,
                    isDarkMode = isDarkMode
                )

                Spacer(modifier = Modifier.height(24.dp))
            }

            // 2. Audio Quality & Format Section
            item {
                Text(
                    text = "AUDIO QUALITY",
                    color = secondaryTextColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, borderColor, RoundedCornerShape(16.dp))
                        .background(cardBg)
                ) {
                    SettingActionItem(
                        icon = Icons.Default.Headphones,
                        title = "Streaming Quality",
                        subtitle = settings.audioQuality,
                        badge = "Lossless",
                        onClick = { showQualityDialog = true },
                        isDarkMode = isDarkMode,
                        testTag = "audio_quality_setting"
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            // 3. Playback Features (Crossfade, Gapless, Mono Audio)
            item {
                Text(
                    text = "PLAYBACK ENGINE",
                    color = secondaryTextColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, borderColor, RoundedCornerShape(16.dp))
                        .background(cardBg)
                ) {
                    // Crossfade Switch
                    SettingToggleItem(
                        icon = Icons.Default.Sync,
                        title = "Crossfade",
                        subtitle = if (settings.crossfadeEnabled) "${String.format("%.1f", settings.crossfadeDurationSeconds)}s seamless track transition" else "Disabled",
                        checked = settings.crossfadeEnabled,
                        onCheckedChange = { onSettingsChange(settings.copy(crossfadeEnabled = it)) },
                        isDarkMode = isDarkMode,
                        testTag = "crossfade_toggle"
                    )

                    // Crossfade duration slider if enabled
                    if (settings.crossfadeEnabled) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(if (isDarkMode) Color(0xFF13151D) else Color(0xFFF9FAFB))
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Transition Duration", color = secondaryTextColor, fontSize = 12.sp)
                                Text("${String.format("%.1f", settings.crossfadeDurationSeconds)} s", color = accentEmerald, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Slider(
                                value = settings.crossfadeDurationSeconds,
                                onValueChange = { onSettingsChange(settings.copy(crossfadeDurationSeconds = (it * 2).toInt() / 2f)) },
                                valueRange = 1f..12f,
                                colors = SliderDefaults.colors(
                                    thumbColor = accentEmerald,
                                    activeTrackColor = accentEmerald,
                                    inactiveTrackColor = if (isDarkMode) Color(0xFF2D323F) else Color(0xFFE5E7EB)
                                ),
                                modifier = Modifier.testTag("crossfade_slider")
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(dividerColor)
                    )

                    // Gapless Playback
                    SettingToggleItem(
                        icon = Icons.Default.MusicNote,
                        title = "Gapless Playback",
                        subtitle = "Eliminates silence between live or concept album tracks",
                        checked = settings.gaplessPlayback,
                        onCheckedChange = { onSettingsChange(settings.copy(gaplessPlayback = it)) },
                        isDarkMode = isDarkMode,
                        testTag = "gapless_playback_toggle"
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(dividerColor)
                    )

                    // Mono Audio
                    SettingToggleItem(
                        icon = Icons.AutoMirrored.Filled.VolumeUp,
                        title = "Mono Audio",
                        subtitle = "Combines left and right audio channels into one",
                        checked = settings.monoAudio,
                        onCheckedChange = { onSettingsChange(settings.copy(monoAudio = it)) },
                        isDarkMode = isDarkMode,
                        testTag = "mono_audio_toggle"
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            // 4. About Novi & Version
            item {
                Text(
                    text = "ABOUT NOVI",
                    color = secondaryTextColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, borderColor, RoundedCornerShape(16.dp))
                        .background(cardBg)
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Novi",
                            color = primaryTextColor,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(accentEmerald.copy(alpha = 0.15f))
                                .clickable {
                                    if (!settings.developerMode) {
                                        showDevPasscodeDialog = true
                                    } else {
                                        onSettingsChange(settings.copy(developerMode = false))
                                    }
                                }
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = if (settings.developerMode) "DEV MODE ON" else "v0.1.0-beta-build.1",
                                color = accentEmerald,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Novi is music, unfinished — on purpose. A beta app for discovery and sharing, built with your feedback.",
                        color = secondaryTextColor,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            // 5. Developer Mode & Studio Equalizer Controls
            item {
                Text(
                    text = "DEVELOPER OPTIONS",
                    color = secondaryTextColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, borderColor, RoundedCornerShape(16.dp))
                        .background(cardBg)
                ) {
                    SettingToggleItem(
                        icon = Icons.Default.Tune,
                        title = "Developer Mode",
                        subtitle = if (settings.developerMode) {
                            "Active • Monochromatic Theme Wallpaper & 6-Band Studio Curve Equalizer"
                        } else {
                            "Unlock Custom Theme Wallpaper & Detailed 6-Band Equalizer"
                        },
                        checked = settings.developerMode,
                        onCheckedChange = { isChecked ->
                            if (isChecked) {
                                showDevPasscodeDialog = true
                            } else {
                                onSettingsChange(settings.copy(developerMode = false))
                            }
                        },
                        isDarkMode = isDarkMode || isDevMode,
                        testTag = "developer_mode_toggle"
                    )

                    if (settings.developerMode) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(dividerColor)
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(accentEmerald)
                            )

                            Text(
                                text = "Developer mode features activated: Full-screen monochromatic satin-wave background wallpaper and 6-band detailed studio frequency curve equalizer are live.",
                                color = secondaryTextColor,
                                fontSize = 12.sp,
                                lineHeight = 17.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))
            }
        }

        // Developer Passcode Dialog
        if (showDevPasscodeDialog) {
            DevPasscodeDialog(
                onDismiss = { showDevPasscodeDialog = false },
                onSuccess = {
                    showDevPasscodeDialog = false
                    onSettingsChange(settings.copy(developerMode = true))
                },
                isDarkMode = isDarkMode || isDevMode
            )
        }

        // Quality Selection Dialog
        if (showQualityDialog) {
            AlertDialog(
                onDismissRequest = { showQualityDialog = false },
                containerColor = if (isDarkMode) Color(0xFF1A1D24) else Color.White,
                title = { Text("Audio Streaming Quality", color = primaryTextColor, fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        audioQualityOptions.forEach { (option, description) ->
                            val isSelected = settings.audioQuality == option
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable {
                                        onSettingsChange(settings.copy(audioQuality = option))
                                        showQualityDialog = false
                                    }
                                    .padding(vertical = 8.dp, horizontal = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = if (isSelected) Icons.Default.RadioButtonChecked else Icons.Default.RadioButtonUnchecked,
                                    contentDescription = null,
                                    tint = if (isSelected) accentEmerald else secondaryTextColor
                                )
                                Column {
                                    Text(
                                        text = option,
                                        color = if (isSelected) accentEmerald else primaryTextColor,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = description,
                                        color = secondaryTextColor,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showQualityDialog = false }) {
                        Text("Done", color = accentEmerald)
                    }
                }
            )
        }
    }
}

@Composable
fun SettingActionItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    isDarkMode: Boolean,
    modifier: Modifier = Modifier,
    badge: String? = null,
    testTag: String = ""
) {
    val textPrimary = if (isDarkMode) Color.White else Color(0xFF111827)
    val textSecondary = if (isDarkMode) Color(0xFF9CA3AF) else Color(0xFF6B7280)
    val iconTint = if (isDarkMode) Color(0xFF10B981) else Color(0xFF00A86B)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(iconTint.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(22.dp)
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = title,
                    color = textPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
                if (badge != null) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(iconTint.copy(alpha = 0.18f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = badge,
                            color = iconTint,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Text(
                text = subtitle,
                color = textSecondary,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
private fun TelemetryCard(
    icon: ImageVector,
    label: String,
    value: String,
    isDarkMode: Boolean,
    modifier: Modifier = Modifier
) {
    val cardBg = if (isDarkMode) Color(0xFF181B24) else Color.White
    val textPrimary = if (isDarkMode) Color.White else Color(0xFF111827)
    val textSecondary = if (isDarkMode) Color(0xFF9CA3AF) else Color(0xFF6B7280)
    val accentEmerald = if (isDarkMode) Color(0xFF10B981) else Color(0xFF00A86B)

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(cardBg)
            .padding(10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accentEmerald,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = label,
                color = textSecondary,
                fontSize = 11.sp
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            color = textPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
