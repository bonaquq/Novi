package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ArtworkType
import com.example.ui.components.TrackArtworkDisplay

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WelcomeScreen(
    onGetStarted: () -> Unit,
    onSignIn: (email: String, name: String) -> Unit = { _, _ -> onGetStarted() },
    modifier: Modifier = Modifier
) {
    var showSignInSheet by remember { mutableStateOf(false) }
    var emailInput by remember { mutableStateOf("audrey.v@novimusic.io") }
    var passwordInput by remember { mutableStateOf("••••••••") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0D0F14))
    ) {
        // Tilted Album Art Grid in the top half
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(420.dp)
                .padding(top = 24.dp)
        ) {
            // Background subtle glow
            Box(
                modifier = Modifier
                    .size(260.dp)
                    .align(Alignment.Center)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(Color(0x3310B981), Color.Transparent)
                        )
                    )
            )

            // Card 1: Top Left tilted card (Gorillaz / Concert style)
            Box(
                modifier = Modifier
                    .size(170.dp, 210.dp)
                    .offset(x = (-20).dp, y = 20.dp)
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
                    .offset(x = 30.dp, y = (-10).dp)
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

            // Card 3: Center Left Card with • LIVE badge (as in left screenshot!)
            Box(
                modifier = Modifier
                    .size(185.dp, 230.dp)
                    .offset(x = 30.dp, y = 140.dp)
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

                // "• LIVE" badge pill in bottom-left of card (Screenshot 1)
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
                    .offset(x = 10.dp, y = 40.dp)
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
        }

        // Bottom Content Section: Heading, Description, Get started & Sign in CTAs
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 32.dp)
        ) {
            Text(
                text = "Music without\nborders",
                color = Color.White,
                fontSize = 32.sp,
                lineHeight = 38.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Create playlists, find new tracks and listen to your favorite music anytime!",
                color = Color(0xFF9CA3AF),
                fontSize = 15.sp,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Vibrant Emerald Green Button: Get started
            Button(
                onClick = onGetStarted,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("get_started_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00A86B)
                ),
                shape = RoundedCornerShape(26.dp)
            ) {
                Text(
                    text = "Get started",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Sign In / Login Button
            OutlinedButton(
                onClick = { showSignInSheet = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("sign_in_button"),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.White
                ),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF2D323F)),
                shape = RoundedCornerShape(26.dp)
            ) {
                Text(
                    text = "Sign in / Log in",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Sign In Bottom Sheet
        if (showSignInSheet) {
            ModalBottomSheet(
                onDismissRequest = { showSignInSheet = false },
                sheetState = sheetState,
                containerColor = Color(0xFF161922),
                contentColor = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 36.dp)
                ) {
                    Text(
                        text = "Sign in to Novi",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Access your custom playlists, synced lyrics and audio profile",
                        color = Color(0xFF9CA3AF),
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Email Field
                    OutlinedTextField(
                        value = emailInput,
                        onValueChange = { emailInput = it },
                        label = { Text("Email address") },
                        leadingIcon = {
                            Icon(Icons.Default.Email, contentDescription = null, tint = Color(0xFF9CA3AF))
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF10B981),
                            unfocusedBorderColor = Color(0xFF2D323F),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedLabelColor = Color(0xFF10B981),
                            unfocusedLabelColor = Color(0xFF9CA3AF)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_email_input")
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Password Field
                    OutlinedTextField(
                        value = passwordInput,
                        onValueChange = { passwordInput = it },
                        label = { Text("Password") },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF9CA3AF))
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                    tint = Color(0xFF9CA3AF)
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF10B981),
                            unfocusedBorderColor = Color(0xFF2D323F),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedLabelColor = Color(0xFF10B981),
                            unfocusedLabelColor = Color(0xFF9CA3AF)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_password_input")
                    )

                    if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = errorMessage!!,
                            color = Color(0xFFEF4444),
                            fontSize = 13.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Submit Sign In Button
                    Button(
                        onClick = {
                            if (emailInput.isBlank()) {
                                errorMessage = "Please enter your email"
                            } else {
                                val name = emailInput.substringBefore("@").replaceFirstChar { it.uppercase() }
                                onSignIn(emailInput, name)
                                showSignInSheet = false
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("login_submit_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF00A86B)
                        ),
                        shape = RoundedCornerShape(25.dp)
                    ) {
                        Text(
                            text = "Log In",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Quick Guest / Demo Login
                    OutlinedButton(
                        onClick = {
                            onSignIn("audrey.v@novimusic.io", "Audrey V.")
                            showSignInSheet = false
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("quick_demo_login_button"),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF10B981)
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981)),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Text(
                            text = "Quick Demo Sign In (Audrey V.)",
                            color = Color(0xFF10B981),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
