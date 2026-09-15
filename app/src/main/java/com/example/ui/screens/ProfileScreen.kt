package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Crop
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AudioAppSettings
import com.example.model.UserProfile
import com.example.ui.components.EqualizerView
import com.example.ui.components.ProfilePhotoCropperDialog
import com.example.ui.components.UserAvatarView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    isDarkMode: Boolean,
    onToggleDarkMode: (Boolean) -> Unit,
    userProfile: UserProfile = UserProfile(),
    onUpdateProfile: (name: String, handle: String, bio: String, avatarId: Int, customAvatarUri: String?) -> Unit = { _, _, _, _, _ -> },
    audioSettings: AudioAppSettings = AudioAppSettings(),
    onUpdateAudioSettings: (AudioAppSettings) -> Unit = {},
    onOpenFullSettings: () -> Unit = {},
    onLogOut: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showEditProfileSheet by remember { mutableStateOf(false) }
    var showLanguageSheet by remember { mutableStateOf(false) }
    val editSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val languageSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Image Picker and Cropper State
    var rawSelectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var showCropperDialog by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            rawSelectedImageUri = uri
            showCropperDialog = true
        }
    }

    val supportedLanguages = listOf(
        "English (US)" to "English (United States)",
        "English (UK)" to "English (United Kingdom)",
        "Español" to "Spanish",
        "Français" to "French",
        "Deutsch" to "German",
        "日本語" to "Japanese",
        "한국어" to "Korean",
        "Português" to "Portuguese",
        "Italiano" to "Italian",
        "中文" to "Chinese (Simplified)"
    )

    // Edit form states
    var editName by remember(userProfile.name) { mutableStateOf(userProfile.name) }
    var editHandle by remember(userProfile.handle) { mutableStateOf(userProfile.handle) }
    var editBio by remember(userProfile.bio) { mutableStateOf(userProfile.bio) }
    var editAvatarId by remember(userProfile.avatarId) { mutableStateOf(userProfile.avatarId) }
    var editCustomAvatarUri by remember(userProfile.customAvatarUri) { mutableStateOf(userProfile.customAvatarUri) }

    val bgColor = if (isDarkMode) Color(0xFF0D0F14) else Color(0xFFF9FAFB)
    val cardBg = if (isDarkMode) Color(0xFF1A1D24) else Color.White
    val borderColor = if (isDarkMode) Color(0xFF2D323F) else Color(0xFFE5E7EB)
    val dividerColor = if (isDarkMode) Color(0xFF2D323F) else Color(0xFFF3F4F6)
    val primaryTextColor = if (isDarkMode) Color(0xFFF9FAFB) else Color(0xFF111827)
    val secondaryTextColor = if (isDarkMode) Color(0xFF9CA3AF) else Color(0xFF6B7280)

    // Interactive Photo Cropper Dialog
    if (showCropperDialog && rawSelectedImageUri != null) {
        ProfilePhotoCropperDialog(
            imageUri = rawSelectedImageUri!!,
            onDismiss = {
                showCropperDialog = false
                rawSelectedImageUri = null
            },
            onCropSuccess = { croppedUri ->
                showCropperDialog = false
                rawSelectedImageUri = null
                val uriStr = croppedUri.toString()
                editCustomAvatarUri = uriStr
                onUpdateProfile(
                    userProfile.name,
                    userProfile.handle,
                    userProfile.bio,
                    userProfile.avatarId,
                    uriStr
                )
            }
        )
    }

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
                    // Profile Photo with Camera Tap Overlay
                    Box(
                        contentAlignment = Alignment.BottomEnd,
                        modifier = Modifier
                            .size(90.dp)
                            .clickable {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            }
                            .testTag("profile_avatar_tap")
                    ) {
                        UserAvatarView(
                            modifier = Modifier.size(90.dp),
                            avatarId = userProfile.avatarId,
                            customAvatarUri = userProfile.customAvatarUri,
                            borderColor = borderColor
                        )

                        // Camera badge for quick photo change and crop
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF00A86B))
                                .border(2.dp, bgColor, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Change profile photo",
                                tint = Color.White,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = userProfile.name.ifBlank { "Novi Listener" },
                        color = primaryTextColor,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = userProfile.handle.ifBlank { "@novi_user" },
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
                Text(
                    text = "PREFERENCES",
                    color = secondaryTextColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Dedicated Audio & Settings Card with "Configure" button
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .border(1.dp, borderColor, RoundedCornerShape(18.dp))
                        .background(cardBg)
                        .clickable { onOpenFullSettings() }
                        .padding(16.dp)
                        .testTag("audio_and_settings_card")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color(0xFF10B981).copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.GraphicEq,
                                    contentDescription = "Audio & Settings",
                                    tint = Color(0xFF10B981),
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Column {
                                Text(
                                    text = "Audio & Settings",
                                    color = primaryTextColor,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Equalizer, Crossfade, Gapless & Mono Audio",
                                    color = secondaryTextColor,
                                    fontSize = 12.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        // Explicit "Configure" Button
                        Button(
                            onClick = onOpenFullSettings,
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isDarkMode) Color(0xFF10B981) else Color(0xFF00A86B)
                            ),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            modifier = Modifier.testTag("open_full_settings_button")
                        ) {
                            Text(
                                text = "Configure",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // General Preferences Card (Language & Dark Theme)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, borderColor, RoundedCornerShape(16.dp))
                        .background(cardBg)
                ) {
                    // Language Selection Option
                    SettingClickableItem(
                        icon = Icons.Default.Language,
                        title = "Language",
                        subtitle = audioSettings.language,
                        onClick = { showLanguageSheet = true },
                        isDarkMode = isDarkMode,
                        testTag = "profile_language_item"
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

                Spacer(modifier = Modifier.height(14.dp))

                // Account & Authentication Section Card
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, borderColor, RoundedCornerShape(16.dp))
                        .background(cardBg)
                ) {
                    SettingClickableItem(
                        icon = Icons.AutoMirrored.Filled.Logout,
                        title = "Log Out / Switch Account",
                        subtitle = "Sign out and return to the Welcome screen",
                        onClick = onLogOut,
                        isDarkMode = isDarkMode,
                        testTag = "profile_logout_button"
                    )
                }
            }
        }

        // Language Selection Modal Bottom Sheet
        if (showLanguageSheet) {
            ModalBottomSheet(
                onDismissRequest = { showLanguageSheet = false },
                sheetState = languageSheetState,
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
                        text = "Select Language",
                        color = primaryTextColor,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Choose your preferred interface and lyrics language",
                        color = secondaryTextColor,
                        fontSize = 13.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        supportedLanguages.forEach { (langKey, langDesc) ->
                            val isSelected = audioSettings.language.startsWith(langKey) || audioSettings.language == langKey
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(
                                        if (isSelected) {
                                            if (isDarkMode) Color(0xFF10B981).copy(alpha = 0.2f)
                                            else Color(0xFF00A86B).copy(alpha = 0.12f)
                                        } else Color.Transparent
                                    )
                                    .clickable {
                                        onUpdateAudioSettings(audioSettings.copy(language = langKey))
                                        showLanguageSheet = false
                                    }
                                    .padding(horizontal = 16.dp, vertical = 13.dp)
                                    .testTag("language_option_${langKey.replace(" ", "_")}"),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = langKey,
                                        color = if (isSelected) {
                                            if (isDarkMode) Color(0xFF10B981) else Color(0xFF00A86B)
                                        } else primaryTextColor,
                                        fontSize = 15.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    )
                                    Text(
                                        text = langDesc,
                                        color = secondaryTextColor,
                                        fontSize = 12.sp
                                    )
                                }

                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = if (isDarkMode) Color(0xFF10B981) else Color(0xFF00A86B),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
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

                    // Custom Photo Section & PFP Avatar Picker
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Profile Picture",
                            color = primaryTextColor,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        // Upload & Crop Button
                        OutlinedButton(
                            onClick = {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, Color(0xFF10B981)),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFF10B981)
                            ),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("upload_custom_avatar_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddPhotoAlternate,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Upload & Crop", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    if (!editCustomAvatarUri.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(cardBg)
                                .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                UserAvatarView(
                                    modifier = Modifier.size(48.dp),
                                    customAvatarUri = editCustomAvatarUri,
                                    borderColor = Color(0xFF10B981)
                                )
                                Column {
                                    Text(
                                        text = "Custom Cropped Photo Active",
                                        color = primaryTextColor,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "Tap Crop to change or Remove to use avatar",
                                        color = secondaryTextColor,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            Row {
                                OutlinedButton(
                                    onClick = {
                                        photoPickerLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    },
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, Color(0xFF10B981)),
                                    modifier = Modifier.height(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Crop,
                                        contentDescription = "Crop",
                                        tint = Color(0xFF10B981),
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                OutlinedButton(
                                    onClick = { editCustomAvatarUri = null },
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, Color(0xFFEF4444)),
                                    modifier = Modifier.height(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Remove photo",
                                        tint = Color(0xFFEF4444),
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Or choose preset avatar style:",
                        color = secondaryTextColor,
                        fontSize = 13.sp
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
                            val isSelected = editAvatarId == id && editCustomAvatarUri.isNullOrBlank()
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        editAvatarId = id
                                        editCustomAvatarUri = null
                                    }
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
                            onUpdateProfile(editName, editHandle, editBio, editAvatarId, editCustomAvatarUri)
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

@Composable
fun SettingClickableItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    isDarkMode: Boolean,
    testTag: String = ""
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp)
            .then(if (testTag.isNotEmpty()) Modifier.testTag(testTag) else Modifier),
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

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = if (isDarkMode) Color(0xFF9CA3AF) else Color(0xFF6B7280),
            modifier = Modifier.size(20.dp)
        )
    }
}
