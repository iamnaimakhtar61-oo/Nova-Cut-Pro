package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.Compare
import androidx.compose.material.icons.filled.FitScreen
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AspectRatio
import com.example.data.model.AutoCaptionItem
import com.example.data.model.CaptionStylePreset
import com.example.data.model.ClipItem
import com.example.data.model.ColorGradingConfig
import com.example.data.model.EffectType
import com.example.data.model.KeyframeItem
import com.example.data.model.Project
import com.example.data.model.TextOverlayConfig
import com.example.ui.theme.NovaAmber
import com.example.ui.theme.NovaBorder
import com.example.ui.theme.NovaCyan
import com.example.ui.theme.NovaDarkBg
import com.example.ui.theme.NovaGreen
import com.example.ui.theme.NovaPanelAlt
import com.example.ui.theme.NovaPanelBg
import com.example.ui.theme.NovaPink
import com.example.ui.theme.NovaTextPrimary
import com.example.ui.theme.NovaTextSecondary
import com.example.ui.viewmodel.EditorViewModel
import kotlin.math.sin

@Composable
fun VideoPreviewCanvas(
    viewModel: EditorViewModel,
    project: Project,
    playheadMs: Long,
    modifier: Modifier = Modifier
) {
    val activeClip = viewModel.getActiveMainVideoClip()
    val activePip = viewModel.getActivePipClip()
    val activeText = viewModel.getActiveTextClip()

    var interactivePanX by remember { mutableFloatStateOf(0f) }
    var interactivePanY by remember { mutableFloatStateOf(0f) }
    var interactiveZoom by remember { mutableFloatStateOf(1f) }
    var interactiveRotation by remember { mutableFloatStateOf(0f) }

    var showSafeZone by remember { mutableStateOf(false) }
    var showBeforeAfterSplit by remember { mutableStateOf(false) }
    var splitSliderPos by remember { mutableFloatStateOf(0.5f) }

    // Interpolate keyframes for the active clip at playheadMs
    val keyframe = if (activeClip != null) {
        viewModel.interpolateKeyframeAt(activeClip, playheadMs)
    } else {
        KeyframeItem("default", playheadMs)
    }

    val grading = activeClip?.colorGrading ?: ColorGradingConfig()
    val effects = activeClip?.effects ?: emptyList()

    // Smooth motion pulse simulation for video playback
    val infiniteTransition = rememberInfiniteTransition(label = "playback_anim")
    val pulsePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.283f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(NovaDarkBg),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Canvas Controls Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Aspect Ratio badge
            Surface(
                color = NovaPanelAlt,
                shape = RoundedCornerShape(16.dp),
                border = Stroke(1f).let { androidx.compose.foundation.BorderStroke(1.dp, NovaBorder) }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AspectRatio,
                        contentDescription = "Aspect Ratio",
                        tint = NovaCyan,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = " ${project.aspectRatio.label} (${project.aspectRatio.platformHint.take(14)})",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = NovaTextPrimary
                    )
                }
            }

            // Quick Toolbar icons
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Safe Zone Overlay Toggle
                IconButton(
                    onClick = { showSafeZone = !showSafeZone },
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("btn_safe_zone")
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "Safe Zone Guides",
                        tint = if (showSafeZone) NovaCyan else NovaTextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Before/After Split Comparison Toggle
                IconButton(
                    onClick = { showBeforeAfterSplit = !showBeforeAfterSplit },
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("btn_before_after")
                ) {
                    Icon(
                        imageVector = Icons.Default.Compare,
                        contentDescription = "Before After Split",
                        tint = if (showBeforeAfterSplit) NovaAmber else NovaTextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Reset canvas transform button if altered
                if (interactivePanX != 0f || interactivePanY != 0f || interactiveZoom != 1f || interactiveRotation != 0f) {
                    IconButton(
                        onClick = {
                            interactivePanX = 0f
                            interactivePanY = 0f
                            interactiveZoom = 1f
                            interactiveRotation = 0f
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FitScreen,
                            contentDescription = "Reset Framing",
                            tint = NovaPink,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // Before/After comparison slider bar if enabled
        if (showBeforeAfterSplit) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "RAW",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = NovaTextSecondary
                )
                Slider(
                    value = splitSliderPos,
                    onValueChange = { splitSliderPos = it },
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp)
                        .height(24.dp)
                        .testTag("slider_before_after"),
                    colors = SliderDefaults.colors(
                        thumbColor = NovaAmber,
                        activeTrackColor = NovaAmber,
                        inactiveTrackColor = NovaBorder
                    )
                )
                Text(
                    text = "GRADED",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = NovaAmber
                )
            }
        }

        // Main Viewport Box with Aspect Ratio constraint
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false)
                .padding(horizontal = 16.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            val maxH = maxHeight
            val maxW = maxWidth
            val targetRatio = project.aspectRatio.ratioFloat

            // Calculate bounding box that fits within available width/height while preserving aspect ratio
            val (viewportWidth, viewportHeight) = remember(maxW, maxH, targetRatio) {
                val candidateW = maxH * targetRatio
                if (candidateW <= maxW) {
                    Pair(candidateW, maxH)
                } else {
                    Pair(maxW, maxW / targetRatio)
                }
            }

            Box(
                modifier = Modifier
                    .width(viewportWidth)
                    .height(viewportHeight)
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.5.dp, NovaBorder, RoundedCornerShape(8.dp))
                    .background(Color.Black)
                    .pointerInput(Unit) {
                        detectTransformGestures { _, pan, zoom, rotation ->
                            interactivePanX = (interactivePanX + pan.x).coerceIn(-300f, 300f)
                            interactivePanY = (interactivePanY + pan.y).coerceIn(-300f, 300f)
                            interactiveZoom = (interactiveZoom * zoom).coerceIn(0.5f, 3.5f)
                            interactiveRotation = (interactiveRotation + rotation).coerceIn(-180f, 180f)

                            // Also sync back to keyframes if in keyframing mode
                            viewModel.updateKeyframeValues(
                                scale = keyframe.scale * interactiveZoom,
                                rotation = keyframe.rotation + interactiveRotation
                            )
                        }
                    }
                    .testTag("video_preview_canvas")
            ) {
                // Video Layer Drawing via Canvas
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val canvasWidth = size.width
                    val canvasHeight = size.height

                    val finalScale = (keyframe.scale * interactiveZoom).coerceAtLeast(0.1f)
                    val finalRot = keyframe.rotation + interactiveRotation
                    val finalX = (keyframe.posX * canvasWidth) + interactivePanX
                    val finalY = (keyframe.posY * canvasHeight) + interactivePanY

                    // Draw Main Video Footage Simulation
                    scale(finalScale, Offset(canvasWidth / 2f + finalX, canvasHeight / 2f + finalY)) {
                        rotate(finalRot, Offset(canvasWidth / 2f + finalX, canvasHeight / 2f + finalY)) {
                            translate(finalX, finalY) {
                                drawSimulatedVideoClip(
                                    clip = activeClip,
                                    playheadMs = playheadMs,
                                    pulsePhase = pulsePhase,
                                    grading = grading,
                                    effects = effects,
                                    beforeAfterSplit = if (showBeforeAfterSplit) splitSliderPos else 1f
                                )
                            }
                        }
                    }

                    // Draw PIP Overlay Clip if present
                    if (activePip != null) {
                        val pipKf = viewModel.interpolateKeyframeAt(activePip, playheadMs)
                        val pipW = canvasWidth * 0.38f * pipKf.scale
                        val pipH = canvasHeight * 0.28f * pipKf.scale
                        val pipX = (canvasWidth / 2f) + (pipKf.posX * canvasWidth) - (pipW / 2f)
                        val pipY = (canvasHeight / 2f) + (pipKf.posY * canvasHeight) - (pipH / 2f)

                        drawRoundRect(
                            color = Color(0xFF1E1B4B),
                            topLeft = Offset(pipX, pipY),
                            size = Size(pipW, pipH),
                            cornerRadius = CornerRadius(12f, 12f)
                        )
                        drawRoundRect(
                            brush = Brush.linearGradient(
                                colors = listOf(Color(0xFF8A2BE2), Color(0xFF00E5FF))
                            ),
                            topLeft = Offset(pipX, pipY),
                            size = Size(pipW, pipH),
                            cornerRadius = CornerRadius(12f, 12f),
                            style = Stroke(width = 3f)
                        )
                    }

                    // Safe Area overlay guide lines
                    if (showSafeZone) {
                        drawSocialSafeZones(project.aspectRatio)
                    }
                }

                // Render Overlays: Animated Captions & Motion Graphic Titles
                if (activeText != null) {
                    val activeCaption = activeText.captions.firstOrNull {
                        playheadMs >= it.startMs && playheadMs < it.endMs
                    }
                    val textConfig = activeText.textOverlay ?: TextOverlayConfig()
                    val displayText = activeCaption?.text ?: textConfig.text

                    if (displayText.isNotBlank()) {
                        SocialCaptionView(
                            text = displayText,
                            stylePreset = textConfig.stylePreset,
                            isHook = activeCaption?.isHookOrPunchline ?: false,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .offset(
                                    x = 0.dp,
                                    y = (viewportHeight * (textConfig.posY - 0.5f))
                                )
                                .padding(horizontal = 16.dp)
                        )
                    }
                }

                // Active LUT & Effect watermark badges in top-left
                Column(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                ) {
                    if (grading.lut != com.example.data.model.LutPreset.NONE) {
                        Surface(
                            color = Color(0x99000000),
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.padding(bottom = 4.dp)
                        ) {
                            Text(
                                text = "LUT: ${grading.lut.title}",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = NovaCyan,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    effects.forEach { fx ->
                        Surface(
                            color = Color(0x99FF2A6D),
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.padding(bottom = 2.dp)
                        ) {
                            Text(
                                text = fx.type.label,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Renders high-fidelity animated cinematic video simulation with color grading shaders
 */
private fun DrawScope.drawSimulatedVideoClip(
    clip: ClipItem?,
    playheadMs: Long,
    pulsePhase: Float,
    grading: ColorGradingConfig,
    effects: List<com.example.data.model.EffectItem>,
    beforeAfterSplit: Float
) {
    val w = size.width
    val h = size.height

    // Dynamic gradient colors reflecting the clip's theme & motion
    val baseTheme = clip?.visualTheme ?: "cyberpunk"
    val (color1, color2, color3) = when (baseTheme) {
        "stock_cyber" -> Triple(
            Color(0xFF0F172A),
            Color(0xFF311042),
            Color(0xFF051C3B)
        )
        "stock_skate" -> Triple(
            Color(0xFF2E1065),
            Color(0xFF7C2D12),
            Color(0xFF451A03)
        )
        "stock_gym" -> Triple(
            Color(0xFF111827),
            Color(0xFF1E3A8A),
            Color(0xFF064E3B)
        )
        "stock_tech" -> Triple(
            Color(0xFF18181B),
            Color(0xFF27272A),
            Color(0xFF09090B)
        )
        else -> Triple(
            Color(0xFF172554),
            Color(0xFF581C87),
            Color(0xFF1E1B4B)
        )
    }

    // Motion offset driven by playhead time + sine wave
    val timeFactor = (playheadMs % 10_000L) / 10_000f
    val motionX = sin(pulsePhase + timeFactor * 6f) * 40f
    val motionY = sin(pulsePhase * 0.7f) * 30f

    // 1. Draw raw background
    drawRect(
        brush = Brush.radialGradient(
            colors = listOf(color2, color1, color3),
            center = Offset(w / 2f + motionX, h / 2f + motionY),
            radius = w * 0.85f
        )
    )

    // 2. Draw cinematic graphic elements (city lights, skate silhouettes, tech lines)
    when (baseTheme) {
        "stock_cyber" -> {
            // Neon grid lines
            for (i in 0..8) {
                val yLine = (h * 0.5f) + (i * 28f)
                drawLine(
                    color = Color(0x3300E5FF),
                    start = Offset(0f, yLine),
                    end = Offset(w, yLine),
                    strokeWidth = 1.5f
                )
            }
            // Glowing neon sun/orb
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFFF2A6D), Color(0x00FF2A6D)),
                    center = Offset(w / 2f + motionX * 0.3f, h * 0.45f),
                    radius = w * 0.32f
                ),
                radius = w * 0.32f,
                center = Offset(w / 2f + motionX * 0.3f, h * 0.45f)
            )
        }
        "stock_skate" -> {
            // Sunset lens flare and sun
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFFFD166), Color(0xFFFF6B35), Color(0x00FF6B35)),
                    center = Offset(w * 0.65f, h * 0.35f + motionY * 0.2f),
                    radius = w * 0.4f
                ),
                radius = w * 0.4f,
                center = Offset(w * 0.65f, h * 0.35f + motionY * 0.2f)
            )
        }
        else -> {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0x5500E5FF), Color(0x0000E5FF)),
                    center = Offset(w * 0.5f + motionX, h * 0.5f + motionY),
                    radius = w * 0.45f
                ),
                radius = w * 0.45f,
                center = Offset(w * 0.5f + motionX, h * 0.5f + motionY)
            )
        }
    }

    // 3. Color Grading Simulation Layer (respecting Before/After Split)
    val splitWidth = w * beforeAfterSplit.coerceIn(0f, 1f)

    if (splitWidth > 0f) {
        // Apply Temperature Shift (Amber Warmth or Cool Cyan)
        if (grading.temperature > 0.05f) {
            val amberAlpha = (grading.temperature * 0.22f).coerceIn(0f, 0.4f)
            drawRect(
                color = Color(0xFFFF9E00).copy(alpha = amberAlpha),
                size = Size(splitWidth, h)
            )
        } else if (grading.temperature < -0.05f) {
            val blueAlpha = (-grading.temperature * 0.22f).coerceIn(0f, 0.4f)
            drawRect(
                color = Color(0xFF00B4D8).copy(alpha = blueAlpha),
                size = Size(splitWidth, h)
            )
        }

        // Apply Tint (Magenta vs Green)
        if (grading.tint > 0.05f) {
            drawRect(
                color = Color(0xFFFF007F).copy(alpha = grading.tint * 0.15f),
                size = Size(splitWidth, h)
            )
        }

        // Apply LUT Tint Filter
        if (grading.lut != com.example.data.model.LutPreset.NONE) {
            val lutColor = Color(grading.lut.previewTint)
            drawRect(
                color = lutColor,
                size = Size(splitWidth, h)
            )
        }

        // Apply Contrast Boost (Darken edges or pop highlights)
        if (grading.contrast > 0.1f) {
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(Color.Transparent, Color(0x44000000)),
                    radius = w * 0.7f
                ),
                size = Size(splitWidth, h)
            )
        }

        // Vignette Ring
        val vignetteStrength = (grading.vignette * 0.6f).coerceIn(0f, 0.8f)
        if (vignetteStrength > 0.05f) {
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = vignetteStrength)),
                    radius = w * 0.7f
                ),
                size = Size(splitWidth, h)
            )
        }
    }

    // 4. Effects Simulation
    effects.forEach { fx ->
        when (fx.type) {
            EffectType.GLITCH -> {
                // Glitch scan bars
                val barY = (playheadMs % 1200L) / 1200f * h
                drawRect(
                    color = Color(0x6600E5FF),
                    topLeft = Offset(0f, barY),
                    size = Size(w, 14f)
                )
                drawRect(
                    color = Color(0x66FF2A6D),
                    topLeft = Offset(0f, (barY + 22f) % h),
                    size = Size(w, 8f)
                )
            }
            EffectType.RGB_SPLIT -> {
                // Chromatic aberration border edge
                drawRect(
                    color = Color(0x2200E5FF),
                    topLeft = Offset(-4f, 0f),
                    size = Size(w, h)
                )
                drawRect(
                    color = Color(0x22FF2A6D),
                    topLeft = Offset(4f, 0f),
                    size = Size(w, h)
                )
            }
            EffectType.VHS_RETRO -> {
                // Scanlines every 6px
                for (y in 0 until h.toInt() step 6) {
                    drawLine(
                        color = Color(0x18000000),
                        start = Offset(0f, y.toFloat()),
                        end = Offset(w, y.toFloat()),
                        strokeWidth = 1f
                    )
                }
            }
            EffectType.LIGHT_LEAK -> {
                // Dreamy warm light leak on corner
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0x88FFB703), Color(0x00FFB703)),
                        center = Offset(0f, 0f),
                        radius = w * 0.6f
                    ),
                    radius = w * 0.6f,
                    center = Offset(0f, 0f)
                )
            }
            else -> {}
        }
    }

    // If Before/After split is active, draw the dividing vertical line
    if (beforeAfterSplit < 0.99f && beforeAfterSplit > 0.01f) {
        drawLine(
            color = Color.White,
            start = Offset(splitWidth, 0f),
            end = Offset(splitWidth, h),
            strokeWidth = 2.5f
        )
        drawCircle(
            color = Color.White,
            radius = 12f,
            center = Offset(splitWidth, h / 2f)
        )
    }
}

/**
 * Draws social media safe zone guides (TikTok UI: right buttons, bottom caption)
 */
private fun DrawScope.drawSocialSafeZones(aspectRatio: AspectRatio) {
    if (aspectRatio != AspectRatio.RATIO_9_16) return

    val w = size.width
    val h = size.height
    val dashEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)

    // Top safe zone margin (sound / search header)
    drawLine(
        color = Color(0x8800E5FF),
        start = Offset(0f, h * 0.12f),
        end = Offset(w, h * 0.12f),
        strokeWidth = 1.5f,
        pathEffect = dashEffect
    )

    // Bottom safe zone (captions & sound pill)
    drawLine(
        color = Color(0x8800E5FF),
        start = Offset(0f, h * 0.78f),
        end = Offset(w, h * 0.78f),
        strokeWidth = 1.5f,
        pathEffect = dashEffect
    )

    // Right safe zone (avatar, like, comment, share icons column)
    drawLine(
        color = Color(0x88FF2A6D),
        start = Offset(w * 0.82f, h * 0.25f),
        end = Offset(w * 0.82f, h * 0.78f),
        strokeWidth = 1.5f,
        pathEffect = dashEffect
    )
}

@Composable
private fun SocialCaptionView(
    text: String,
    stylePreset: CaptionStylePreset,
    isHook: Boolean,
    modifier: Modifier = Modifier
) {
    when (stylePreset) {
        CaptionStylePreset.HORMOZI -> {
            Surface(
                color = if (isHook) Color(0xFFFFD700) else Color(0xCC000000),
                shape = RoundedCornerShape(6.dp),
                modifier = modifier.shadow(8.dp, RoundedCornerShape(6.dp))
            ) {
                Text(
                    text = text.uppercase(),
                    fontSize = if (isHook) 20.sp else 18.sp,
                    fontWeight = FontWeight.Black,
                    color = if (isHook) Color.Black else Color.White,
                    letterSpacing = 0.5.sp,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
        CaptionStylePreset.KARAOKE_BOUNCE -> {
            Surface(
                color = Color.Transparent,
                modifier = modifier
            ) {
                Text(
                    text = text,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isHook) NovaGreen else NovaCyan,
                    modifier = Modifier
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                        .drawBehind {
                            drawCircle(
                                color = if (isHook) Color(0x4400F59B) else Color(0x4400E5FF),
                                radius = size.maxDimension * 0.6f
                            )
                        },
                    textAlign = TextAlign.Center
                )
            }
        }
        CaptionStylePreset.MINIMAL_PILL -> {
            Surface(
                color = Color(0xD9141620),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x33FFFFFF)),
                modifier = modifier
            ) {
                Text(
                    text = text,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
        CaptionStylePreset.NEON_CYBER -> {
            Surface(
                color = Color(0xBB000000),
                shape = RoundedCornerShape(4.dp),
                border = androidx.compose.foundation.BorderStroke(2.dp, if (isHook) NovaPink else NovaCyan),
                modifier = modifier
            ) {
                Text(
                    text = text.uppercase(),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = if (isHook) NovaPink else NovaCyan,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
