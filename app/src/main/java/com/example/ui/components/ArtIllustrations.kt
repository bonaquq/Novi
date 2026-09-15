package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ArtworkType

/**
 * High-fidelity vector illustrations matching the aesthetic from the reference screenshots.
 */
@Composable
fun TrackArtworkDisplay(
    artworkType: ArtworkType,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        when (artworkType) {
            ArtworkType.SONIC_YOUTH -> SonicYouthArtwork(Modifier.fillMaxSize())
            ArtworkType.DEPECHE_MODE -> DepecheModeArtwork(Modifier.fillMaxSize())
            ArtworkType.WOODZ -> WoodzArtwork(Modifier.fillMaxSize())
            ArtworkType.APHEX_TWIN -> AphexTwinArtwork(Modifier.fillMaxSize())
            ArtworkType.NELLY_MES -> NellyMesArtwork(Modifier.fillMaxSize())
            ArtworkType.BOARDS_OF_CANADA -> BoardsOfCanadaArtwork(Modifier.fillMaxSize())
            ArtworkType.GORILLAZ -> GorillazArtwork(Modifier.fillMaxSize())
        }
    }
}

/**
 * Sonic Youth "Goo" style comic art (Screenshots 2 & 3).
 * Distinctive black & white comic ink style with characters in dark sunglasses and text.
 */
@Composable
fun SonicYouthArtwork(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.background(Color(0xFFFAF9F6))) {
        val w = size.width
        val h = size.height

        // Outer crisp border
        drawRect(
            color = Color(0xFF181A20),
            style = Stroke(width = 2.dp.toPx())
        )

        // Top "SONIC YOUTH" banner area
        drawRect(
            color = Color(0xFF181A20),
            topLeft = Offset(0f, 0f),
            size = Size(w, h * 0.16f)
        )

        // White stencil cut text lines in banner
        val bannerH = h * 0.16f
        drawLine(
            color = Color.White,
            start = Offset(w * 0.1f, bannerH * 0.5f),
            end = Offset(w * 0.9f, bannerH * 0.5f),
            strokeWidth = 3.dp.toPx(),
            cap = StrokeCap.Round
        )
        drawLine(
            color = Color.White,
            start = Offset(w * 0.15f, bannerH * 0.3f),
            end = Offset(w * 0.35f, bannerH * 0.3f),
            strokeWidth = 2.dp.toPx()
        )
        drawLine(
            color = Color.White,
            start = Offset(w * 0.65f, bannerH * 0.7f),
            end = Offset(w * 0.85f, bannerH * 0.7f),
            strokeWidth = 2.dp.toPx()
        )

        // Male character silhouette (left, wearing sunglasses)
        val headPath = Path().apply {
            moveTo(w * 0.20f, h * 0.32f)
            cubicTo(w * 0.25f, h * 0.20f, w * 0.48f, h * 0.20f, w * 0.52f, h * 0.35f)
            lineTo(w * 0.52f, h * 0.60f)
            lineTo(w * 0.48f, h * 0.75f)
            lineTo(w * 0.25f, h * 0.75f)
            close()
        }
        drawPath(headPath, color = Color(0xFF181A20))

        // Left character face cutout
        val faceCutout = Path().apply {
            moveTo(w * 0.30f, h * 0.38f)
            lineTo(w * 0.46f, h * 0.42f)
            lineTo(w * 0.44f, h * 0.68f)
            lineTo(w * 0.34f, h * 0.68f)
            close()
        }
        drawPath(faceCutout, color = Color.White)

        // Dark sunglasses on left character
        drawRoundRect(
            color = Color(0xFF181A20),
            topLeft = Offset(w * 0.31f, h * 0.43f),
            size = Size(w * 0.16f, h * 0.08f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx())
        )

        // Right character silhouette (wearing sunglasses & smoking cigarette)
        val femaleHead = Path().apply {
            moveTo(w * 0.55f, h * 0.30f)
            cubicTo(w * 0.65f, h * 0.18f, w * 0.88f, h * 0.20f, w * 0.90f, h * 0.38f)
            lineTo(w * 0.90f, h * 0.75f)
            lineTo(w * 0.60f, h * 0.75f)
            close()
        }
        drawPath(femaleHead, color = Color(0xFF181A20))

        // Right character face cutout
        val faceCutout2 = Path().apply {
            moveTo(w * 0.62f, h * 0.36f)
            lineTo(w * 0.80f, h * 0.38f)
            lineTo(w * 0.76f, h * 0.66f)
            lineTo(w * 0.65f, h * 0.66f)
            close()
        }
        drawPath(faceCutout2, color = Color.White)

        // Sunglasses on right character
        drawRoundRect(
            color = Color(0xFF181A20),
            topLeft = Offset(w * 0.64f, h * 0.41f),
            size = Size(w * 0.15f, h * 0.075f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx())
        )

        // White cigarette line with smoke curl
        drawLine(
            color = Color.White,
            start = Offset(w * 0.62f, h * 0.58f),
            end = Offset(w * 0.50f, h * 0.55f),
            strokeWidth = 2.dp.toPx()
        )
        // Smoke wisps
        drawCircle(
            color = Color(0xFF181A20),
            radius = 2.dp.toPx(),
            center = Offset(w * 0.48f, h * 0.50f)
        )

        // Bottom comic text box with handwriting style
        drawRect(
            color = Color.White,
            topLeft = Offset(w * 0.1f, h * 0.78f),
            size = Size(w * 0.8f, h * 0.18f)
        )
        drawRect(
            color = Color(0xFF181A20),
            topLeft = Offset(w * 0.1f, h * 0.78f),
            size = Size(w * 0.8f, h * 0.18f),
            style = Stroke(width = 1.5.dp.toPx())
        )
        // Text lines in box
        drawLine(
            color = Color(0xFF181A20),
            start = Offset(w * 0.15f, h * 0.83f),
            end = Offset(w * 0.82f, h * 0.83f),
            strokeWidth = 1.5.dp.toPx()
        )
        drawLine(
            color = Color(0xFF181A20),
            start = Offset(w * 0.15f, h * 0.89f),
            end = Offset(w * 0.72f, h * 0.89f),
            strokeWidth = 1.5.dp.toPx()
        )
    }
}

/**
 * Depeche Mode "Review" album art: Intersecting colorful rays (Screenshot 2).
 */
@Composable
fun DepecheModeArtwork(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.background(Color(0xFFF3F4F6))) {
        val w = size.width
        val h = size.height

        val center = Offset(w * 0.5f, h * 0.5f)
        val colors = listOf(
            Color(0xFFEF4444), // Red
            Color(0xFF3B82F6), // Blue
            Color(0xFF10B981), // Emerald
            Color(0xFFF59E0B), // Amber
            Color(0xFF8B5CF6), // Purple
            Color(0xFF06B6D4)  // Cyan
        )

        // Starburst intersecting colorful linear strokes
        val angles = listOf(15f, 45f, 75f, 105f, 135f, 165f, 210f, 255f, 290f, 330f)
        angles.forEachIndexed { index, deg ->
            val rad = Math.toRadians(deg.toDouble())
            val length = w * 0.45f
            val endX = center.x + (length * Math.cos(rad)).toFloat()
            val endY = center.y + (length * Math.sin(rad)).toFloat()
            val startX = center.x - (length * 0.3f * Math.cos(rad)).toFloat()
            val startY = center.y - (length * 0.3f * Math.sin(rad)).toFloat()

            drawLine(
                color = colors[index % colors.size],
                start = Offset(startX, startY),
                end = Offset(endX, endY),
                strokeWidth = 2.5.dp.toPx(),
                cap = StrokeCap.Round
            )
        }

        // Concentric thin rings
        drawCircle(
            color = Color(0xFF111827),
            radius = w * 0.22f,
            center = center,
            style = Stroke(width = 1.5.dp.toPx())
        )
        drawCircle(
            color = Color(0xFFEF4444),
            radius = w * 0.08f,
            center = center,
            style = Fill
        )
    }
}

/**
 * Woodz "Drowning" artwork: Cyan/Blue hair portrait (Screenshot 2).
 */
@Composable
fun WoodzArtwork(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.background(Color(0xFFE0F2FE))) {
        val w = size.width
        val h = size.height

        // Vibrant blue hair profile
        val hairPath = Path().apply {
            moveTo(w * 0.25f, h * 0.20f)
            cubicTo(w * 0.65f, h * 0.10f, w * 0.85f, h * 0.30f, w * 0.80f, h * 0.65f)
            lineTo(w * 0.65f, h * 0.85f)
            lineTo(w * 0.35f, h * 0.80f)
            lineTo(w * 0.20f, h * 0.50f)
            close()
        }
        drawPath(
            hairPath,
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFF0284C7), Color(0xFF0369A1))
            )
        )

        // Face profile in pale ivory
        drawCircle(
            color = Color(0xFFFFFBEB),
            radius = w * 0.22f,
            center = Offset(w * 0.45f, h * 0.52f)
        )

        // Striking electric blue strands
        drawLine(
            color = Color(0xFF38BDF8),
            start = Offset(w * 0.32f, h * 0.25f),
            end = Offset(w * 0.22f, h * 0.70f),
            strokeWidth = 3.dp.toPx(),
            cap = StrokeCap.Round
        )
        drawLine(
            color = Color(0xFF0284C7),
            start = Offset(w * 0.50f, h * 0.20f),
            end = Offset(w * 0.40f, h * 0.45f),
            strokeWidth = 2.dp.toPx()
        )
    }
}

/**
 * Aphex Twin Live line-art illustration (singer with microphone in hand, Screenshot 2).
 */
@Composable
fun AphexTwinArtwork(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.background(Color.White)) {
        val w = size.width
        val h = size.height

        val stroke = Stroke(
            width = 1.8.dp.toPx(),
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )

        // Head and hair
        drawCircle(
            color = Color(0xFF111827),
            radius = w * 0.16f,
            center = Offset(w * 0.48f, h * 0.30f),
            style = stroke
        )

        // Curly hair details
        for (i in 0..5) {
            val angle = i * 35f + 160f
            val rad = Math.toRadians(angle.toDouble())
            val cx = w * 0.48f + (w * 0.15f * Math.cos(rad)).toFloat()
            val cy = h * 0.28f + (h * 0.15f * Math.sin(rad)).toFloat()
            drawCircle(
                color = Color(0xFF111827),
                radius = 3.dp.toPx(),
                center = Offset(cx, cy)
            )
        }

        // Arm holding microphone
        val armPath = Path().apply {
            moveTo(w * 0.65f, h * 0.55f)
            lineTo(w * 0.40f, h * 0.45f)
            lineTo(w * 0.35f, h * 0.38f)
        }
        drawPath(armPath, color = Color(0xFF111827), style = stroke)

        // Microphone capsule
        drawRoundRect(
            color = Color(0xFF111827),
            topLeft = Offset(w * 0.30f, h * 0.32f),
            size = Size(w * 0.08f, h * 0.12f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx())
        )

        // Mic cable curly line
        val cablePath = Path().apply {
            moveTo(w * 0.34f, h * 0.44f)
            cubicTo(w * 0.30f, h * 0.60f, w * 0.40f, h * 0.70f, w * 0.32f, h * 0.85f)
        }
        drawPath(cablePath, color = Color(0xFF111827), style = stroke)

        // Torso / shirt grid pattern
        val shirtPath = Path().apply {
            moveTo(w * 0.35f, h * 0.50f)
            lineTo(w * 0.65f, h * 0.50f)
            lineTo(w * 0.70f, h * 0.85f)
            lineTo(w * 0.30f, h * 0.85f)
            close()
        }
        drawPath(shirtPath, color = Color(0xFF111827), style = stroke)

        // Plaid shirt lines
        drawLine(
            color = Color(0xFF111827),
            start = Offset(w * 0.45f, h * 0.50f),
            end = Offset(w * 0.45f, h * 0.85f),
            strokeWidth = 1.dp.toPx()
        )
        drawLine(
            color = Color(0xFF111827),
            start = Offset(w * 0.55f, h * 0.50f),
            end = Offset(w * 0.55f, h * 0.85f),
            strokeWidth = 1.dp.toPx()
        )
        drawLine(
            color = Color(0xFF111827),
            start = Offset(w * 0.32f, h * 0.65f),
            end = Offset(w * 0.68f, h * 0.65f),
            strokeWidth = 1.dp.toPx()
        )
    }
}

/**
 * Nelly Mes DJ girl line-art illustration (turntables and dancing girl, Screenshot 2).
 */
@Composable
fun NellyMesArtwork(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.background(Color.White)) {
        val w = size.width
        val h = size.height

        val stroke = Stroke(
            width = 1.8.dp.toPx(),
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )

        // DJ Turntables console at bottom
        drawRect(
            color = Color(0xFF111827),
            topLeft = Offset(w * 0.15f, h * 0.70f),
            size = Size(w * 0.70f, h * 0.18f),
            style = stroke
        )
        // Left vinyl record on turntable
        drawCircle(
            color = Color(0xFF111827),
            radius = w * 0.12f,
            center = Offset(w * 0.32f, h * 0.79f),
            style = stroke
        )
        drawCircle(
            color = Color(0xFF111827),
            radius = w * 0.04f,
            center = Offset(w * 0.32f, h * 0.79f)
        )
        // Right vinyl record on turntable
        drawCircle(
            color = Color(0xFF111827),
            radius = w * 0.12f,
            center = Offset(w * 0.68f, h * 0.79f),
            style = stroke
        )
        drawCircle(
            color = Color(0xFF111827),
            radius = w * 0.04f,
            center = Offset(w * 0.68f, h * 0.79f)
        )

        // Dancing female figure with arm in air
        // Head & hair ponytail
        drawCircle(
            color = Color(0xFF111827),
            radius = w * 0.12f,
            center = Offset(w * 0.50f, h * 0.34f),
            style = stroke
        )
        // Ponytail waving to the left
        val ponytail = Path().apply {
            moveTo(w * 0.42f, h * 0.30f)
            cubicTo(w * 0.25f, h * 0.25f, w * 0.22f, h * 0.40f, w * 0.15f, h * 0.42f)
        }
        drawPath(ponytail, color = Color(0xFF111827), style = stroke)

        // Headphones over ears
        val headphones = Path().apply {
            moveTo(w * 0.38f, h * 0.34f)
            cubicTo(w * 0.40f, h * 0.20f, w * 0.60f, h * 0.20f, w * 0.62f, h * 0.34f)
        }
        drawPath(headphones, color = Color(0xFF111827), style = stroke)
        drawCircle(
            color = Color(0xFF111827),
            radius = 3.dp.toPx(),
            center = Offset(w * 0.38f, h * 0.34f)
        )
        drawCircle(
            color = Color(0xFF111827),
            radius = 3.dp.toPx(),
            center = Offset(w * 0.62f, h * 0.34f)
        )

        // Raised hand dancing
        val raisedArm = Path().apply {
            moveTo(w * 0.55f, h * 0.45f)
            lineTo(w * 0.72f, h * 0.30f)
            lineTo(w * 0.78f, h * 0.18f)
        }
        drawPath(raisedArm, color = Color(0xFF111827), style = stroke)

        // Hand touching the record
        val deckArm = Path().apply {
            moveTo(w * 0.45f, h * 0.45f)
            lineTo(w * 0.35f, h * 0.60f)
            lineTo(w * 0.32f, h * 0.72f)
        }
        drawPath(deckArm, color = Color(0xFF111827), style = stroke)
    }
}

/**
 * Boards of Canada analog synth art.
 */
@Composable
fun BoardsOfCanadaArtwork(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.background(Color(0xFFFEF3C7))) {
        val w = size.width
        val h = size.height

        // Retro sun
        drawCircle(
            color = Color(0xFFF59E0B),
            radius = w * 0.28f,
            center = Offset(w * 0.5f, h * 0.42f)
        )

        // Synth horizon lines
        for (i in 0..4) {
            val y = h * (0.60f + i * 0.08f)
            drawLine(
                color = Color(0xFFB45309),
                start = Offset(0f, y),
                end = Offset(w, y),
                strokeWidth = (1.5f + i * 0.8f).dp.toPx()
            )
        }
    }
}

/**
 * Gorillaz Demon Days comic style artwork.
 */
@Composable
fun GorillazArtwork(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.background(Color(0xFF064E3B))) {
        val w = size.width
        val h = size.height

        // Electric green & dark contrast
        val facePath = Path().apply {
            moveTo(w * 0.30f, h * 0.20f)
            lineTo(w * 0.70f, h * 0.20f)
            lineTo(w * 0.65f, h * 0.75f)
            lineTo(w * 0.35f, h * 0.75f)
            close()
        }
        drawPath(facePath, color = Color(0xFF10B981))

        // Spiky hair
        val hair = Path().apply {
            moveTo(w * 0.25f, h * 0.25f)
            lineTo(w * 0.20f, h * 0.10f)
            lineTo(w * 0.35f, h * 0.18f)
            lineTo(w * 0.50f, h * 0.08f)
            lineTo(w * 0.65f, h * 0.18f)
            lineTo(w * 0.80f, h * 0.12f)
            lineTo(w * 0.75f, h * 0.25f)
            close()
        }
        drawPath(hair, color = Color(0xFF0F172A))

        // Eyes
        drawCircle(
            color = Color(0xFF0F172A),
            radius = w * 0.06f,
            center = Offset(w * 0.42f, h * 0.42f)
        )
        drawCircle(
            color = Color(0xFF0F172A),
            radius = w * 0.06f,
            center = Offset(w * 0.58f, h * 0.42f)
        )
    }
}

/**
 * User Profile Avatar with selectable styles for Edit Profile.
 */
@Composable
fun UserAvatarView(
    modifier: Modifier = Modifier,
    avatarId: Int = 1,
    borderColor: Color = Color(0xFFE5E7EB)
) {
    val bgColors = when (avatarId) {
        2 -> Color(0xFFC084FC) // Violet/Cyberpunk
        3 -> Color(0xFFFB923C) // Sunset Coral
        4 -> Color(0xFFA7F3D0) // Emerald Audiophile
        else -> Color(0xFFFDE68A) // Blonde / Classic
    }

    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(bgColors)
            .border(1.5.dp, borderColor, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            when (avatarId) {
                2 -> {
                    // Cyberpunk / Neon Violet avatar
                    drawCircle(
                        color = Color(0xFFE9D5FF),
                        radius = w * 0.35f,
                        center = Offset(w * 0.5f, h * 0.55f)
                    )
                    // Purple hair
                    val hair = Path().apply {
                        moveTo(w * 0.15f, h * 0.35f)
                        cubicTo(w * 0.25f, h * 0.08f, w * 0.75f, h * 0.08f, w * 0.85f, h * 0.35f)
                        lineTo(w * 0.90f, h * 0.80f)
                        lineTo(w * 0.75f, h * 0.75f)
                        lineTo(w * 0.70f, h * 0.40f)
                        lineTo(w * 0.30f, h * 0.40f)
                        lineTo(w * 0.25f, h * 0.75f)
                        lineTo(w * 0.10f, h * 0.80f)
                        close()
                    }
                    drawPath(hair, color = Color(0xFF7E22CE))
                    // Cyan shades
                    drawRoundRect(
                        color = Color(0xFF06B6D4),
                        topLeft = Offset(w * 0.30f, h * 0.45f),
                        size = Size(w * 0.40f, h * 0.12f),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx())
                    )
                }
                3 -> {
                    // Retro Synthwave avatar
                    drawCircle(
                        color = Color(0xFFFFEDD5),
                        radius = w * 0.35f,
                        center = Offset(w * 0.5f, h * 0.55f)
                    )
                    // Dark wavy hair
                    val hair = Path().apply {
                        moveTo(w * 0.18f, h * 0.30f)
                        cubicTo(w * 0.30f, h * 0.10f, w * 0.70f, h * 0.10f, w * 0.82f, h * 0.30f)
                        lineTo(w * 0.80f, h * 0.55f)
                        lineTo(w * 0.68f, h * 0.38f)
                        lineTo(w * 0.32f, h * 0.38f)
                        lineTo(w * 0.20f, h * 0.55f)
                        close()
                    }
                    drawPath(hair, color = Color(0xFF1E1B4B))
                    // Eyes
                    drawCircle(Color(0xFFEA580C), 2.5.dp.toPx(), Offset(w * 0.40f, h * 0.50f))
                    drawCircle(Color(0xFFEA580C), 2.5.dp.toPx(), Offset(w * 0.60f, h * 0.50f))
                    // Smile
                    drawLine(Color(0xFFEA580C), Offset(w * 0.44f, h * 0.66f), Offset(w * 0.56f, h * 0.66f), 2.dp.toPx(), StrokeCap.Round)
                }
                4 -> {
                    // Emerald Audiophile with headphones
                    drawCircle(
                        color = Color(0xFFD1FAE5),
                        radius = w * 0.35f,
                        center = Offset(w * 0.5f, h * 0.55f)
                    )
                    // Headband
                    drawArc(
                        color = Color(0xFF065F46),
                        startAngle = 180f,
                        sweepAngle = 180f,
                        useCenter = false,
                        topLeft = Offset(w * 0.20f, h * 0.18f),
                        size = Size(w * 0.60f, h * 0.60f),
                        style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                    )
                    // Ear cups
                    drawRoundRect(
                        color = Color(0xFF047857),
                        topLeft = Offset(w * 0.14f, h * 0.40f),
                        size = Size(w * 0.12f, h * 0.24f),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(3.dp.toPx())
                    )
                    drawRoundRect(
                        color = Color(0xFF047857),
                        topLeft = Offset(w * 0.74f, h * 0.40f),
                        size = Size(w * 0.12f, h * 0.24f),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(3.dp.toPx())
                    )
                    // Eyes
                    drawCircle(Color(0xFF065F46), 2.dp.toPx(), Offset(w * 0.42f, h * 0.52f))
                    drawCircle(Color(0xFF065F46), 2.dp.toPx(), Offset(w * 0.58f, h * 0.52f))
                }
                else -> {
                    // Face
                    drawCircle(
                        color = Color(0xFFFEF08A),
                        radius = w * 0.35f,
                        center = Offset(w * 0.5f, h * 0.55f)
                    )

                    // Blonde hair with bangs
                    val hairPath = Path().apply {
                        moveTo(w * 0.20f, h * 0.25f)
                        cubicTo(w * 0.30f, h * 0.10f, w * 0.70f, h * 0.10f, w * 0.80f, h * 0.25f)
                        lineTo(w * 0.85f, h * 0.75f)
                        lineTo(w * 0.72f, h * 0.75f)
                        lineTo(w * 0.70f, h * 0.40f)
                        // Bangs over forehead
                        lineTo(w * 0.30f, h * 0.40f)
                        lineTo(w * 0.28f, h * 0.75f)
                        lineTo(w * 0.15f, h * 0.75f)
                        close()
                    }
                    drawPath(hairPath, color = Color(0xFFFACC15))

                    // Eyes
                    drawCircle(
                        color = Color(0xFF1F2937),
                        radius = 2.dp.toPx(),
                        center = Offset(w * 0.42f, h * 0.50f)
                    )
                    drawCircle(
                        color = Color(0xFF1F2937),
                        radius = 2.dp.toPx(),
                        center = Offset(w * 0.58f, h * 0.50f)
                    )

                    // Soft smile
                    drawLine(
                        color = Color(0xFFDC2626),
                        start = Offset(w * 0.46f, h * 0.65f),
                        end = Offset(w * 0.54f, h * 0.65f),
                        strokeWidth = 1.5.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }
            }
        }
    }
}
