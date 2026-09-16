package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AudioAppSettings
import com.example.model.EqualizerBand
import com.example.model.EqualizerPreset

@Composable
fun EqualizerView(
    settings: AudioAppSettings,
    onSettingsChange: (AudioAppSettings) -> Unit,
    isDarkMode: Boolean,
    modifier: Modifier = Modifier
) {
    // ONLY show detailed studio curve equalizer if developer mode is enabled
    if (settings.developerMode) {
        DetailedEqualizerView(
            settings = settings,
            onSettingsChange = onSettingsChange,
            isDarkMode = isDarkMode,
            modifier = modifier
        )
        return
    }

    val cardBg = if (isDarkMode) Color(0xFF161922) else Color.White
    val borderColor = if (isDarkMode) Color(0xFF2D323F) else Color(0xFFE5E7EB)
    val textPrimary = if (isDarkMode) Color.White else Color(0xFF111827)
    val textSecondary = if (isDarkMode) Color(0xFF9CA3AF) else Color(0xFF6B7280)
    val accentEmerald = if (isDarkMode) Color(0xFF10B981) else Color(0xFF00A86B)
    val graphGridColor = if (isDarkMode) Color(0x334B5563) else Color(0x229CA3AF)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, borderColor, RoundedCornerShape(20.dp))
            .background(cardBg)
            .padding(18.dp)
    ) {
        // Equalizer Header with Toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(accentEmerald.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.GraphicEq,
                        contentDescription = "Equalizer",
                        tint = accentEmerald,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column {
                    Text(
                        text = "Interactive Equalizer",
                        color = textPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (settings.equalizerEnabled) "${settings.selectedPreset.displayName} • Active" else "Bypassed",
                        color = if (settings.equalizerEnabled) accentEmerald else textSecondary,
                        fontSize = 12.sp
                    )
                }
            }

            Switch(
                checked = settings.equalizerEnabled,
                onCheckedChange = { onSettingsChange(settings.copy(equalizerEnabled = it)) },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = accentEmerald,
                    uncheckedThumbColor = if (isDarkMode) Color(0xFF9CA3AF) else Color(0xFF6B7280),
                    uncheckedTrackColor = if (isDarkMode) Color(0xFF2D323F) else Color(0xFFE5E7EB)
                ),
                modifier = Modifier.testTag("equalizer_master_switch")
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Preset Selector Chips (Acoustic, Jazz, Flat, Bass Boost, Rock, Electronic, Vocal, Pop, Custom)
        Text(
            text = "PRESETS",
            color = textSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 2.dp)
        ) {
            items(EqualizerPreset.values()) { preset ->
                val isSelected = settings.selectedPreset == preset
                val chipBg = if (isSelected) accentEmerald else if (isDarkMode) Color(0xFF1E222D) else Color(0xFFF3F4F6)
                val chipText = if (isSelected) Color.White else textPrimary

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(chipBg)
                        .clickable {
                            val updatedBands = settings.bands.mapIndexed { idx, band ->
                                band.copy(gainDb = preset.gains.getOrElse(idx) { 0f })
                            }
                            onSettingsChange(
                                settings.copy(
                                    selectedPreset = preset,
                                    bands = updatedBands
                                )
                            )
                        }
                        .padding(horizontal = 14.dp, vertical = 7.dp)
                        .testTag("equalizer_preset_${preset.name.lowercase()}")
                ) {
                    Text(
                        text = preset.displayName,
                        color = chipText,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Interactive Visual Response Curve Graph (Canvas)
        Text(
            text = "FREQUENCY RESPONSE CURVE",
            color = textSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(if (isDarkMode) Color(0xFF0F1117) else Color(0xFFF8FAFC))
                .border(1.dp, borderColor, RoundedCornerShape(14.dp))
                .pointerInput(settings.bands) {
                    detectDragGestures { change, _ ->
                        change.consume()
                        val width = size.width
                        val height = size.height
                        val touchX = change.position.x
                        val touchY = change.position.y

                        // Find closest band by X coordinate
                        val bandStep = width / (settings.bands.size - 1).coerceAtLeast(1)
                        val closestIndex = ((touchX / bandStep) + 0.5f).toInt().coerceIn(0, settings.bands.lastIndex)

                        // Convert Y coordinate to dB gain (-12dB at bottom, +12dB at top)
                        val gainDb = (((height - touchY) / height) * 24f - 12f).coerceIn(-12f, 12f)

                        val updatedBands = settings.bands.mapIndexed { idx, band ->
                            if (idx == closestIndex) band.copy(gainDb = (gainDb * 2).toInt() / 2f) else band
                        }
                        onSettingsChange(
                            settings.copy(
                                selectedPreset = EqualizerPreset.CUSTOM,
                                bands = updatedBands
                            )
                        )
                    }
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height

                // Draw Grid Lines (+12dB, +6dB, 0dB, -6dB, -12dB)
                val gridLevels = listOf(0.08f, 0.29f, 0.5f, 0.71f, 0.92f)
                gridLevels.forEach { frac ->
                    val y = height * frac
                    drawLine(
                        color = if (frac == 0.5f) graphGridColor.copy(alpha = 0.5f) else graphGridColor,
                        start = Offset(0f, y),
                        end = Offset(width, y),
                        strokeWidth = if (frac == 0.5f) 1.5f else 1f
                    )
                }

                // Compute points for each band
                val points = settings.bands.mapIndexed { index, band ->
                    val x = if (settings.bands.size > 1) index * (width / (settings.bands.size - 1)) else width / 2f
                    // -12dB -> height * 0.92, +12dB -> height * 0.08, 0dB -> height * 0.5
                    val normalizedGain = (band.gainDb.coerceIn(-12f, 12f) + 12f) / 24f
                    val y = height * (0.92f - normalizedGain * 0.84f)
                    Offset(x, y)
                }

                // Smooth cubic Bezier spline curve
                val path = Path()
                val fillPath = Path()

                if (points.isNotEmpty()) {
                    path.moveTo(points.first().x, points.first().y)
                    fillPath.moveTo(points.first().x, height)
                    fillPath.lineTo(points.first().x, points.first().y)

                    for (i in 0 until points.size - 1) {
                        val p0 = points[i]
                        val p1 = points[i + 1]
                        val cx1 = p0.x + (p1.x - p0.x) / 2f
                        val cy1 = p0.y
                        val cx2 = p0.x + (p1.x - p0.x) / 2f
                        val cy2 = p1.y

                        path.cubicTo(cx1, cy1, cx2, cy2, p1.x, p1.y)
                        fillPath.cubicTo(cx1, cy1, cx2, cy2, p1.x, p1.y)
                    }

                    fillPath.lineTo(points.last().x, height)
                    fillPath.close()

                    // Draw gradient fill under the curve
                    drawPath(
                        path = fillPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                accentEmerald.copy(alpha = 0.35f),
                                accentEmerald.copy(alpha = 0.02f)
                            ),
                            startY = 0f,
                            endY = height
                        )
                    )

                    // Draw bright response stroke line
                    drawPath(
                        path = path,
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF10B981),
                                Color(0xFF06B6D4),
                                Color(0xFF3B82F6)
                            )
                        ),
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                    )

                    // Draw glowing node circles for each band
                    points.forEachIndexed { idx, pt ->
                        val band = settings.bands[idx]
                        drawCircle(
                            color = accentEmerald.copy(alpha = 0.3f),
                            radius = 9.dp.toPx(),
                            center = pt
                        )
                        drawCircle(
                            color = Color.White,
                            radius = 5.dp.toPx(),
                            center = pt
                        )
                        drawCircle(
                            color = accentEmerald,
                            radius = 3.5.dp.toPx(),
                            center = pt
                        )
                    }
                }
            }

            // Overlay dB indicators on left
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 8.dp, top = 6.dp, bottom = 6.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text("+12 dB", color = textSecondary.copy(alpha = 0.6f), fontSize = 9.sp, fontWeight = FontWeight.SemiBold)
                Text("  0 dB", color = textSecondary.copy(alpha = 0.6f), fontSize = 9.sp, fontWeight = FontWeight.SemiBold)
                Text("-12 dB", color = textSecondary.copy(alpha = 0.6f), fontSize = 9.sp, fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Individual Manual Band Sliders (60Hz, 230Hz, 910Hz, 3.6kHz, 14kHz)
        Text(
            text = "MANUAL BAND ADJUSTMENTS",
            color = textSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        settings.bands.forEachIndexed { index, band ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = band.frequencyLabel,
                    color = textPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.width(58.dp)
                )

                Slider(
                    value = band.gainDb,
                    onValueChange = { newGain ->
                        val updatedBands = settings.bands.mapIndexed { idx, b ->
                            if (idx == index) b.copy(gainDb = (newGain * 2).toInt() / 2f) else b
                        }
                        onSettingsChange(
                            settings.copy(
                                selectedPreset = EqualizerPreset.CUSTOM,
                                bands = updatedBands
                            )
                        )
                    },
                    valueRange = -12f..12f,
                    colors = SliderDefaults.colors(
                        thumbColor = accentEmerald,
                        activeTrackColor = accentEmerald,
                        inactiveTrackColor = if (isDarkMode) Color(0xFF2D323F) else Color(0xFFE5E7EB)
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("equalizer_slider_${band.id}")
                )

                Text(
                    text = if (band.gainDb > 0) "+${String.format("%.1f", band.gainDb)} dB" else "${String.format("%.1f", band.gainDb)} dB",
                    color = if (band.gainDb != 0f) accentEmerald else textSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.width(62.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Bass Boost and Virtualizer Sliders
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Bass Boost
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (isDarkMode) Color(0xFF1E222D) else Color(0xFFF3F4F6))
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Bass Boost", color = textPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("${(settings.bassBoostAmount * 100).toInt()}%", color = accentEmerald, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Slider(
                    value = settings.bassBoostAmount,
                    onValueChange = { onSettingsChange(settings.copy(bassBoostAmount = it)) },
                    valueRange = 0f..1f,
                    colors = SliderDefaults.colors(
                        thumbColor = accentEmerald,
                        activeTrackColor = accentEmerald,
                        inactiveTrackColor = if (isDarkMode) Color(0xFF2D323F) else Color(0xFFD1D5DB)
                    ),
                    modifier = Modifier.testTag("bass_boost_slider")
                )
            }

            // Virtualizer / Spatializer
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (isDarkMode) Color(0xFF1E222D) else Color(0xFFF3F4F6))
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("3D Spatializer", color = textPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("${(settings.virtualizerAmount * 100).toInt()}%", color = accentEmerald, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Slider(
                    value = settings.virtualizerAmount,
                    onValueChange = { onSettingsChange(settings.copy(virtualizerAmount = it)) },
                    valueRange = 0f..1f,
                    colors = SliderDefaults.colors(
                        thumbColor = accentEmerald,
                        activeTrackColor = accentEmerald,
                        inactiveTrackColor = if (isDarkMode) Color(0xFF2D323F) else Color(0xFFD1D5DB)
                    ),
                    modifier = Modifier.testTag("virtualizer_slider")
                )
            }
        }
    }
}
