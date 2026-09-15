package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.UserAvatarView

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Tune
import com.example.model.AudioAppSettings
import com.example.model.UserProfile
import com.example.ui.components.EqualizerView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    isDarkMode: Boolean,
    onToggleDarkMode: (Boolean) -> Unit,
    userProfile: UserProfile = UserProfile(),
    onUpdateProfile: (name: String, handle: String, bio: String, avatarId: Int) -> Unit = { _, _, _, _ -> },
    audioSettings: AudioAppSettings = AudioAppSettings(),
    onUpdateAudioSettings: (AudioAppSettings) -> Unit = {},
    onOpenFullSettings: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showEditProfileSheet by remember { mutableStateOf(false) }
    var showEqualizerSheet by remember { mutableStateOf(false) }
    val equalizerSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Edit form states
    var editName by remember(userProfile.name) { mutableStateOf(userProfile.name) }
    var editHandle by remember(userProfile.handle) { mutableStateOf(userProfile.handle) }
    var editBio by remember(userProfile.bio) { mutableStateOf(userProfile.bio) }
    var editAvatarId by remember(userProfile.avatarId) { mutableStateOf(userProfile.avatarId) }

    val editSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val bgColor = if (isDarkMode) Color(0xFF0D0F14) else Color(0xFFF9FAFB)
    val cardBg = if (isDarkMode) Color(0xFF1A1D24) else Color.White
    val borderColor = if (isDarkMode) Color(0xFF2D323F) else Color(0xFFE5E7EB)
    val dividerColor = if (isDarkMode) Color(0xFF2D323F) else Color(0xFFF3F4F6)
    val primaryTextColor = if (isDarkMode) Color(0xFFF9FAFB) else Color(0xFF111827)
    val secondaryTextColor = if (isDarkMode) Color(0xFF9CA3AF) else Color(0xFF6B7280)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(bgColor)
            .statusBarsPadding()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 120.dp, top = 20.dp)
        ) {
            // User Header
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    UserAvatarView(
                        modifier = Modifier.size(80.dp),
                        avatarId = userProfile.avatarId,
                        borderColor = borderColor
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = userProfile.name,
                        color = primaryTextColor,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = userProfile.handle,
                        color = if (isDarkMode) Color(0xFF10B981) else Color(0xFF00A86B),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )

                    if (userProfile.bio.isNotBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = userProfile.bio,
                            color = secondaryTextColor,
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Edit Profile Button below user's name
                    OutlinedButton(
                        onClick = {
                            editName = userProfile.name
                            editHandle = userProfile.handle
                            editBio = userProfile.bio
                            editAvatarId = userProfile.avatarId
                            showEditProfileSheet = true
                        },
                        modifier = Modifier.testTag("edit_profile_button"),
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, if (isDarkMode) Color(0xFF10B981) else Color(0xFF00A86B)),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = if (isDarkMode) Color(0xFF10B981) else Color(0xFF00A86B)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Profile",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Edit Profile",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Stats Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
                            .background(cardBg)
                            .padding(vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatItem(count = "24", label = "Favorites", isDarkMode = isDarkMode)
                        Box(
                            modifier = Modifier
                                .size(width = 1.dp, height = 36.dp)
                                .background(borderColor)
                        )
                        StatItem(count = "8", label = "Playlists", isDarkMode = isDarkMode)
                        Box(
                            modifier = Modifier
                                .size(width = 1.dp, height = 36.dp)
                                .background(borderColor)
                        )
                        StatItem(count = "19", label = "Artists", isDarkMode = isDarkMode)
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))
            }

            // Preferences Section
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Audio & Settings",
                        color = primaryTextColor,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Configure",
                        color = if (isDarkMode) Color(0xFF10B981) else Color(0xFF00A86B),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .clickable { onOpenFullSettings() }
                            .testTag("open_full_settings_button")
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Equalizer Card (Acoustic, Jazz, Bass Boost, Custom interactive preview)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ) {
                    EqualizerView(
                        settings = audioSettings,
                        onSettingsChange = onUpdateAudioSettings,
                        isDarkMode = isDarkMode
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, borderColor, RoundedCornerShape(16.dp))
                        .background(cardBg)
                ) {
                    // Audio Quality Option
                    SettingToggleItem(
                        icon = Icons.Default.Headphones,
                        title = "Lossless Audio (${audioSettings.audioQuality.substringBefore(" (")})",
                        subtitle = "Hi-Res studio sound with low jitter",
                        checked = true,
                        onCheckedChange = { onOpenFullSettings() },
                        isDarkMode = isDarkMode,
                        testTag = "lossless_audio_toggle"
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(dividerColor)
                    )

                    // Crossfade Switch
                    SettingToggleItem(
                        icon = Icons.Default.Sync,
                        title = "Crossfade",
                        subtitle = if (audioSettings.crossfadeEnabled) "${String.format("%.1f", audioSettings.crossfadeDurationSeconds)}s seamless track transition" else "Disabled",
                        checked = audioSettings.crossfadeEnabled,
                        onCheckedChange = { onUpdateAudioSettings(audioSettings.copy(crossfadeEnabled = it)) },
                        isDarkMode = isDarkMode,
                        testTag = "crossfade_toggle"
                    )

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
                        subtitle = "Eliminates silence between live tracks",
                        checked = audioSettings.gaplessPlayback,
                        onCheckedChange = { onUpdateAudioSettings(audioSettings.copy(gaplessPlayback = it)) },
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
                        subtitle = "Combines stereo channels for single earbud use",
                        checked = audioSettings.monoAudio,
                        onCheckedChange = { onUpdateAudioSettings(audioSettings.copy(monoAudio = it)) },
                        isDarkMode = isDarkMode,
                        testTag = "mono_audio_toggle"
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(dividerColor)
                    )

                    // Languages
                    SettingToggleItem(
                        icon = Icons.Default.Language,
                        title = "Language",
                        subtitle = audioSettings.language,
                        checked = true,
                        onCheckedChange = { onOpenFullSettings() },
                        isDarkMode = isDarkMode,
                        testTag = "language_toggle"
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(dividerColor)
                    )

                    // Developer Mode
                    SettingToggleItem(
                        icon = Icons.Default.Code,
                        title = "Developer Mode",
                        subtitle = if (audioSettings.developerMode) "Telemetry & buffer logs enabled" else "Disabled",
                        checked = audioSettings.developerMode,
                        onCheckedChange = { onUpdateAudioSettings(audioSettings.copy(developerMode = it)) },
                        isDarkMode = isDarkMode,
                        testTag = "developer_mode_toggle"
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(dividerColor)
                    )

                    // Dark Theme Option
                    SettingToggleItem(
                        icon = if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                        title = "Dark Theme",
                        subtitle = if (isDarkMode) "Enabled • Sleek dark aesthetic" else "Disabled • Clean light aesthetic",
                        checked = isDarkMode,
                        onCheckedChange = onToggleDarkMode,
                        isDarkMode = isDarkMode,
                        testTag = "dark_mode_toggle"
                    )
                }
            }
        }

        // Edit Profile Modal Bottom Sheet
        if (showEditProfileSheet) {
            ModalBottomSheet(
                onDismissRequest = { showEditProfileSheet = false },
                sheetState = editSheetState,
                containerColor = if (isDarkMode) Color(0xFF161922) else Color.White,
                contentColor = primaryTextColor
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 36.dp)
                ) {
                    Text(
                        text = "Edit Profile",
                        color = primaryTextColor,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Customize your avatar, display name, handle and bio",
                        color = secondaryTextColor,
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // PFP Avatar Picker
                    Text(
                        text = "Select Avatar (PFP)",
                        color = primaryTextColor,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        listOf(
                            1 to "Classic",
                            2 to "Cyberpunk",
                            3 to "Synth",
                            4 to "Audiophile"
                        ).forEach { (id, label) ->
                            val isSelected = editAvatarId == id
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { editAvatarId = id }
                                    .padding(6.dp)
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(62.dp)
                                        .border(
                                            width = if (isSelected) 3.dp else 1.dp,
                                            color = if (isSelected) Color(0xFF10B981) else borderColor,
                                            shape = androidx.compose.foundation.shape.CircleShape
                                        )
                                        .padding(2.dp)
                                ) {
                                    UserAvatarView(
                                        modifier = Modifier.fillMaxSize(),
                                        avatarId = id,
                                        borderColor = Color.Transparent
                                    )
                                    if (isSelected) {
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.BottomEnd)
                                                .size(20.dp)
                                                .clip(androidx.compose.foundation.shape.CircleShape)
                                                .background(Color(0xFF10B981)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Selected",
                                                tint = Color.White,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = label,
                                    color = if (isSelected) Color(0xFF10B981) else secondaryTextColor,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Name Field
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Display Name") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF10B981),
                            unfocusedBorderColor = borderColor,
                            focusedTextColor = primaryTextColor,
                            unfocusedTextColor = primaryTextColor,
                            focusedLabelColor = Color(0xFF10B981),
                            unfocusedLabelColor = secondaryTextColor
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("edit_name_input")
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Handle Field
                    OutlinedTextField(
                        value = editHandle,
                        onValueChange = { editHandle = it },
                        label = { Text("Username / Handle") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF10B981),
                            unfocusedBorderColor = borderColor,
                            focusedTextColor = primaryTextColor,
                            unfocusedTextColor = primaryTextColor,
                            focusedLabelColor = Color(0xFF10B981),
                            unfocusedLabelColor = secondaryTextColor
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("edit_handle_input")
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Bio Field
                    OutlinedTextField(
                        value = editBio,
                        onValueChange = { editBio = it },
                        label = { Text("Bio / Tagline") },
                        singleLine = false,
                        maxLines = 2,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF10B981),
                            unfocusedBorderColor = borderColor,
                            focusedTextColor = primaryTextColor,
                            unfocusedTextColor = primaryTextColor,
                            focusedLabelColor = Color(0xFF10B981),
                            unfocusedLabelColor = secondaryTextColor
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("edit_bio_input")
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Save Button
                    Button(
                        onClick = {
                            onUpdateProfile(editName, editHandle, editBio, editAvatarId)
                            showEditProfileSheet = false
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("save_profile_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF00A86B)
                        ),
                        shape = RoundedCornerShape(25.dp)
                    ) {
                        Text(
                            text = "Save Changes",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatItem(
    count: String,
    label: String,
    isDarkMode: Boolean
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = count,
            color = if (isDarkMode) Color.White else Color(0xFF111827),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            color = if (isDarkMode) Color(0xFF9CA3AF) else Color(0xFF6B7280),
            fontSize = 12.sp
        )
    }
}

@Composable
fun SettingToggleItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    isDarkMode: Boolean,
    testTag: String = ""
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF00A86B),
                modifier = Modifier.size(22.dp)
            )
            Column {
                Text(
                    text = title,
                    color = if (isDarkMode) Color(0xFFF9FAFB) else Color(0xFF111827),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = subtitle,
                    color = if (isDarkMode) Color(0xFF9CA3AF) else Color(0xFF6B7280),
                    fontSize = 12.sp
                )
            }
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = if (testTag.isNotEmpty()) Modifier.testTag(testTag) else Modifier,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF00A86B),
                uncheckedThumbColor = if (isDarkMode) Color(0xFF9CA3AF) else Color(0xFFD1D5DB),
                uncheckedTrackColor = if (isDarkMode) Color(0xFF2D323F) else Color(0xFFE5E7EB)
            )
        )
    }
}
