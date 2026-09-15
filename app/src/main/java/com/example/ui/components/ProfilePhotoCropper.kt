package com.example.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.RotateRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Crop
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import kotlin.math.max
import kotlin.math.min

/**
 * High quality interactive Profile Photo Cropper Dialog.
 * Allows zoom, pan, rotate, and circular framing with high resolution export.
 */
@Composable
fun ProfilePhotoCropperDialog(
    imageUri: Uri,
    onCropSuccess: (croppedUriString: String) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var sourceBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var isSaving by remember { mutableStateOf(false) }

    // Transformation States
    var scale by remember { mutableFloatStateOf(1f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    var rotationDegrees by remember { mutableIntStateOf(0) }

    // Load Bitmap safely from Uri
    LaunchedEffect(imageUri) {
        isLoading = true
        withContext(Dispatchers.IO) {
            try {
                val inputStream: InputStream? = context.contentResolver.openInputStream(imageUri)
                val rawBitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()

                // Limit max dimension to 2048 to prevent memory strain while keeping crisp quality
                if (rawBitmap != null) {
                    val maxDim = max(rawBitmap.width, rawBitmap.height)
                    if (maxDim > 2048) {
                        val factor = 2048f / maxDim
                        val targetW = (rawBitmap.width * factor).toInt()
                        val targetH = (rawBitmap.height * factor).toInt()
                        sourceBitmap = Bitmap.createScaledBitmap(rawBitmap, targetW, targetH, true)
                        if (sourceBitmap != rawBitmap) rawBitmap.recycle()
                    } else {
                        sourceBitmap = rawBitmap
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        isLoading = false
    }

    Dialog(
        onDismissRequest = { if (!isSaving) onDismiss() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFF0F1117)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Action Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onDismiss,
                        enabled = !isSaving
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cancel",
                            tint = Color(0xFF9CA3AF)
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Crop,
                            contentDescription = null,
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Crop Profile Picture",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    IconButton(
                        onClick = {
                            rotationDegrees = (rotationDegrees + 90) % 360
                        },
                        enabled = !isSaving
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.RotateRight,
                            contentDescription = "Rotate 90°",
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Cropping Viewport Area
                BoxWithConstraints(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF060709)),
                    contentAlignment = Alignment.Center
                ) {
                    val canvasWidth = constraints.maxWidth.toFloat()
                    val canvasHeight = constraints.maxHeight.toFloat()
                    val cropDiameter = min(canvasWidth, canvasHeight) * 0.76f
                    val cropRadius = cropDiameter / 2f
                    val cropCenter = Offset(canvasWidth / 2f, canvasHeight / 2f)

                    if (isLoading || sourceBitmap == null) {
                        CircularProgressIndicator(
                            color = Color(0xFF10B981),
                            modifier = Modifier.size(44.dp)
                        )
                    } else {
                        val bitmap = sourceBitmap!!
                        val imageBitmap = remember(bitmap, rotationDegrees) {
                            if (rotationDegrees != 0) {
                                val matrix = Matrix().apply { postRotate(rotationDegrees.toFloat()) }
                                Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true).asImageBitmap()
                            } else {
                                bitmap.asImageBitmap()
                            }
                        }

                        // Interactive transform Canvas (Gesture drag & pinch)
                        Canvas(
                            modifier = Modifier
                                .fillMaxSize()
                                .pointerInput(imageBitmap) {
                                    detectTransformGestures { _, pan, zoom, _ ->
                                        scale = (scale * zoom).coerceIn(0.6f, 4.5f)
                                        offsetX += pan.x
                                        offsetY += pan.y
                                    }
                                }
                        ) {
                            val w = size.width
                            val h = size.height
                            val center = Offset(w / 2f, h / 2f)

                            // 1. Draw transformed image inside viewport
                            val baseScale = max(cropDiameter / imageBitmap.width, cropDiameter / imageBitmap.height)
                            val currentScale = baseScale * scale

                            val scaledWidth = imageBitmap.width * currentScale
                            val scaledHeight = imageBitmap.height * currentScale

                            val drawLeft = center.x - (scaledWidth / 2f) + offsetX
                            val drawTop = center.y - (scaledHeight / 2f) + offsetY

                            // Draw image
                            drawImage(
                                image = imageBitmap,
                                dstOffset = androidx.compose.ui.unit.IntOffset(drawLeft.toInt(), drawTop.toInt()),
                                dstSize = androidx.compose.ui.unit.IntSize(scaledWidth.toInt(), scaledHeight.toInt())
                            )

                            // 2. Draw Dark Mask with Circular Cutout
                            val circlePath = Path().apply {
                                addOval(Rect(cropCenter, cropRadius))
                            }

                            clipPath(circlePath, clipOp = ClipOp.Difference) {
                                drawRect(color = Color(0xCC000000))
                            }

                            // 3. Draw Circular Framing Border
                            drawCircle(
                                color = Color(0xFF10B981),
                                radius = cropRadius,
                                center = cropCenter,
                                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.5.dp.toPx())
                            )

                            // 4. Subtle Alignment Crosshairs
                            drawLine(
                                color = Color(0x66FFFFFF),
                                start = Offset(cropCenter.x - 14.dp.toPx(), cropCenter.y),
                                end = Offset(cropCenter.x + 14.dp.toPx(), cropCenter.y),
                                strokeWidth = 1.dp.toPx()
                            )
                            drawLine(
                                color = Color(0x66FFFFFF),
                                start = Offset(cropCenter.x, cropCenter.y - 14.dp.toPx()),
                                end = Offset(cropCenter.x, cropCenter.y + 14.dp.toPx()),
                                strokeWidth = 1.dp.toPx()
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Zoom Controls & Reset
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF161922))
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Zoom & Position",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )

                        TextButton(
                            onClick = {
                                scale = 1f
                                offsetX = 0f
                                offsetY = 0f
                                rotationDegrees = 0
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                tint = Color(0xFF9CA3AF),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Reset", color = Color(0xFF9CA3AF), fontSize = 12.sp)
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(
                            onClick = { scale = max(0.6f, scale - 0.2f) },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = "Zoom Out", tint = Color(0xFF9CA3AF))
                        }

                        Slider(
                            value = scale,
                            onValueChange = { scale = it },
                            valueRange = 0.6f..3.5f,
                            modifier = Modifier.weight(1f),
                            colors = SliderDefaults.colors(
                                thumbColor = Color(0xFF10B981),
                                activeTrackColor = Color(0xFF10B981),
                                inactiveTrackColor = Color(0xFF2D323F)
                            )
                        )

                        IconButton(
                            onClick = { scale = min(3.5f, scale + 0.2f) },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Zoom In", tint = Color(0xFF9CA3AF))
                        }
                    }

                    Text(
                        text = "Tip: Drag with one finger to reposition, pinch or use slider to zoom.",
                        color = Color(0xFF6B7280),
                        fontSize = 11.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Bottom Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        enabled = !isSaving,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color(0xFF1A1D24),
                            contentColor = Color.White
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2D323F)),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Text("Cancel", color = Color(0xFFD1D5DB))
                    }

                    Button(
                        onClick = {
                            val bitmap = sourceBitmap ?: return@Button
                            isSaving = true
                            scope.launch {
                                val savedUri = saveCroppedBitmap(
                                    context = context,
                                    source = bitmap,
                                    rotation = rotationDegrees,
                                    scale = scale,
                                    panX = offsetX,
                                    panY = offsetY
                                )
                                isSaving = false
                                if (savedUri != null) {
                                    onCropSuccess(savedUri)
                                } else {
                                    onDismiss()
                                }
                            }
                        },
                        enabled = !isSaving && sourceBitmap != null,
                        modifier = Modifier
                            .weight(1.5f)
                            .height(48.dp)
                            .testTag("save_cropped_photo_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF00A86B)
                        ),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(
                                color = Color.White,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                Text("Crop & Save", color = Color.White, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Exports high-resolution cropped bitmap to application private storage.
 */
private suspend fun saveCroppedBitmap(
    context: Context,
    source: Bitmap,
    rotation: Int,
    scale: Float,
    panX: Float,
    panY: Float
): String? = withContext(Dispatchers.IO) {
    try {
        val targetSize = 600 // Output avatar size 600x600 px

        // 1. Rotate source if needed
        val rotatedSource = if (rotation != 0) {
            val mat = Matrix().apply { postRotate(rotation.toFloat()) }
            Bitmap.createBitmap(source, 0, 0, source.width, source.height, mat, true)
        } else {
            source
        }

        // 2. Compute sampling
        val baseScale = max(targetSize.toFloat() / rotatedSource.width, targetSize.toFloat() / rotatedSource.height)
        val totalScale = baseScale * scale

        val outputBitmap = Bitmap.createBitmap(targetSize, targetSize, Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(outputBitmap)

        val matrix = Matrix().apply {
            postScale(totalScale, totalScale)
            val scaledW = rotatedSource.width * totalScale
            val scaledH = rotatedSource.height * totalScale
            val transX = (targetSize - scaledW) / 2f + (panX * (targetSize.toFloat() / 320f))
            val transY = (targetSize - scaledH) / 2f + (panY * (targetSize.toFloat() / 320f))
            postTranslate(transX, transY)
        }

        val paint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG or android.graphics.Paint.FILTER_BITMAP_FLAG)
        canvas.drawBitmap(rotatedSource, matrix, paint)

        // 3. Save to app private cache directory
        val fileName = "avatar_cropped_${System.currentTimeMillis()}.jpg"
        val outputFile = File(context.filesDir, fileName)
        val fos = FileOutputStream(outputFile)
        outputBitmap.compress(Bitmap.CompressFormat.JPEG, 92, fos)
        fos.flush()
        fos.close()

        outputFile.absolutePath
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

/**
 * Launcher helper for zero-permission Android Photo Picker.
 */
@Composable
fun rememberProfilePhotoPickerLauncher(
    onPhotoSelected: (Uri) -> Unit
) = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia()
) { uri: Uri? ->
    if (uri != null) {
        onPhotoSelected(uri)
    }
}
