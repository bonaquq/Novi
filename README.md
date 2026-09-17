# Novi Music Player

A modern Material Design 3 offline & online music player application built for Android using Jetpack Compose, Media3 ExoPlayer, Room, and WorkManager.

---

## Features

- **Local Music Library & Scanner**: Automatically queries `MediaStore.Audio.Media.EXTERNAL_CONTENT_URI` to discover audio files across device storage, extracts embedded metadata (title, artist, album, duration, artwork), and indexes them with a Room database (`com.example.data.local`).
- **Real Audio Playback Engine**: Powered by Media3 ExoPlayer (`PlaybackManager`), supporting queue management, seek, shuffle, repeat, background playback via `MediaSessionService`, and real-time 5-band equalizer with bass boost.
- **In-App Music Downloader**: Enqueues and downloads remote audio tracks straight to `Music/Novi/` via Android `DownloadManager` with automatic `MediaScannerConnection` media indexing, plus a fallback `WorkManager` + OkHttp `DownloadWorker` for custom progress tracking.
- **Procedural Synthesizer Demo**: Real-time mathematical audio wave generation with dynamic frequency, BPM controls, and equalizer visualizer bars.
- **Unified Navigation & Floating MiniPlayer**: Bottom navigation between Library, Now Playing, Downloads, and Synth, with an interactive floating MiniPlayer bar.

---

## Dependencies

The project leverages the following Android libraries:

```kotlin
// Navigation & Lifecycle
implementation(libs.androidx.navigation.compose)
implementation(libs.androidx.lifecycle.viewmodel.compose)
implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

// Media3 ExoPlayer & Audio
implementation("androidx.media3:media3-exoplayer:1.4.0")
implementation("androidx.media3:media3-session:1.4.0")
implementation("androidx.media3:media3-ui:1.4.0")

// Image Loading
implementation("io.coil-kt:coil-compose:2.7.0")

// Room Local Persistence
implementation(libs.androidx.room.runtime)
implementation(libs.androidx.room.ktx)
ksp(libs.androidx.room.compiler)

// Background Work & Network
implementation("androidx.work:work-runtime-ktx:2.9.1")
implementation("androidx.hilt:hilt-work:1.2.0")
implementation(libs.okhttp)
implementation(libs.retrofit)
```

---

## Required Permissions

Declared in `AndroidManifest.xml`:

- `android.permission.INTERNET`: For streaming, online music search, and in-app music downloading.
- `android.permission.READ_MEDIA_AUDIO` (Android 13+ / API 33+): For scanning and reading local audio files.
- `android.permission.READ_EXTERNAL_STORAGE` (API <= 32): Legacy read storage permission for audio files.
- `android.permission.POST_NOTIFICATIONS`: For download progress and MediaSession playback notifications.
- `android.permission.FOREGROUND_SERVICE` & `FOREGROUND_SERVICE_MEDIA_PLAYBACK`: For continuous audio playback when backgrounded.

---

## How to Test

### 1. Pushing Local Music to the Device / Emulator
To populate your local library with sample audio tracks, connect your device or emulator via ADB and push audio files directly into the standard Music directory:

```bash
# Push an MP3 file to the Music directory
adb push song.mp3 /sdcard/Music/

# Optional: Push into the Novi downloads folder
adb push sample.mp3 /sdcard/Music/Novi/

# Trigger media scanner via broadcast (optional, or use "Scan Again" in Novi)
adb shell am broadcast -a android.intent.action.MEDIA_SCANNER_SCAN_FILE -d file:///sdcard/Music/song.mp3
```

### 2. Granting Permissions
When opening the **Library** tab for the first time, tap **Grant Music Permission** to authorize reading media audio files. Tapping **Scan Again** triggers an immediate re-index of the device's local audio files.

### 3. Testing Downloads
1. Navigate to the **Downloads** tab via the bottom navigation bar.
2. Tap **Download from URL** or the `+` icon in the top app bar.
3. Enter a direct audio file URL (e.g. `https://example.com/audio.mp3`) and tap **Download**.
4. The file will be downloaded to `/sdcard/Music/Novi/`, indexed via `MediaScannerConnection`, and will appear in your **Library** automatically.
