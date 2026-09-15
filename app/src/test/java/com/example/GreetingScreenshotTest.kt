package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.assertIsDisplayed
import com.example.ui.screens.ProfileScreen
import com.example.ui.theme.MyApplicationTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun profile_screen_light_mode() {
    composeTestRule.setContent {
      MyApplicationTheme(darkTheme = false) {
        ProfileScreen(isDarkMode = false, onToggleDarkMode = {})
      }
    }
    composeTestRule.onNodeWithTag("dark_mode_toggle").assertIsDisplayed()
  }

  @Test
  fun profile_screen_dark_mode() {
    composeTestRule.setContent {
      MyApplicationTheme(darkTheme = true) {
        ProfileScreen(isDarkMode = true, onToggleDarkMode = {})
      }
    }
    composeTestRule.onNodeWithTag("dark_mode_toggle").assertIsDisplayed()
  }
}
