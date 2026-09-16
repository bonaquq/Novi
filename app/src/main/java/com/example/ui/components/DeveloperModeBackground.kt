package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

/**
 * Developer Mode Theme Background
 * Faithfully reproduces the sleek, monochromatic, minimalist satin-ribbon dark wallpaper
 * provided in user reference: deep OLED pitch-black background with smooth, sweeping 3D
 * satin-silk silver-white fluid ribbons and an elegant secondary curved arc.
 */
@Composable
fun DeveloperModeBackground(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF000000))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // 1. Pure Pitch Black OLED Base with ultra-subtle top-right & bottom-left soft ambient glow
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF14171E),
                        Color(0xFF050608),
                        Color(0xFF000000)
                    ),
                    center = Offset(w * 0.85f, h * 0.2f),
                    radius = w * 1.2f
                )
            )

            // 2. Main 3D Fluid Satin-Silk Silver-White Ribbon
            // Top/Outer contour of main ribbon
            val outerPath = Path().apply {
                moveTo(w * 0.36f, h * 1.05f)
                cubicTo(
                    w * 0.18f, h * 0.88f,
                    w * 0.04f, h * 0.72f,
                    w * 0.05f, h * 0.58f
                )
                cubicTo(
                    w * 0.06f, h * 0.44f,
                    w * 0.28f, h * 0.32f,
                    w * 0.52f, h * 0.24f
                )
                cubicTo(
                    w * 0.72f, h * 0.17f,
                    w * 0.88f, h * 0.10f,
                    w * 1.02f, h * 0.00f
                )
                // Cap at top right
                lineTo(w * 1.02f, h * 0.07f)
                // Inner contour of main ribbon (returning back to bottom)
                cubicTo(
                    w * 0.86f, h * 0.17f,
                    w * 0.70f, h * 0.24f,
                    w * 0.48f, h * 0.33f
                )
                cubicTo(
                    w * 0.24f, h * 0.43f,
                    w * 0.13f, h * 0.58f,
                    w * 0.14f, h * 0.70f
                )
                cubicTo(
                    w * 0.16f, h * 0.82f,
                    w * 0.24f, h * 0.95f,
                    w * 0.28f, h * 1.05f
                )
                close()
            }

            // Fill main ribbon with smooth 3D metallic satin gradient
            drawPath(
                path = outerPath,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF2C2F38),
                        Color(0xFF717684),
                        Color(0xFFC7CBD4),
                        Color(0xFFFFFFFF),
                        Color(0xFF9EA3AF),
                        Color(0xFF4A4E58),
                        Color(0xFF1E2026)
                    ),
                    start = Offset(w * 0.05f, h * 0.6f),
                    end = Offset(w * 0.9f, h * 0.15f)
                ),
                style = Fill
            )

            // Outer soft ambient shadow/glow under the ribbon for deep 3D illusion
            val ribbonShadowPath = Path().apply {
                moveTo(w * 0.14f, h * 0.70f)
                cubicTo(
                    w * 0.24f, h * 0.43f,
                    w * 0.48f, h * 0.33f,
                    w * 0.70f, h * 0.24f
                )
                cubicTo(
                    w * 0.86f, h * 0.17f,
                    w * 0.98f, h * 0.10f,
                    w * 1.02f, h * 0.07f
                )
                lineTo(w * 1.02f, h * 0.16f)
                cubicTo(
                    w * 0.82f, h * 0.28f,
                    w * 0.62f, h * 0.38f,
                    w * 0.40f, h * 0.48f
                )
                cubicTo(
                    w * 0.20f, h * 0.60f,
                    w * 0.10f, h * 0.78f,
                    w * 0.22f, h * 1.05f
                )
                close()
            }

            drawPath(
                path = ribbonShadowPath,
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0x882A2D36),
                        Color(0x3314161C),
                        Color.Transparent
                    ),
                    center = Offset(w * 0.35f, h * 0.55f),
                    radius = w * 0.6f
                )
            )

            // Crisp silver-white leading edge specular highlight along outer curve
            val leadingHighlightPath = Path().apply {
                moveTo(w * 0.36f, h * 1.05f)
                cubicTo(
                    w * 0.18f, h * 0.88f,
                    w * 0.04f, h * 0.72f,
                    w * 0.05f, h * 0.58f
                )
                cubicTo(
                    w * 0.06f, h * 0.44f,
                    w * 0.28f, h * 0.32f,
                    w * 0.52f, h * 0.24f
                )
                cubicTo(
                    w * 0.72f, h * 0.17f,
                    w * 0.88f, h * 0.10f,
                    w * 1.02f, h * 0.00f
                )
            }

            // Soft glow around highlight
            drawPath(
                path = leadingHighlightPath,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0x44FFFFFF),
                        Color(0x99FFFFFF),
                        Color(0xEEFFFFFF),
                        Color(0xAAFFFFFF),
                        Color(0x44FFFFFF)
                    ),
                    start = Offset(w * 0.1f, h * 0.8f),
                    end = Offset(w * 0.95f, h * 0.05f)
                ),
                style = Stroke(width = 5.dp.toPx(), cap = StrokeCap.Round)
            )

            // Sharp core white highlight line
            drawPath(
                path = leadingHighlightPath,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0x88FFFFFF),
                        Color.White,
                        Color(0xFFF3F4F6),
                        Color(0xCCFFFFFF),
                        Color(0x66FFFFFF)
                    ),
                    start = Offset(w * 0.15f, h * 0.75f),
                    end = Offset(w * 0.98f, h * 0.02f)
                ),
                style = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round)
            )

            // 3. Secondary Elegant Thin Silver-White Curved Arc
            // Curves gracefully from the bottom right curving inwards and looping up towards top right
            val secondaryArcPath = Path().apply {
                moveTo(w * 0.68f, h * 1.05f)
                cubicTo(
                    w * 0.54f, h * 0.88f,
                    w * 0.38f, h * 0.72f,
                    w * 0.40f, h * 0.58f
                )
                cubicTo(
                    w * 0.42f, h * 0.48f,
                    w * 0.60f, h * 0.42f,
                    w * 0.78f, h * 0.38f
                )
                cubicTo(
                    w * 0.86f, h * 0.36f,
                    w * 0.94f, h * 0.32f,
                    w * 1.02f, h * 0.25f
                )
            }

            // Outer soft glow for secondary arc
            drawPath(
                path = secondaryArcPath,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0x00FFFFFF),
                        Color(0x44FFFFFF),
                        Color(0x66FFFFFF),
                        Color(0x44FFFFFF),
                        Color(0x00FFFFFF)
                    ),
                    start = Offset(w * 0.65f, h * 0.95f),
                    end = Offset(w * 0.98f, h * 0.28f)
                ),
                style = Stroke(width = 3.5.dp.toPx(), cap = StrokeCap.Round)
            )

            // Sharp core for secondary arc
            drawPath(
                path = secondaryArcPath,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0x22FFFFFF),
                        Color(0xDDFFFFFF),
                        Color.White,
                        Color(0xCCFFFFFF),
                        Color(0x11FFFFFF)
                    ),
                    start = Offset(w * 0.6f, h * 0.9f),
                    end = Offset(w * 0.95f, h * 0.3f)
                ),
                style = Stroke(width = 1.2.dp.toPx(), cap = StrokeCap.Round)
            )

            // 4. Subtle vignetting around edges to ensure UI readability
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0x44000000),
                        Color.Transparent,
                        Color.Transparent,
                        Color(0x77000000)
                    ),
                    startY = 0f,
                    endY = h
                )
            )
        }
    }
}
