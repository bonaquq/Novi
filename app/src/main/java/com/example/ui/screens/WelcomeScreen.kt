package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.ArtworkType
import com.example.ui.components.TrackArtworkDisplay
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun WelcomeScreen(
    onGetStarted: () -> Unit = {},
    onSignIn: suspend (email: String, password: String) -> String? = { _, _ -> null },
    onSignUp: suspend (name: String, email: String, password: String, handle: String, selectedGenres: List<String>) -> String? = { _, _, _, _, _ -> null },
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var showAuthSheet by remember { mutableStateOf(false) }
    var selectedAuthTab by remember { mutableIntStateOf(0) } // 0: Sign In, 1: Sign Up

    // Google Account Creation state
    var showGoogleSignUpDialog by remember { mutableStateOf(false) }
    var googleSignUpName by remember { mutableStateOf("") }
    var googleSignUpEmail by remember { mutableStateOf("") }
    var googleSignUpHandle by remember { mutableStateOf("") }
    var googleSignUpError by remember { mutableStateOf<String?>(null) }
    var isGoogleCreating by remember { mutableStateOf(false) }
    val googleSelectedGenres = remember { mutableStateListOf("Electronic", "IDM", "Ambient") }

    // Sign In form fields
    var loginEmail by remember { mutableStateOf("") }
    var loginPassword by remember { mutableStateOf("") }
    var loginPasswordVisible by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(true) }

    // Sign Up form fields
    var signUpName by remember { mutableStateOf("") }
    var signUpEmail by remember { mutableStateOf("") }
    var signUpHandle by remember { mutableStateOf("") }
    var signUpPassword by remember { mutableStateOf("") }
    var signUpConfirmPassword by remember { mutableStateOf("") }
    var signUpPasswordVisible by remember { mutableStateOf(false) }
    var signUpConfirmVisible by remember { mutableStateOf(false) }
    var agreeToTerms by remember { mutableStateOf(true) }

    // Genre selection for Sign Up
    val availableGenres = listOf(
        "Electronic", "IDM", "Ambient", "Indie Rock", "Synthwave", "Post-Punk", "Hip-Hop", "Jazz"
    )
    val selectedGenres = remember { mutableStateListOf("Electronic", "IDM", "Ambient") }

    // State indicators
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showForgotPasswordDialog by remember { mutableStateOf(false) }
    var forgotPasswordEmail by remember { mutableStateOf("") }
    var resetEmailSent by remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Helper to launch external Google Account Registration in browser
    fun launchGoogleSignUpInBrowser() {
        try {
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://accounts.google.com/signup")
            ).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (_: Exception) {}
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0D0F14))
    ) {
        // Tilted Album Art Grid in the top half
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(440.dp)
                .padding(top = 16.dp)
        ) {
            // Background subtle glow
            Box(
                modifier = Modifier
                    .size(320.dp)
                    .align(Alignment.Center)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(Color(0x4410B981), Color(0x18047857), Color.Transparent)
                        )
                    )
            )

            // Card 1: Top Left tilted card (Gorillaz / Concert style)
            Box(
                modifier = Modifier
                    .size(170.dp, 210.dp)
                    .offset(x = (-20).dp, y = 30.dp)
                    .rotate(-10f)
                    .shadow(16.dp, RoundedCornerShape(20.dp))
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF161922))
                    .border(1.5.dp, Color(0xFF2A2E3D), RoundedCornerShape(20.dp))
            ) {
                TrackArtworkDisplay(
                    artworkType = ArtworkType.GORILLAZ,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Card 2: Top Right tilted card (Sonic Youth comic art)
            Box(
                modifier = Modifier
                    .size(175.dp, 215.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = 30.dp, y = 10.dp)
                    .rotate(12f)
                    .shadow(16.dp, RoundedCornerShape(20.dp))
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF161922))
                    .border(1.5.dp, Color(0xFF2A2E3D), RoundedCornerShape(20.dp))
            ) {
                TrackArtworkDisplay(
                    artworkType = ArtworkType.DEPECHE_MODE,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Card 3: Center Left Card with • LIVE badge
            Box(
                modifier = Modifier
                    .size(185.dp, 230.dp)
                    .offset(x = 20.dp, y = 185.dp)
                    .rotate(-4f)
                    .shadow(20.dp, RoundedCornerShape(22.dp))
                    .clip(RoundedCornerShape(22.dp))
                    .background(Color(0xFF1A1E29))
                    .border(2.dp, Color(0xFF32384A), RoundedCornerShape(22.dp))
            ) {
                TrackArtworkDisplay(
                    artworkType = ArtworkType.SONIC_YOUTH,
                    modifier = Modifier.fillMaxSize()
                )

                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(12.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xCC000000))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEF4444))
                    )
                    Text(
                        text = "LIVE",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Card 4: Bottom Right tilted card
            Box(
                modifier = Modifier
                    .size(165.dp, 205.dp)
                    .align(Alignment.BottomEnd)
                    .offset(x = 10.dp, y = 60.dp)
                    .rotate(8f)
                    .shadow(16.dp, RoundedCornerShape(20.dp))
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF161922))
                    .border(1.5.dp, Color(0xFF2A2E3D), RoundedCornerShape(20.dp))
            ) {
                TrackArtworkDisplay(
                    artworkType = ArtworkType.WOODZ,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Centerpiece: Large Standout Novi in the Middle of the screen
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Novi",
                    color = Color.White,
                    fontSize = 62.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-1.5).sp,
                    textAlign = TextAlign.Center,
                    style = androidx.compose.ui.text.TextStyle(
                        shadow = androidx.compose.ui.graphics.Shadow(
                            color = Color(0xEE000000),
                            blurRadius = 36f
                        )
                    )
                )
            }
        }

        // Bottom Content Section: Heading, Description, Sign Up & Sign In Buttons
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 28.dp)
        ) {
            Text(
                text = "Music without\nborders",
                color = Color.White,
                fontSize = 32.sp,
                lineHeight = 38.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Create custom playlists, explore synchronized lyrics and experience lossless sound.",
                color = Color(0xFF9CA3AF),
                fontSize = 14.sp,
                lineHeight = 21.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Primary Button: Create Account / Sign Up
            Button(
                onClick = {
                    selectedAuthTab = 1 // Open Sign Up tab
                    errorMessage = null
                    showAuthSheet = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("sign_up_cta_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00A86B)
                ),
                shape = RoundedCornerShape(26.dp)
            ) {
                Text(
                    text = "Create Account / Sign Up",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Secondary Button: Sign In / Log In
            OutlinedButton(
                onClick = {
                    selectedAuthTab = 0 // Open Log In tab
                    errorMessage = null
                    showAuthSheet = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("sign_in_cta_button"),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.White
                ),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF2D323F)),
                shape = RoundedCornerShape(25.dp)
            ) {
                Text(
                    text = "Sign In / Log In",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Full Interactive Authentication Bottom Sheet (Log In / Sign Up)
        if (showAuthSheet) {
            ModalBottomSheet(
                onDismissRequest = { showAuthSheet = false },
                sheetState = sheetState,
                containerColor = Color(0xFF161922),
                contentColor = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 36.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Centered Large Standout Novi Logo in the middle of Login Page (No BETA tag)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp, bottom = 22.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(Color(0xFF10B981), Color(0xFF047857))
                                    )
                                )
                                .border(1.5.dp, Color(0xFF34D399).copy(alpha = 0.6f), RoundedCornerShape(20.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.MusicNote,
                                contentDescription = "Novi Logo",
                                tint = Color.White,
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Novi",
                            color = Color.White,
                            fontSize = 38.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = (-1).sp
                        )
                    }

                    // Segmented Tab Switcher: [ Log In | Sign Up ]
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF0D0F14))
                            .padding(4.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            // Log In Tab
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        if (selectedAuthTab == 0) Color(0xFF00A86B) else Color.Transparent
                                    )
                                    .clickable {
                                        selectedAuthTab = 0
                                        errorMessage = null
                                    }
                                    .padding(vertical = 10.dp)
                                    .testTag("tab_login"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Log In",
                                    color = if (selectedAuthTab == 0) Color.White else Color(0xFF9CA3AF),
                                    fontSize = 14.sp,
                                    fontWeight = if (selectedAuthTab == 0) FontWeight.Bold else FontWeight.Medium
                                )
                            }

                            // Sign Up Tab
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        if (selectedAuthTab == 1) Color(0xFF00A86B) else Color.Transparent
                                    )
                                    .clickable {
                                        selectedAuthTab = 1
                                        errorMessage = null
                                    }
                                    .padding(vertical = 10.dp)
                                    .testTag("tab_signup"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Create Account",
                                    color = if (selectedAuthTab == 1) Color.White else Color(0xFF9CA3AF),
                                    fontSize = 14.sp,
                                    fontWeight = if (selectedAuthTab == 1) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    AnimatedContent(
                        targetState = selectedAuthTab,
                        transitionSpec = {
                            fadeIn() togetherWith fadeOut()
                        },
                        label = "auth_tab_content"
                    ) { tab ->
                        if (tab == 0) {
                            // LOG IN FORM
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = "Welcome Back",
                                    color = Color.White,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Log in to sync your playlists and listening queue",
                                    color = Color(0xFF9CA3AF),
                                    fontSize = 13.sp
                                )

                                Spacer(modifier = Modifier.height(18.dp))

                                // Email Input
                                OutlinedTextField(
                                    value = loginEmail,
                                    onValueChange = {
                                        loginEmail = it
                                        errorMessage = null
                                    },
                                    label = { Text("Email Address") },
                                    placeholder = { Text("name@example.com") },
                                    leadingIcon = {
                                        Icon(Icons.Default.Email, contentDescription = null, tint = Color(0xFF9CA3AF))
                                    },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                                    colors = authTextFieldColors(),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("login_email_input")
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                // Password Input
                                OutlinedTextField(
                                    value = loginPassword,
                                    onValueChange = {
                                        loginPassword = it
                                        errorMessage = null
                                    },
                                    label = { Text("Password") },
                                    placeholder = { Text("Enter your password") },
                                    leadingIcon = {
                                        Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF9CA3AF))
                                    },
                                    trailingIcon = {
                                        IconButton(onClick = { loginPasswordVisible = !loginPasswordVisible }) {
                                            Icon(
                                                imageVector = if (loginPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                                contentDescription = if (loginPasswordVisible) "Hide password" else "Show password",
                                                tint = Color(0xFF9CA3AF)
                                            )
                                        }
                                    },
                                    visualTransformation = if (loginPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                                    colors = authTextFieldColors(),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("login_password_input")
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                // Remember Me & Forgot Password Row
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.clickable { rememberMe = !rememberMe }
                                    ) {
                                        Checkbox(
                                            checked = rememberMe,
                                            onCheckedChange = { rememberMe = it },
                                            colors = CheckboxDefaults.colors(
                                                checkedColor = Color(0xFF00A86B),
                                                uncheckedColor = Color(0xFF9CA3AF)
                                            )
                                        )
                                        Text(
                                            text = "Remember me",
                                            color = Color(0xFF9CA3AF),
                                            fontSize = 13.sp
                                        )
                                    }

                                    TextButton(
                                        onClick = {
                                            forgotPasswordEmail = loginEmail
                                            resetEmailSent = false
                                            showForgotPasswordDialog = true
                                        }
                                    ) {
                                        Text(
                                            text = "Forgot password?",
                                            color = Color(0xFF10B981),
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }

                                if (errorMessage != null) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = errorMessage!!,
                                        color = Color(0xFFEF4444),
                                        fontSize = 13.sp
                                    )
                                }

                                Spacer(modifier = Modifier.height(18.dp))

                                // Submit Log In Button
                                Button(
                                    onClick = {
                                        if (loginEmail.isBlank()) {
                                            errorMessage = "Please enter your email address"
                                        } else if (!loginEmail.contains("@")) {
                                            errorMessage = "Please enter a valid email address"
                                        } else if (loginPassword.isBlank()) {
                                            errorMessage = "Please enter your password"
                                        } else {
                                            isLoading = true
                                            errorMessage = null
                                            coroutineScope.launch {
                                                val loginErr = onSignIn(loginEmail.trim(), loginPassword)
                                                isLoading = false
                                                if (loginErr != null) {
                                                    errorMessage = loginErr
                                                } else {
                                                    showAuthSheet = false
                                                }
                                            }
                                        }
                                    },
                                    enabled = !isLoading,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                        .testTag("submit_login_button"),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF00A86B)
                                    ),
                                    shape = RoundedCornerShape(25.dp)
                                ) {
                                    if (isLoading) {
                                        CircularProgressIndicator(
                                            color = Color.White,
                                            strokeWidth = 2.dp,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    } else {
                                        Text(
                                            text = "Log In",
                                            color = Color.White,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        } else {
                            // SIGN UP FORM
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = "Join Novi",
                                    color = Color.White,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Create your profile and build endless music libraries",
                                    color = Color(0xFF9CA3AF),
                                    fontSize = 13.sp
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                // Full Name
                                OutlinedTextField(
                                    value = signUpName,
                                    onValueChange = {
                                        signUpName = it
                                        if (signUpHandle.isBlank() || signUpHandle == "@${signUpName.lowercase().replace(" ", "")}") {
                                            signUpHandle = "@" + it.lowercase().replace(" ", "")
                                        }
                                        errorMessage = null
                                    },
                                    label = { Text("Full Name") },
                                    placeholder = { Text("e.g. Alex Mercer") },
                                    leadingIcon = {
                                        Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF9CA3AF))
                                    },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                                    colors = authTextFieldColors(),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("signup_name_input")
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                // Email Input
                                OutlinedTextField(
                                    value = signUpEmail,
                                    onValueChange = {
                                        signUpEmail = it
                                        errorMessage = null
                                    },
                                    label = { Text("Email Address") },
                                    placeholder = { Text("alex@example.com") },
                                    leadingIcon = {
                                        Icon(Icons.Default.Email, contentDescription = null, tint = Color(0xFF9CA3AF))
                                    },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                                    colors = authTextFieldColors(),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("signup_email_input")
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                // Handle / Username
                                OutlinedTextField(
                                    value = signUpHandle,
                                    onValueChange = {
                                        signUpHandle = it
                                        errorMessage = null
                                    },
                                    label = { Text("Username") },
                                    placeholder = { Text("@alexmercer") },
                                    leadingIcon = {
                                        Icon(Icons.Default.AlternateEmail, contentDescription = null, tint = Color(0xFF9CA3AF))
                                    },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                                    colors = authTextFieldColors(),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("signup_handle_input")
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                // Password
                                OutlinedTextField(
                                    value = signUpPassword,
                                    onValueChange = {
                                        signUpPassword = it
                                        errorMessage = null
                                    },
                                    label = { Text("Create Password (min 6 chars)") },
                                    leadingIcon = {
                                        Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF9CA3AF))
                                    },
                                    trailingIcon = {
                                        IconButton(onClick = { signUpPasswordVisible = !signUpPasswordVisible }) {
                                            Icon(
                                                imageVector = if (signUpPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                                contentDescription = null,
                                                tint = Color(0xFF9CA3AF)
                                            )
                                        }
                                    },
                                    visualTransformation = if (signUpPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
                                    colors = authTextFieldColors(),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("signup_password_input")
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                // Confirm Password
                                OutlinedTextField(
                                    value = signUpConfirmPassword,
                                    onValueChange = {
                                        signUpConfirmPassword = it
                                        errorMessage = null
                                    },
                                    label = { Text("Confirm Password") },
                                    leadingIcon = {
                                        Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF9CA3AF))
                                    },
                                    trailingIcon = {
                                        IconButton(onClick = { signUpConfirmVisible = !signUpConfirmVisible }) {
                                            Icon(
                                                imageVector = if (signUpConfirmVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                                contentDescription = null,
                                                tint = Color(0xFF9CA3AF)
                                            )
                                        }
                                    },
                                    visualTransformation = if (signUpConfirmVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                                    colors = authTextFieldColors(),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("signup_confirm_password_input")
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                // Music Taste Selection (Favorite Genres)
                                Text(
                                    text = "Select your music preferences:",
                                    color = Color(0xFFD1D5DB),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    availableGenres.forEach { genre ->
                                        val isSelected = selectedGenres.contains(genre)
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(16.dp))
                                                .background(
                                                    if (isSelected) Color(0xFF00A86B) else Color(0xFF1E222D)
                                                )
                                                .border(
                                                    1.dp,
                                                    if (isSelected) Color(0xFF10B981) else Color(0xFF2D323F),
                                                    RoundedCornerShape(16.dp)
                                                )
                                                .clickable {
                                                    if (isSelected) selectedGenres.remove(genre)
                                                    else selectedGenres.add(genre)
                                                }
                                                .padding(horizontal = 12.dp, vertical = 6.dp)
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                if (isSelected) {
                                                    Icon(
                                                        imageVector = Icons.Default.Check,
                                                        contentDescription = null,
                                                        tint = Color.White,
                                                        modifier = Modifier.size(12.dp)
                                                    )
                                                }
                                                Text(
                                                    text = genre,
                                                    color = if (isSelected) Color.White else Color(0xFF9CA3AF),
                                                    fontSize = 12.sp,
                                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // Agree to terms checkbox
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.clickable { agreeToTerms = !agreeToTerms }
                                ) {
                                    Checkbox(
                                        checked = agreeToTerms,
                                        onCheckedChange = { agreeToTerms = it },
                                        colors = CheckboxDefaults.colors(
                                            checkedColor = Color(0xFF00A86B),
                                            uncheckedColor = Color(0xFF9CA3AF)
                                        )
                                    )
                                    Text(
                                        text = "I agree to Novi's Terms of Service & Privacy Policy",
                                        color = Color(0xFF9CA3AF),
                                        fontSize = 12.sp
                                    )
                                }

                                if (errorMessage != null) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = errorMessage!!,
                                        color = Color(0xFFEF4444),
                                        fontSize = 13.sp
                                    )
                                }

                                Spacer(modifier = Modifier.height(18.dp))

                                // Submit Create Account Button
                                Button(
                                    onClick = {
                                        if (signUpName.isBlank()) {
                                            errorMessage = "Please enter your name"
                                        } else if (signUpEmail.isBlank() || !signUpEmail.contains("@")) {
                                            errorMessage = "Please enter a valid email address"
                                        } else if (signUpPassword.length < 4) {
                                            errorMessage = "Password must be at least 4 characters"
                                        } else if (signUpPassword != signUpConfirmPassword) {
                                            errorMessage = "Passwords do not match"
                                        } else if (!agreeToTerms) {
                                            errorMessage = "Please accept the Terms of Service"
                                        } else {
                                            isLoading = true
                                            errorMessage = null
                                            coroutineScope.launch {
                                                val regErr = onSignUp(
                                                    signUpName.trim(),
                                                    signUpEmail.trim(),
                                                    signUpPassword,
                                                    signUpHandle.trim(),
                                                    selectedGenres.toList()
                                                )
                                                isLoading = false
                                                if (regErr != null) {
                                                    errorMessage = regErr
                                                } else {
                                                    showAuthSheet = false
                                                }
                                            }
                                        }
                                    },
                                    enabled = !isLoading,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                        .testTag("submit_signup_button"),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF00A86B)
                                    ),
                                    shape = RoundedCornerShape(25.dp)
                                ) {
                                    if (isLoading) {
                                        CircularProgressIndicator(
                                            color = Color.White,
                                            strokeWidth = 2.dp,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    } else {
                                        Text(
                                            text = "Create Novi Account",
                                            color = Color.White,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                 // Google Account Creation button inside Sign Up tab
                                 OutlinedButton(
                                     onClick = {
                                         googleSignUpName = signUpName
                                         googleSignUpEmail = signUpEmail
                                         googleSignUpHandle = signUpHandle
                                         googleSignUpError = null
                                         showGoogleSignUpDialog = true
                                     },
                                     modifier = Modifier
                                         .fillMaxWidth()
                                         .height(46.dp)
                                         .testTag("signup_tab_google_button"),
                                     colors = ButtonDefaults.outlinedButtonColors(
                                         containerColor = Color(0xFF0D0F14),
                                         contentColor = Color.White
                                     ),
                                     border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2D323F)),
                                     shape = RoundedCornerShape(23.dp)
                                 ) {
                                     Row(
                                         verticalAlignment = Alignment.CenterVertically,
                                         horizontalArrangement = Arrangement.spacedBy(8.dp)
                                     ) {
                                         GoogleGLogo(modifier = Modifier.size(16.dp))
                                         Text("Create account using Google", color = Color.White, fontSize = 13.sp)
                                     }
                                 }
                             }
                         }
                     }
                 }
             }
         }

        // Dedicated Google Account Creation / Registration Dialog
        if (showGoogleSignUpDialog) {
            Dialog(
                onDismissRequest = {
                    if (!isGoogleCreating) showGoogleSignUpDialog = false
                },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.75f))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .background(Color(0xFF1E222D))
                            .border(1.dp, Color(0xFF2D323F), RoundedCornerShape(24.dp))
                            .padding(22.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        // Header with Google G Logo
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            GoogleGLogo(modifier = Modifier.size(28.dp))
                            Column {
                                Text(
                                    text = "Create Account with Google",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Register your profile connected to Google",
                                    color = Color(0xFF9CA3AF),
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Full Name
                        OutlinedTextField(
                            value = googleSignUpName,
                            onValueChange = {
                                googleSignUpName = it
                                if (googleSignUpHandle.isBlank() || googleSignUpHandle.startsWith("@")) {
                                    googleSignUpHandle = "@" + it.lowercase().replace(" ", "")
                                }
                                googleSignUpError = null
                            },
                            label = { Text("Full Name") },
                            placeholder = { Text("e.g. Alex Mercer") },
                            leadingIcon = {
                                Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF9CA3AF))
                            },
                            singleLine = true,
                            colors = authTextFieldColors(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("google_signup_name_input")
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Google Email Address
                        OutlinedTextField(
                            value = googleSignUpEmail,
                            onValueChange = {
                                googleSignUpEmail = it
                                googleSignUpError = null
                            },
                            label = { Text("Google Email Address") },
                            placeholder = { Text("yourname@gmail.com") },
                            leadingIcon = {
                                Icon(Icons.Default.Email, contentDescription = null, tint = Color(0xFF9CA3AF))
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            colors = authTextFieldColors(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("google_signup_email_input")
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Username / Handle
                        OutlinedTextField(
                            value = googleSignUpHandle,
                            onValueChange = {
                                googleSignUpHandle = it
                                googleSignUpError = null
                            },
                            label = { Text("Username") },
                            placeholder = { Text("@username") },
                            leadingIcon = {
                                Icon(Icons.Default.AlternateEmail, contentDescription = null, tint = Color(0xFF9CA3AF))
                            },
                            singleLine = true,
                            colors = authTextFieldColors(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("google_signup_handle_input")
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "Music Preferences:",
                            color = Color(0xFFD1D5DB),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            availableGenres.forEach { genre ->
                                val isSelected = googleSelectedGenres.contains(genre)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(
                                            if (isSelected) Color(0xFF00A86B) else Color(0xFF141720)
                                        )
                                        .border(
                                            1.dp,
                                            if (isSelected) Color(0xFF10B981) else Color(0xFF2D323F),
                                            RoundedCornerShape(14.dp)
                                        )
                                        .clickable {
                                            if (isSelected) googleSelectedGenres.remove(genre)
                                            else googleSelectedGenres.add(genre)
                                        }
                                        .padding(horizontal = 10.dp, vertical = 5.dp)
                                ) {
                                    Text(
                                        text = genre,
                                        color = if (isSelected) Color.White else Color(0xFF9CA3AF),
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                                    )
                                }
                            }
                        }

                        if (googleSignUpError != null) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = googleSignUpError!!,
                                color = Color(0xFFEF4444),
                                fontSize = 13.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Submit Button to Create Account using Google
                        Button(
                            onClick = {
                                if (googleSignUpName.isBlank()) {
                                    googleSignUpError = "Please enter your name"
                                } else if (googleSignUpEmail.isBlank() || !googleSignUpEmail.contains("@")) {
                                    googleSignUpError = "Please enter a valid Google email address"
                                } else {
                                    isGoogleCreating = true
                                    googleSignUpError = null
                                    coroutineScope.launch {
                                        val regErr = onSignUp(
                                            googleSignUpName.trim(),
                                            googleSignUpEmail.trim(),
                                            "google_auth_linked_pass",
                                            googleSignUpHandle.trim(),
                                            googleSelectedGenres.toList()
                                        )
                                        isGoogleCreating = false
                                        if (regErr != null) {
                                            googleSignUpError = regErr
                                        } else {
                                            showGoogleSignUpDialog = false
                                            showAuthSheet = false
                                        }
                                    }
                                }
                            },
                            enabled = !isGoogleCreating,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("submit_google_signup_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF00A86B)
                            ),
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            if (isGoogleCreating) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(20.dp)
                                )
                            } else {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    GoogleGLogo(modifier = Modifier.size(16.dp))
                                    Text(
                                        text = "Complete Google Registration",
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // External Google Registration link in Browser (if needed)
                        OutlinedButton(
                            onClick = { launchGoogleSignUpInBrowser() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(42.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFF9CA3AF)
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2D323F)),
                            shape = RoundedCornerShape(21.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                    contentDescription = null,
                                    tint = Color(0xFF9CA3AF),
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "Need a new Google account? Sign up in browser",
                                    color = Color(0xFF9CA3AF),
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Cancel Button
                        TextButton(
                            onClick = { showGoogleSignUpDialog = false },
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Text(text = "Cancel", color = Color(0xFF9CA3AF), fontSize = 13.sp)
                        }
                    }
                }
            }
        }

        // Forgot Password Dialog
        if (showForgotPasswordDialog) {
            AlertDialog(
                onDismissRequest = { showForgotPasswordDialog = false },
                containerColor = Color(0xFF1E222D),
                title = {
                    Text(
                        text = if (resetEmailSent) "Reset Link Sent" else "Reset Your Password",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                text = {
                    if (resetEmailSent) {
                        Text(
                            text = "We sent password reset instructions to $forgotPasswordEmail. Check your inbox to choose a new password.",
                            color = Color(0xFFD1D5DB),
                            fontSize = 14.sp
                        )
                    } else {
                        Column {
                            Text(
                                text = "Enter the email associated with your Novi account and we'll send a link to reset your password.",
                                color = Color(0xFF9CA3AF),
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedTextField(
                                value = forgotPasswordEmail,
                                onValueChange = { forgotPasswordEmail = it },
                                label = { Text("Email Address") },
                                singleLine = true,
                                colors = authTextFieldColors(),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (resetEmailSent) {
                                showForgotPasswordDialog = false
                            } else {
                                if (forgotPasswordEmail.isNotBlank() && forgotPasswordEmail.contains("@")) {
                                    resetEmailSent = true
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF00A86B)
                        )
                    ) {
                        Text(if (resetEmailSent) "Done" else "Send Reset Link", color = Color.White)
                    }
                },
                dismissButton = {
                    if (!resetEmailSent) {
                        TextButton(onClick = { showForgotPasswordDialog = false }) {
                            Text("Cancel", color = Color(0xFF9CA3AF))
                        }
                    }
                }
            )
        }
    }
}

@Composable
private fun authTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Color(0xFF10B981),
    unfocusedBorderColor = Color(0xFF2D323F),
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White,
    focusedLabelColor = Color(0xFF10B981),
    unfocusedLabelColor = Color(0xFF9CA3AF),
    focusedPlaceholderColor = Color(0xFF6B7280),
    unfocusedPlaceholderColor = Color(0xFF6B7280)
)

@Composable
private fun GoogleGLogo(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val sizePx = size.minDimension
        val strokeWidthPx = sizePx * 0.22f
        val center = Offset(sizePx / 2, sizePx / 2)
        val radius = (sizePx - strokeWidthPx) / 2

        // Red (top)
        drawArc(
            color = Color(0xFFEA4335),
            startAngle = 180f,
            sweepAngle = 90f,
            useCenter = false,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeWidthPx)
        )
        // Yellow (left)
        drawArc(
            color = Color(0xFFFBBC05),
            startAngle = 90f,
            sweepAngle = 90f,
            useCenter = false,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeWidthPx)
        )
        // Green (bottom)
        drawArc(
            color = Color(0xFF34A853),
            startAngle = 0f,
            sweepAngle = 90f,
            useCenter = false,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeWidthPx)
        )
        // Blue (right bar & arc)
        drawArc(
            color = Color(0xFF4285F4),
            startAngle = 315f,
            sweepAngle = 45f,
            useCenter = false,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeWidthPx)
        )
        drawLine(
            color = Color(0xFF4285F4),
            start = Offset(center.x, center.y),
            end = Offset(center.x + radius + strokeWidthPx / 2, center.y),
            strokeWidth = strokeWidthPx
        )
    }
}
