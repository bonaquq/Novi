package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AudioAppSettings
import com.example.model.DetailedEqualizerDefaults
import com.example.model.EqualizerBand

/**
 * Detailed Studio Equalizer View
 * Faithfully matches the user's provided reference screenshot:
 * - Top Header: Back navigation arrow with centered bold "Equalizer" title
 * - 6-Band Frequency Graph: (60Hz, 150Hz, 400Hz, 1KHz, 2.4KHz, 15KHz)
 * - White circular nodes with bright neon green connection line & vertical gradient fill
 * - Subtle vertical frequency guidelines
 * - Master Equalizer toggle row with iOS/Material-style green switch
 * - Full vertical preset list with active checkmarks (Acoustic, Bass Booster, Bass Reducer ✓, Classical...)
 * ONLY visible when Developer Mode is enabled.
 */
@Composable
fun DetailedEqualizerView(
    settings: AudioAppSettings,
    onSettingsChange: (AudioAppSettings) -> Unit,
    onBack: (() -> Unit)? = null,
    isDarkMode: Boolean = true,
    modifier: Modifier = Modifier
) {
    val bands = if (settings.detailedBands.size == 6) {
        settings.detailedBands
    } else {
        DetailedEqualizerDefaults.createInitialDetailedBands()
    }

    val activePresetName = settings.detailedSelectedPreset

    val neonGreen = Color(0xFF10B981) // Vibrant emerald/neon green from user reference
    val primaryText = if (isDarkMode) Color.White else Color(0xFF111827)
    val secondaryText = if (isDarkMode) Color(0xFF9CA3AF) else Color(0xFF6B7280)
    val dividerColor = if (isDarkMode) Color(0x1FFFFFFF) else Color(0x1F000000)
    val cardBackground = if (isDarkMode) Color(0xCC12151E) else Color(0xF5FFFFFF)
    val borderColor = if (isDarkMode) Color(0x33FFFFFF) else Color(0x1F000000)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(cardBackground)
            .border(1.dp, borderColor, RoundedCornerShape(20.dp))
            .padding(vertical = 14.dp)
            .testTag("detailed_studio_equalizer")
    ) {
        // 1. Top Header: < Arrow and Centered "Equalizer"
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (onBack != null) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("detailed_equalizer_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = primaryText,
                        modifier = Modifier.size(20.dp)
                    )
                }
            } else {
                Spacer(modifier = Modifier.width(4.dp))
            }

            Text(
                text = "Equalizer",
                color = primaryText,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = if (onBack != null) 36.dp else 0.dp)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 2. Interactive 6-Band Frequency Graph
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(180.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(if (isDarkMode) Color(0xFF090B0F) else Color(0xFFF3F4F6))
                .pointerInput(bands) {
                    detectDragGestures { change, _ ->
                        change.consume()
                        val width = size.width
                        val height = size.height
                        val touchX = change.position.x
                        val touchY = change.position.y

                        val paddingHoriz = width * 0.08f
                        val graphW = width - (paddingHoriz * 2)
                        val stepX = graphW / (bands.size - 1)

                        val closestIdx = (((touchX - paddingHoriz) / stepX) + 0.5f)
                            .toInt()
                            .coerceIn(0, bands.lastIndex)

                        // Gain range: -12dB at bottom, +12dB at top
                        val topMargin = height * 0.15f
                        val bottomMargin = height * 0.82f
                        val effectiveH = bottomMargin - topMargin

                        val normalized = (1f - ((touchY - topMargin) / effectiveH)).coerceIn(0f, 1f)
                        val newGain = (normalized * 24f - 12f)
                        val roundedGain = (newGain * 2).toInt() / 2f

                        val updated = bands.mapIndexed { idx, b ->
                            if (idx == closestIdx) b.copy(gainDb = roundedGain) else b
                        }

                        onSettingsChange(
                            settings.copy(
                                detailedBands = updated,
                                detailedSelectedPreset = "Custom"
                            )
                        )
                    }
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                val paddingHoriz = w * 0.08f
                val graphW = w - (paddingHoriz * 2)
                val stepX = graphW / (bands.size - 1)

                val topMargin = h * 0.15f
                val bottomMargin = h * 0.82f
                val effectiveH = bottomMargin - topMargin

                // 1. Draw subtle vertical gridlines for each of the 6 frequencies
                bands.indices.forEach { index ->
                    val x = paddingHoriz + index * stepX
                    drawLine(
                        color = if (isDarkMode) Color(0x1AFFFFFF) else Color(0x1F000000),
                        start = Offset(x, 10f),
                        end = Offset(x, bottomMargin),
                        strokeWidth = 1.dp.toPx()
                    )
                }

                // 2. Compute points (x, y) for each of the 6 bands
                val points = bands.mapIndexed { index, band ->
                    val x = paddingHoriz + index * stepX
                    val normalized = (band.gainDb.coerceIn(-12f, 12f) + 12f) / 24f
                    val y = bottomMargin - (normalized * effectiveH)
                    Offset(x, y)
                }

                // 3. Draw gradient fill below the curve
                val fillPath = Path().apply {
                    moveTo(points.first().x, points.first().y)
                    for (i in 0 until points.size - 1) {
                        val p0 = points[i]
                        val p1 = points[i + 1]
                        val cx1 = p0.x + (p1.x - p0.x) / 2f
                        val cy1 = p0.y
                        val cx2 = p0.x + (p1.x - p0.x) / 2f
                        val cy2 = p1.y
                        cubicTo(cx1, cy1, cx2, cy2, p1.x, p1.y)
                    }
                    lineTo(points.last().x, bottomMargin)
                    lineTo(points.first().x, bottomMargin)
                    close()
                }

                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            neonGreen.copy(alpha = 0.40f),
                            neonGreen.copy(alpha = 0.04f),
                            Color.Transparent
                        ),
                        startY = topMargin,
                        endY = bottomMargin
                    )
                )

                // 4. Draw bright neon-green connection line
                val linePath = Path().apply {
                    moveTo(points.first().x, points.first().y)
                    for (i in 0 until points.size - 1) {
                        val p0 = points[i]
                        val p1 = points[i + 1]
                        val cx1 = p0.x + (p1.x - p0.x) / 2f
                        val cy1 = p0.y
                        val cx2 = p0.x + (p1.x - p0.x) / 2f
                        val cy2 = p1.y
                        cubicTo(cx1, cy1, cx2, cy2, p1.x, p1.y)
                    }
                }

                // Outer line glow
                drawPath(
                    path = linePath,
                    color = neonGreen.copy(alpha = 0.35f),
                    style = Stroke(
                        width = 6.dp.toPx(),
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )

                // Main sharp line
                drawPath(
                    path = linePath,
                    color = neonGreen,
                    style = Stroke(
                        width = 3.dp.toPx(),
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )

                // 5. Draw prominent solid white circular nodes at each coordinate
                points.forEach { pt ->
                    // Outer glow around node
                    drawCircle(
                        color = neonGreen.copy(alpha = 0.25f),
                        radius = 8.5.dp.toPx(),
                        center = pt
                    )
                    // Solid white node circle as in user reference
                    drawCircle(
                        color = Color.White,
                        radius = 5.5.dp.toPx(),
                        center = pt
                    )
                }
            }
        }

        // Frequency Labels Row (60Hz, 150Hz, 400Hz, 1KHz, 2.4KHz, 15KHz)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(top = 8.dp, bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            bands.forEach { band ->
                Text(
                    text = band.frequencyLabel,
                    color = secondaryText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        var isPresetsBoxExpanded by remember { mutableStateOf(false) }
        var presetSearchQuery by remember { mutableStateOf("") }

        // Master Equalizer Switch Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Equalizer",
                color = primaryText,
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold
            )

            Switch(
                checked = settings.equalizerEnabled,
                onCheckedChange = { isEnabled ->
                    onSettingsChange(settings.copy(equalizerEnabled = isEnabled))
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = neonGreen,
                    uncheckedThumbColor = if (isDarkMode) Color(0xFF9CA3AF) else Color(0xFF6B7280),
                    uncheckedTrackColor = if (isDarkMode) Color(0xFF2D323F) else Color(0xFFE5E7EB)
                ),
                modifier = Modifier.testTag("detailed_equalizer_switch")
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 4. Dedicated Box which when pressed shows all the equalizer presets
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    if (isPresetsBoxExpanded) {
                        if (isDarkMode) Color(0xFF0F1219) else Color(0xFFF3F4F6)
                    } else {
                        if (isDarkMode) Color(0xFF161A24) else Color(0xFFF9FAFB)
                    }
                )
                .border(
                    width = if (isPresetsBoxExpanded) 1.5.dp else 1.dp,
                    color = if (isPresetsBoxExpanded) neonGreen.copy(alpha = 0.6f) else borderColor,
                    shape = RoundedCornerShape(16.dp)
                )
                .animateContentSize()
                .testTag("dev_mode_equalizer_presets_box")
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Header of the Box (Always visible and clickable to expand/collapse)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isPresetsBoxExpanded = !isPresetsBoxExpanded }
                        .padding(horizontal = 14.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(neonGreen.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.GraphicEq,
                                contentDescription = "Presets",
                                tint = neonGreen,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "EQUALIZER PRESETS",
                                    color = secondaryText,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(neonGreen.copy(alpha = 0.18f))
                                        .padding(horizontal = 5.dp, vertical = 1.5.dp)
                                ) {
                                    Text(
                                        text = "${DetailedEqualizerDefaults.presets.size}",
                                        color = neonGreen,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(2.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = activePresetName,
                                    color = primaryText,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    text = "• Active",
                                    color = neonGreen,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            Text(
                                text = if (isPresetsBoxExpanded) "Tap header to collapse" else "Press box to view all equalizer presets",
                                color = secondaryText,
                                fontSize = 11.sp
                            )
                        }
                    }

                    // Trailing Action Pill / Arrow
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                if (isPresetsBoxExpanded) neonGreen.copy(alpha = 0.15f)
                                else if (isDarkMode) Color(0x33FFFFFF)
                                else Color(0x1A000000)
                            )
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = if (isPresetsBoxExpanded) "Close" else "Show All",
                                color = if (isPresetsBoxExpanded) neonGreen else primaryText,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Icon(
                                imageVector = if (isPresetsBoxExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = if (isPresetsBoxExpanded) "Collapse" else "Expand",
                                tint = if (isPresetsBoxExpanded) neonGreen else primaryText,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                // Expanded Section: Displays all Equalizer Presets when the box is pressed
                if (isPresetsBoxExpanded) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(0.5.dp)
                            .background(dividerColor)
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp)
                    ) {
                        // Quick Search Bar
                        OutlinedTextField(
                            value = presetSearchQuery,
                            onValueChange = { presetSearchQuery = it },
                            placeholder = {
                                Text(
                                    text = "Filter 22 presets (e.g. Bass, Rock, Vocal)...",
                                    fontSize = 13.sp,
                                    color = secondaryText
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search",
                                    tint = secondaryText,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            trailingIcon = {
                                if (presetSearchQuery.isNotEmpty()) {
                                    IconButton(
                                        onClick = { presetSearchQuery = "" },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = "Clear",
                                            tint = secondaryText,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = neonGreen,
                                unfocusedBorderColor = borderColor,
                                focusedTextColor = primaryText,
                                unfocusedTextColor = primaryText,
                                cursorColor = neonGreen
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("presets_search_input")
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Scrollable List of All Equalizer Presets
                        val filteredPresets = remember(presetSearchQuery) {
                            if (presetSearchQuery.isBlank()) {
                                DetailedEqualizerDefaults.presets
                            } else {
                                DetailedEqualizerDefaults.presets.filter {
                                    it.name.contains(presetSearchQuery, ignoreCase = true)
                                }
                            }
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 300.dp)
                                .verticalScroll(rememberScrollState())
                        ) {
                            if (filteredPresets.isEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "No preset matching \"$presetSearchQuery\"",
                                        color = secondaryText,
                                        fontSize = 13.sp
                                    )
                                }
                            } else {
                                filteredPresets.forEach { preset ->
                                    val isSelected = activePresetName.equals(preset.name, ignoreCase = true)

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(
                                                if (isSelected) neonGreen.copy(alpha = 0.12f)
                                                else Color.Transparent
                                            )
                                            .clickable {
                                                val newBands = DetailedEqualizerDefaults.frequencyLabels.mapIndexed { idx, label ->
                                                    EqualizerBand(idx, label, preset.gains.getOrElse(idx) { 0f })
                                                }
                                                onSettingsChange(
                                                    settings.copy(
                                                        detailedBands = newBands,
                                                        detailedSelectedPreset = preset.name
                                                    )
                                                )
                                            }
                                            .padding(horizontal = 12.dp, vertical = 12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(8.dp)
                                                    .clip(CircleShape)
                                                    .background(if (isSelected) neonGreen else secondaryText.copy(alpha = 0.4f))
                                            )

                                            Text(
                                                text = preset.name,
                                                color = if (isSelected) primaryText else primaryText.copy(alpha = 0.85f),
                                                fontSize = 15.sp,
                                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                                            )
                                        }

                                        if (isSelected) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(4.dp))
                                                        .background(neonGreen)
                                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                                ) {
                                                    Text(
                                                        text = "Active",
                                                        color = Color.Black,
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }

                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = "Selected",
                                                    tint = neonGreen,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }
                                    }

                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 12.dp)
                                            .height(0.5.dp)
                                            .background(dividerColor)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Quick Collapse / Close Button
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isDarkMode) Color(0x22FFFFFF) else Color(0x14000000))
                                .clickable { isPresetsBoxExpanded = false }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Done / Collapse Presets ▴",
                                color = secondaryText,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}
